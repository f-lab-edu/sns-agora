package com.ht.project.snsproject.service;

import com.ht.project.snsproject.exception.FileIOException;
import com.ht.project.snsproject.mapper.FileMapper;
import com.ht.project.snsproject.model.feed.FileAdd;
import com.ht.project.snsproject.model.feed.FileDelete;
import com.ht.project.snsproject.model.feed.ProfileImage;
import com.ht.project.snsproject.model.feed.FileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Qualifier("localFileService")
@RequiredArgsConstructor
public class FileServiceLocal implements FileService {

  private final FileMapper fileMapper;

  @Value("${file.windows.path}")
  private String localPath;

  private static final Logger logger = LoggerFactory.getLogger(FileServiceLocal.class);

  /** SimpleDateFormat 타입은 쓰레드에 안전하지 않은 객체이다.
   * Java 8 버전 부터는 쓰레드에 안전한 LocalDateTime 객체와 DateTimeFormatter 객체를 지원한다.
   * DateTimeFormatter 객체는 SimpleDateFormat 객체보다 처리가 빠르다.
   * 그러므로 LocalDateTime, DateTimeFormatter 두 객체를 조합하여 사용하면
   * 쓰레드에 안전하고 빠른 원하는 포맷의 날짜 객체를 사용할 수 있다.
   */

  @Override
  @Transactional
  public void fileUpload(List<MultipartFile> files, String userId, int feedId) {

    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String dirPath = localPath + userId + File.separator + time;
    int fileIndex = 1;// file 순서를 정해줄 index
    List<FileInfo> fileInfoList = new ArrayList<>();

    try {
      File destDir = new File(dirPath);

      if (!destDir.exists()) {
        destDir.mkdirs();
      }

      for (MultipartFile file:files) {
        String originalFileName = file.getOriginalFilename();
        String filePath = dirPath + File.separator + originalFileName;
        File destFile = new File(filePath);

        fileInfoList.add(new FileInfo(dirPath, originalFileName, fileIndex, feedId));
        file.transferTo(destFile);

        fileIndex++;
      }
      fileMapper.fileListUpload(fileInfoList);
    } catch (IOException ioe) {
      throw new FileIOException("파일 업로드에 실패하였습니다.", ioe);
    }
  }

  private void fileUpload(MultipartFile file, String dirPath) {

    File destDir = new File(dirPath);

    if(!destDir.exists()) {
      destDir.mkdirs();
    }

    String originalFileName = file.getOriginalFilename();
    String filePath = dirPath + File.separator + originalFileName;

    File destFile = new File(filePath);

    try{

      file.transferTo(destFile);
    } catch (IOException ioe) {
      throw new FileIOException("파일 업로드에 실패하였습니다.", ioe);
    }
  }

  @Override
  public void fileUploadForFeed(MultipartFile file, String userId, int feedId) {

    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String dirPath = localPath + userId + File.separator + time;

    fileUpload(file, dirPath);
    fileMapper.fileUpload(new FileInfo(dirPath, file.getOriginalFilename(), 1, feedId));
  }

  @Override
  public ProfileImage fileUploadForProfile(MultipartFile file, String userId) {

    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String dirPath = localPath + userId + File.separator + time;

    fileUpload(file, dirPath);

    return new ProfileImage(dirPath, file.getOriginalFilename());
  }

  @Transactional
  public void deleteFiles(int feedId, String path, List<String> fileNames) {

    List<FileDelete> fileDeleteList = new ArrayList<>();

    if (path != null) {
      for (String fileName : fileNames) {

        fileDeleteList.add(FileDelete.create(feedId,fileName));
        File file = new File(path + File.separator + fileName);
        file.delete();
      }
    }
    fileMapper.deleteFiles(fileDeleteList);
  }

  @Override
  public void deleteFile(String filePath, String fileName) {

    try {

      File file = new File(filePath + File.separator + fileName);
      file.delete();
    } catch (Exception e) {
      throw new FileIOException("파일 삭제에 실패하였습니다.", e);
    }
  }


  @Transactional
  public List<FileInfo> addFiles(int feedId, String path, List<FileAdd> fileAddList) {

    List<FileInfo> fileInfoList = new ArrayList<>();

    try {
      for (FileAdd fileAdd : fileAddList) {
        MultipartFile file = fileAdd.getFile();
        String originalFileName = file.getOriginalFilename();
        String filePath = path + File.separator + originalFileName;
        File destFile = new File(filePath);
        int fileIndex = fileAdd.getFileIndex();

        fileInfoList.add(new FileInfo(path, originalFileName, fileIndex, feedId));
        file.transferTo(destFile);
      }
      return fileInfoList;
    } catch (IOException ioe) {
      throw new FileIOException("파일 업로드에 실패하였습니다.", ioe);
    }
  }

  @Transactional
  @Override
  public void deleteAllFiles(int feedId) {

    String path = fileMapper.getFilePath(feedId);
    if (path != null) {
      File dir = new File(path);
      if (dir.exists()) {
        File[] files = dir.listFiles();
        if (files != null) {
          for (File file : files) {
            file.delete();
          }
        }
        dir.delete();
      }
    }

    fileMapper.deleteFile(feedId);
  }

  @Transactional
  @Override
  public void updateFiles(List<MultipartFile> files, String userId, int feedId) {

    List<String> originFiles = fileMapper.getFileNames(feedId);
    String path = fileMapper.getFilePath(feedId);

    List<FileAdd> uploadFiles = new ArrayList<>();
    List<FileInfo> fileInfoList = new ArrayList<>();

    int fileIndex = 0;

    if (originFiles.isEmpty()) {
      fileUpload(files,userId,feedId);
      return;
    }

    if (!files.isEmpty()) {
      for (MultipartFile file : files) {
        String fileName = file.getOriginalFilename();
        ++fileIndex;
        if (originFiles.contains(fileName)) {
          fileInfoList.add(new FileInfo(path,fileName,fileIndex,feedId));
          originFiles.remove(fileName);
        } else {
          uploadFiles.add(new FileAdd(fileIndex, file));
        }
      }
    } else {
      deleteAllFiles(feedId);
      return;
    }

    if (!uploadFiles.isEmpty()) {
      fileInfoList.addAll(addFiles(feedId, path, uploadFiles));
    }

    if (!originFiles.isEmpty()) {
      deleteFiles(feedId, path, originFiles);
    }

    fileMapper.upsertFiles(fileInfoList);
  }
}
