package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.feed.FileDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

  void uploadFiles(List<MultipartFile> files, String dirPath);

  void insertFileInfoList(List<FileDto> fileDtoList, int feedId);

  void deleteFiles(int feedId);

  void deleteFile(String filePath, String fileName);

  void updateFiles(List<MultipartFile> files, List<FileDto> fileDtoList, int feedId);

}
