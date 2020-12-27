package com.ht.project.snsproject.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.ht.project.snsproject.exception.FileIOException;
import com.ht.project.snsproject.model.feed.FileDelete;
import com.ht.project.snsproject.model.feed.FileDto;
import com.ht.project.snsproject.model.feed.FileInfo;
import com.ht.project.snsproject.properites.aws.AwsS3Property;
import com.ht.project.snsproject.repository.file.FileRepository;
import lombok.RequiredArgsConstructor;
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

@Service
@Qualifier("awsFileService")
@RequiredArgsConstructor
public class FileServiceAws implements FileService {

  private final AmazonS3 s3Client;

  private final AwsS3Property awsS3Property;

  private final TransferManager transferManager;

  private final FileRepository fileRepository;

  @Value("${file.upload-path}")
  private String tempPath;

  private static final String S3_SEPARATOR = "/";

  @Transactional
  public void uploadFiles(List<MultipartFile> files, String dirPath) {

    File destDir = new File(tempPath + dirPath);
    if(!destDir.exists()) { destDir.mkdirs(); }
    if (files.isEmpty()) { return; }

    writeTempFiles(files, destDir);
    MultipleFileUpload transfer = transferManager.uploadDirectory(awsS3Property.getBucketName(), dirPath,
            destDir, false);

    try {
      transfer.waitForCompletion();

    } catch (InterruptedException | AmazonClientException e) {

      throw new FileIOException("파일 업로드에 실패했습니다.", e);
    } finally {

      deleteTempDirectory(destDir);
    }
  }

  private void writeTempFiles(List<MultipartFile> files, File destDir) {

    files.forEach(file -> {
      try {
        file.transferTo(new File(destDir.getPath() + File.separator + file.getOriginalFilename()));

      } catch (IOException ioe) {

        deleteTempDirectory(destDir);
        throw new FileIOException("파일 업로드에 실패하였습니다.", ioe);
      }
    });
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
  public void insertFileInfoList(List<FileDto> fileDtoList, int feedId) {

    List<FileInfo> fileInfoList = new ArrayList<>();
    makeFileInfoList(fileInfoList, fileDtoList, feedId);

    if(!fileInfoList.isEmpty()) {

      fileRepository.insertFileInfoList(fileInfoList);
    }

  }

  private void deleteTempDirectory(File directory) {

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
  public void deleteFiles(int feedId) {

    List<String> fileNames = fileRepository.findFileNamesByFeedId(feedId);

    List<DeleteObjectsRequest.KeyVersion> keyVersions = new ArrayList<>();

    if (!fileNames.isEmpty()) {

      fileNames.forEach(fileName -> keyVersions.add(new DeleteObjectsRequest.KeyVersion(feedId
              + S3_SEPARATOR + fileName)));

      s3Client.deleteObjects(new DeleteObjectsRequest(awsS3Property.getBucketName())
              .withKeys(keyVersions));

      fileRepository.deleteFile(feedId);
    }
  }

  @Transactional
  @Override
  public void deleteFile(String filePath, String fileName) {

    try {

      s3Client.deleteObject(new DeleteObjectRequest(awsS3Property.getBucketName(),
              filePath + S3_SEPARATOR + fileName));
    } catch (Exception e) {
      throw new FileIOException("파일 삭제에 실패했습니다.", e);
    }
  }

  @Transactional
  @Override
  public void updateFiles(List<MultipartFile> files, List<FileDto> fileDtoList, int feedId) {

    List<String> originalFiles = fileRepository.findFileNamesByFeedId(feedId);
    String filePath = String.valueOf(feedId);

    uploadFiles(classifyFiles(files, originalFiles), filePath);

    if (!originalFiles.isEmpty()) { deleteFiles(feedId, originalFiles); }

    upsertFileInfoList(fileDtoList, feedId);
  }

  private void deleteFiles(int feedId, List<String> fileNames) {

    List<FileDelete> fileDeleteList = new ArrayList<>();
    List<DeleteObjectsRequest.KeyVersion> keyVersionList = new ArrayList<>();

    fileNames.forEach(fileName -> { fileDeleteList.add(FileDelete.create(feedId, fileName));
      keyVersionList.add(new DeleteObjectsRequest.KeyVersion(feedId + S3_SEPARATOR + fileName)); });

    fileRepository.deleteFiles(fileDeleteList);
    s3Client.deleteObjects(new DeleteObjectsRequest(awsS3Property.getBucketName()).withKeys(keyVersionList));
  }

  private void upsertFileInfoList(List<FileDto> fileDtoList, int feedId) {

    List<FileInfo> fileInfoList = new ArrayList<>();
    makeFileInfoList(fileInfoList, fileDtoList, feedId);

    if (!fileInfoList.isEmpty()) { fileRepository.upsertFiles(fileInfoList); }

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

}
