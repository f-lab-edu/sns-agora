package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.feed.FileForProfile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

  void fileUpload(List<MultipartFile> files, String userId, int feedId);

  void fileUploadForFeed(MultipartFile file, String userId, int feedId);

  FileForProfile fileUploadForProfile(MultipartFile file, String userId);

  void deleteAllFiles(int feedId);

  void deleteFile(String filePath, String fileName);

  void updateFiles(List<MultipartFile> files, String userId, int feedId);
}
