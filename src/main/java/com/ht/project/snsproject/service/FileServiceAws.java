package com.ht.project.snsproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ht.project.snsproject.exception.FileIOException;
import com.ht.project.snsproject.mapper.FileMapper;
import com.ht.project.snsproject.model.feed.*;
import com.ht.project.snsproject.properites.aws.AwsS3Property;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Qualifier("awsFileService")
@RequiredArgsConstructor
public class FileServiceAws implements FileService {

  private final AmazonS3 s3Client;

  private final AwsS3Property awsS3Property;

  private static final Logger logger = LoggerFactory.getLogger(FileServiceAws.class);

  private final FileMapper fileMapper;

  /** ThreadLocal 객체를 사용하면 SimpleDateFormat 의 Thread Safety 를 보장할 수 있다.
   * ThreadLocal 은 한 쓰레드에서 실행되는 코드가 동일한 객체를 사용할 수 있도록 해 주기 때문에
   * 쓰레드와 관련된 코드에서 파라미터를 사용하지 않고 객체를 전파하기 위한 용도로 주로 사용된다.
   * 톰캣,웹로직등의 웹어플리케이션서버에서 ThreadLocal 을 사용할 경우 로직이 종료되도 GC가 안된다.
   * 어플리케이션 쓰레드 내부에서는 ThreadLocal 객체를 strong reference 하기 때문에
   * 어플리케이션의 lifecycle 이 끝났으면 GC가 되어야 하지만
   * 웹서버 입장에서는 실제 thread 를 생성한 것은 was 이고,
   * 여기서도 ThreadLocal 객체를 참조하고 있기 때문이다.
   * 그렇기 때문에 해당 객체를 사용 후에는 반드시 제거해주어야 한다.
   * 그렇지 않으면 재사용되는 Thread 가 올바르지 않은 데이터를 참조할 수 있으며,
   * 혹은 Garbage 들이 쌓여 Memory Leak 문제가 발생할 수 있다.
   */
  private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {

    @Override
    protected DateFormat initialValue() {
      return new SimpleDateFormat("yyyyMMdd_HHmmss");
    }
  };

  @Override
  @Transactional
  public void fileUpload(List<MultipartFile> files, String userId, int feedId) {

    String time = dateFormat.get().format(new Date());
    String dirPath = userId + File.separator + time;
    ObjectMetadata metadata = new ObjectMetadata();
    int fileIndex = 1;
    List<FileInfo> fileInfoList = new ArrayList<>();

    try {
      for (MultipartFile file : files) {

        metadata.setContentType(MediaType.IMAGE_JPEG_VALUE);
        metadata.setContentLength(file.getSize());
        String keyName = dirPath + File.separator + file.getOriginalFilename();

        fileInfoList.add(new FileInfo(dirPath, file.getOriginalFilename(), fileIndex, feedId));
        s3Client.putObject(awsS3Property.getBucketName(), keyName, file.getInputStream(), metadata);

        fileIndex++;
      }

      fileMapper.fileListUpload(fileInfoList);

    } catch (IOException ioe) {
      throw new FileIOException("파일 업로드에 실패하였습니다.", ioe);
    } finally {
      dateFormat.remove();
    }

  }

  private void fileUpload(MultipartFile file, String dirPath) {

    ObjectMetadata metadata = new ObjectMetadata();

    try {
      metadata.setContentType(MediaType.IMAGE_JPEG_VALUE);
      metadata.setContentLength(file.getSize());
      String keyName = dirPath + File.separator + file.getOriginalFilename();

      s3Client.putObject(awsS3Property.getBucketName(), keyName, file.getInputStream(), metadata);

    } catch (IOException ioe) {
      throw new FileIOException("파일 업로드에 실패하였습니다.", ioe);
    }
  }

  @Override
  public void fileUploadForFeed(MultipartFile file, String userId, int feedId){

    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String dirPath = userId + File.separator + time;

    fileUpload(file, dirPath);
    fileMapper.fileUpload(new FileInfo(dirPath, file.getOriginalFilename(), 1, feedId));
  }

  @Override
  public ProfileImage fileUploadForProfile(MultipartFile file, String userId) {

    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String dirPath = userId + File.separator + time;

    fileUpload(file, dirPath);

    return new ProfileImage(dirPath, file.getOriginalFilename());
  }

  @Transactional
  @Override
  public void deleteAllFiles(int feedId) {

    List<FileVo> files = fileMapper.getFiles(feedId);
    List<DeleteObjectsRequest.KeyVersion> keyVersions = new ArrayList<>();

    if (files != null) {
      for (FileVo file : files) {
        keyVersions.add(new DeleteObjectsRequest.KeyVersion(file.getFilePath()
                + File.separator + file.getFileName()));
      }

      DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(awsS3Property.getBucketName())
              .withKeys(keyVersions);
      s3Client.deleteObjects(deleteObjectsRequest);
    }
    fileMapper.deleteFile(feedId);
  }

  @Override
  public void deleteFile(String filePath, String fileName) {

    try {

      s3Client.deleteObject(new DeleteObjectRequest(awsS3Property.getBucketName(),
              filePath + File.separator + fileName));
    } catch (Exception e) {
      throw new FileIOException("파일 삭제에 실패했습니다.", e);
    }
  }


  @Transactional
  @Override
  public void updateFiles(List<MultipartFile> files, String userId, int feedId) {

    if(files.isEmpty()) {
      deleteAllFiles(feedId);
      return;
    }

    List<FileVo> originalFiles = fileMapper.getFiles(feedId);
    String filePath = originalFiles.get(0).getFilePath();

    if(originalFiles.isEmpty()) {
      fileUpload(files, userId, feedId);
      return;
    }

    List<FileInfo> fileInfoList = new ArrayList<>();
    classifyFiles(feedId, filePath, fileInfoList, files, originalFiles);

    if (!originalFiles.isEmpty()) {
      deleteFiles(feedId, originalFiles);
    }

    fileMapper.upsertFiles(fileInfoList);
  }

  @Transactional
  private void classifyFiles(int feedId, String filePath,
                             List<FileInfo> fileInfoList,
                             List<MultipartFile> files,
                             List<FileVo> originalFiles) {

    List<FileAdd> uploadFiles = new ArrayList<>();
    int fileIndex = 0;

    for (MultipartFile file : files) {

      String fileName = file.getOriginalFilename();
      ++fileIndex;

      boolean isExist = false;
      for(FileVo fileVo : originalFiles) {

        if (fileVo.getFileName().equals(fileName)){
          isExist = true;
          originalFiles.remove(fileVo);
        }
      }

      if(!isExist){
        uploadFiles.add(new FileAdd(fileIndex, file));
      }
      fileInfoList.add(new FileInfo(filePath, fileName, fileIndex, feedId));
    }

    if (!uploadFiles.isEmpty()) {

      fileInfoList.addAll(addFiles(feedId, filePath, uploadFiles));
    }
  }

  @Transactional
  public void deleteFiles(int feedId, List<FileVo> fileList) {

    List<FileDelete> fileDeleteList = new ArrayList<>();
    List<DeleteObjectsRequest.KeyVersion> keyVersionList = new ArrayList<>();

    for (FileVo fileVo : fileList) {
      String fileName = fileVo.getFileName();
      fileDeleteList.add(FileDelete.create(feedId, fileName));
      keyVersionList.add(new DeleteObjectsRequest
              .KeyVersion(fileVo.getFilePath()
              + File.separator + fileName));
    }

    fileMapper.deleteFiles(fileDeleteList);
    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(awsS3Property.getBucketName())
            .withKeys(keyVersionList);
    s3Client.deleteObjects(deleteObjectsRequest);
  }

  @Transactional
  public List<FileInfo> addFiles(int feedId, String filePath, List<FileAdd> fileAddList) {

    List<FileInfo> fileInfoList = new ArrayList<>();
    ObjectMetadata objectMetadata = new ObjectMetadata();

    try {
      for (FileAdd fileAdd : fileAddList) {

        MultipartFile file = fileAdd.getFile();
        String originalFileName = file.getOriginalFilename();
        objectMetadata.setContentType(MediaType.IMAGE_JPEG_VALUE);
        objectMetadata.setContentLength(file.getSize());
        String keyName = filePath + File.separator + originalFileName;
        int fileIndex = fileAdd.getFileIndex();

        fileInfoList.add(new FileInfo(filePath, originalFileName, fileIndex, feedId));
        s3Client.putObject(awsS3Property.getBucketName(), keyName, file.getInputStream(), objectMetadata);
      }

      return fileInfoList;
    } catch (IOException ioe) {
      throw new FileIOException("파일 업로드에 실패하였습니다.", ioe);
    } finally {
      dateFormat.remove();
    }
  }
}
