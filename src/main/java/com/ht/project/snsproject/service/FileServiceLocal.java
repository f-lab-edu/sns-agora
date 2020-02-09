package com.ht.project.snsproject.service;

import com.ht.project.snsproject.Exception.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class FileServiceLocal implements FileService {

    @Value("${file.windows.path}")
    private String localPath;

    public String fileUpload(List<MultipartFile> files, String userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmSS");
        String time = dateFormat.format(System.currentTimeMillis());
        String dirPath= localPath + time + userId;
        try {
            File destDir = new File(dirPath);

            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            for(MultipartFile file:files) {
                String originalFileName = file.getOriginalFilename();
                String filePath = dirPath + File.separator + originalFileName;

                File destFile = new File(filePath);
                file.transferTo(destFile);
            }
        } catch (IOException ioe) {
            throw new FileUploadException("파일 업로드에 실패하였습니다.");
        }
        return dirPath;
    }
}
