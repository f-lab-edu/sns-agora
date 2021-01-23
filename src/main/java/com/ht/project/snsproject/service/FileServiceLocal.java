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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Qualifier("localFileService")
@RequiredArgsConstructor
public class FileServiceLocal implements FileService {

  private final FileRepository fileRepository;

  @Value("${file.upload-path}")
  private String localPath;

  private static final Logger logger = LoggerFactory.getLogger(FileServiceLocal.class);

  /** SimpleDateFormat 타입은 쓰레드에 안전하지 않은 객체이다.
   * Java 8 버전 부터는 쓰레드에 안전한 LocalDateTime 객체와 DateTimeFormatter 객체를 지원한다.
   * DateTimeFormatter 객체는 SimpleDateFormat 객체보다 처리가 빠르다.
   * 그러므로 LocalDateTime, DateTimeFormatter 두 객체를 조합하여 사용하면
   * 쓰레드에 안전하고 빠른 원하는 포맷의 날짜 객체를 사용할 수 있다.
   */

  @Transactional
  @Override
  public void uploadFiles(List<MultipartFile> files, String dirPath) {

    File destDir = new File(localPath + dirPath);

    if (!destDir.exists()) {
      destDir.mkdirs();
    }

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

  @Transactional
  @Override
  public void insertFileInfoList(List<FileDto> fileDtoList, int feedId) {

    List<FileInfo> fileInfoList = new ArrayList<>();
    makeFileInfoList(fileInfoList, fileDtoList, feedId);

    if (!fileInfoList.isEmpty()) {
      fileRepository.insertFileInfoList(fileInfoList);
    }
  }

  private void makeFileInfoList(List<FileInfo> fileInfoList, List<FileDto> fileDtoList,
                                int feedId) {

    if (fileDtoList != null) {
      fileDtoList.forEach(fileDto ->
              fileInfoList.add(new FileInfo(String.valueOf(feedId), fileDto.getFileName(),
                      fileDto.getFileIndex(), feedId)));
    }
  }

  @Transactional
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
  @Override
  public void updateFiles(List<MultipartFile> files, List<FileDto> fileDtoList, int feedId) {

    List<String> originalFiles = fileRepository.findFileNamesByFeedId(feedId);

    String filePath = String.valueOf(feedId);

    uploadFiles(classifyFiles(files, originalFiles), filePath);

    if (!originalFiles.isEmpty()) {
      deleteFiles(feedId, originalFiles);
    }

    upsertFileInfoList(fileDtoList, feedId);
  }

  private void deleteFiles(int feedId, List<String> fileNames) {

    List<FileDelete> fileDeleteList = new ArrayList<>();

    fileNames.forEach(fileName -> {
      fileDeleteList.add(FileDelete.create(feedId, fileName));
      new File(feedId + File.separator + fileName).delete();
    });

    fileRepository.deleteFiles(fileDeleteList);
  }

  @Transactional
  @Override
  public void deleteFiles(int feedId) {

    if (!fileRepository.findFileNamesByFeedId(feedId).isEmpty()) {

      deleteDirectory(new File(String.valueOf(feedId)));
    }

    fileRepository.deleteFile(feedId);
  }

  private List<MultipartFile> classifyFiles(List<MultipartFile> files,
                                            List<String> originalFiles) {

    if (files.isEmpty()) {
      return files;
    }

    if (originalFiles.isEmpty()) {
      return files;
    }

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

  private void upsertFileInfoList(List<FileDto> fileDtoList, int feedId) {

    List<FileInfo> fileInfoList = new ArrayList<>();
    makeFileInfoList(fileInfoList, fileDtoList, feedId);

    if (!fileInfoList.isEmpty()) {
      fileRepository.upsertFiles(fileInfoList);
    }

  }
}
