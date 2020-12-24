package com.ht.project.snsproject.service;

import com.ht.project.snsproject.exception.FileIOException;
import com.ht.project.snsproject.model.feed.FileDelete;
import com.ht.project.snsproject.model.feed.FileDto;
import com.ht.project.snsproject.model.feed.FileInfo;
import com.ht.project.snsproject.repository.file.FileRepository;
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
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Qualifier("localFileService")
@RequiredArgsConstructor
public class FileServiceLocal implements FileService {

  private final FileRepository fileRepository;

  @Value("${file.local.path}")
  private String localPath;

  private static final Logger logger = LoggerFactory.getLogger(FileServiceLocal.class);

  /** SimpleDateFormat 타입은 쓰레드에 안전하지 않은 객체이다.
   * Java 8 버전 부터는 쓰레드에 안전한 LocalDateTime 객체와 DateTimeFormatter 객체를 지원한다.
   * DateTimeFormatter 객체는 SimpleDateFormat 객체보다 처리가 빠르다.
   * 그러므로 LocalDateTime, DateTimeFormatter 두 객체를 조합하여 사용하면
   * 쓰레드에 안전하고 빠른 원하는 포맷의 날짜 객체를 사용할 수 있다.
   */

  @Override
  public void uploadFiles(List<MultipartFile> files, String dirPath, File destDir) {

    files.forEach(file -> {
      try {
        file.transferTo(new File(destDir.getPath() + File.separator + file.getOriginalFilename()));

      } catch (IOException ioe) {

        deleteDirectory(destDir);
        throw new FileIOException("파일 업로드에 실패하였습니다.", ioe);
      }
    });
  }

  private void deleteDirectory(File directory) {

    File[] files = directory.listFiles();

    if (files != null) {

      Arrays.stream(files).forEach(File::delete);

      if (directory.isDirectory()) {
        directory.delete();
      }
    }
  }

  @Override
  public void insertFileInfoList(List<FileDto> fileDtoList, String dirPath, int feedId) {

    List<FileInfo> fileInfoList = new ArrayList<>();
    makeFileInfoList(fileInfoList, fileDtoList, feedId, dirPath);

    if(!fileInfoList.isEmpty()) {
      fileRepository.insertFileInfoList(fileInfoList);
    }
  }

  private void makeFileInfoList(List<FileInfo> fileInfoList, List<FileDto> fileDtoList,
                                int feedId, String dirPath) {

    if (fileDtoList != null) {
      fileDtoList.forEach(fileDto ->
              fileInfoList.add(new FileInfo(dirPath, fileDto.getFileName(), fileDto.getFileIndex(), feedId)));
    }
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
    fileRepository.deleteFiles(fileDeleteList);
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

  @Override
  public void updateFiles(List<MultipartFile> files, List<FileDto> fileDtoList, String userId, int feedId) {

    String filePath = fileRepository.findFilePathByFeedId(feedId);
    List<String> originalFiles = fileRepository.findFileNamesByFeedId(feedId);

    if (filePath == null) {
      filePath = userId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    File destDir = new File(localPath + filePath);

    if(!destDir.exists()) { destDir.mkdirs(); }

    uploadFiles(classifyFiles(files, originalFiles), filePath, destDir);

    if (!originalFiles.isEmpty()) { deleteFiles(feedId, filePath, originalFiles); }

    upsertFileInfoList(fileDtoList, filePath, feedId);
  }

  @Transactional
  private List<MultipartFile> classifyFiles(List<MultipartFile> files,
                                            List<String> originalFiles) {

    if (files.isEmpty()) { return files; }

    if (originalFiles.isEmpty()) { return files; }

    List<MultipartFile> uploadingFiles = new ArrayList<>();

    files.forEach(file -> {
      String fileName = file.getOriginalFilename();
      if (!originalFiles.contains(fileName)) {
        uploadingFiles.add(file);
      } else {
        originalFiles.remove(fileName);
      }
    });

    return uploadingFiles;
  }
  private void upsertFileInfoList(List<FileDto> fileDtoList, String dirPath, int feedId) {

    List<FileInfo> fileInfoList = new ArrayList<>();
    makeFileInfoList(fileInfoList, fileDtoList, feedId, dirPath);

    if (!fileInfoList.isEmpty()) { fileRepository.upsertFiles(fileInfoList); }

  }

  @Transactional
  @Override
  public void deleteFiles(int feedId) {

    String path = fileRepository.findFilePathByFeedId(feedId);

    if (path != null) {

      deleteDirectory(new File(path));
    }

    fileRepository.deleteFile(feedId);
  }
}
