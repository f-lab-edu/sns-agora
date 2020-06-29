package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.feed.FileAdd;
import com.ht.project.snsproject.model.feed.FileForProfile;
import com.ht.project.snsproject.model.feed.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

  void fileUpload(List<MultipartFile> files, String userId, int feedId);

  void fileUploadForFeed(MultipartFile file, String userId, int feedId);

  FileForProfile fileUploadForProfile(MultipartFile file, String userId);

  void deleteAllFiles(int feedId);

  void deleteFiles(int feedId, String path, List<String> fileNames);

  void deleteFile(String filePath, String fileName);

  List<FileInfo> addFiles(int feedId, String path, List<FileAdd> fileAddList);

  void updateFiles(List<MultipartFile> files, String userId, int feedId);
}
