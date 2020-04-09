package com.ht.project.snsproject.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    void fileUpload(List<MultipartFile> files, String userId, int feedId);

}
