package com.ht.project.snsproject.service;

import com.ht.project.snsproject.Exception.FileUploadException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
//@Qualifier("mainFileService")
public class FileServiceLocal implements FileService {

    @Value("${file.windows.path}")
    private String localPath;

    public String fileUpload(List<MultipartFile> files, String userId) {

        /* SimpleDateFormat 타입은 쓰레드에 안전하지 않은 객체이다.
         * Java 8 버전 부터는 쓰레드에 안전한 LocalDateTime 객체와 DateTimeFormatter 객체를 지원한다.
         * DateTimeFormatter 객체는 SimpleDateFormat 객체보다 처리가 빠르다.
         * 그러므로 LocalDateTime, DateTimeFormatter 두 객체를 조합하여 사용하면
         * 쓰레드에 안전하고 빠른 원하는 포맷의 날짜 객체를 사용할 수 있다.
         */
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmSS"));
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
