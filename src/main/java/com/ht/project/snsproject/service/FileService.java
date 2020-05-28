package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.feed.FileAdd;
import com.ht.project.snsproject.model.feed.FileInfo;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

  void fileUpload(List<MultipartFile> files, String userId, int feedId);

  void deleteAllFiles(int feedId);

  void deleteFiles(int feedId, String path, List<String> fileNames);

  List<FileInfo> addFiles(int feedId, String path, List<FileAdd> fileAddList);

  void updateFiles(List<MultipartFile> files, String userId, int feedId);
}
