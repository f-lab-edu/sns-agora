package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.feed.FileAdd;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    void fileUpload(List<MultipartFile> files, String userId, int feedId);

    void deleteAllFiles(int feedId);

    void deleteFiles(int feedId, List<String> fileNames);

    void addFiles(int feedId, List<FileAdd> fileAddList);

    void updateFiles(List<MultipartFile> files, String userId, int feedId);
}
