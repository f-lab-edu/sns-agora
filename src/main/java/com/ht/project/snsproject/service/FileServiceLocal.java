package com.ht.project.snsproject.service;

import com.ht.project.snsproject.Exception.FileUploadException;
import com.ht.project.snsproject.enumeration.ErrorCode;
import com.ht.project.snsproject.mapper.FileMapper;
import com.ht.project.snsproject.model.feed.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("localFileService")
public class FileServiceLocal implements FileService {

    @Autowired
    FileMapper fileMapper;

    @Value("${file.windows.path}")
    private String localPath;

    /** SimpleDateFormat 타입은 쓰레드에 안전하지 않은 객체이다.
     * Java 8 버전 부터는 쓰레드에 안전한 LocalDateTime 객체와 DateTimeFormatter 객체를 지원한다.
     * DateTimeFormatter 객체는 SimpleDateFormat 객체보다 처리가 빠르다.
     * 그러므로 LocalDateTime, DateTimeFormatter 두 객체를 조합하여 사용하면
     * 쓰레드에 안전하고 빠른 원하는 포맷의 날짜 객체를 사용할 수 있다.
     */

    @Override
    @Transactional
    public void fileUpload(List<MultipartFile> files, String userId, int feedId) {

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmSS"));
        String dirPath= localPath + userId + File.separator + time;
        int fileIndex = 1;// file 순서를 정해줄 index
        List<FileInfo> fileInfoList = new ArrayList<>();

        try {
            File destDir = new File(dirPath);

            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            for(MultipartFile file:files) {
                String originalFileName = file.getOriginalFilename();
                String filePath = dirPath + File.separator + originalFileName;
                File destFile = new File(filePath);

                fileInfoList.add(new FileInfo(dirPath, originalFileName, fileIndex, feedId));
                file.transferTo(destFile);

                fileIndex++;
            }
            fileMapper.fileListUpload(fileInfoList);
        } catch (IOException ioe) {
            throw new FileUploadException("파일 업로드에 실패하였습니다.", ioe, ErrorCode.UPLOAD_ERROR);
        }
    }

    @Transactional
    @Override
    public void deleteFile(int feedId){


        String path = fileMapper.getFilePath(feedId);

        if(path!=null) {
            File dir = new File(path);

            if (dir.exists()) {

                File[] files = dir.listFiles();

                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
                dir.delete();
            }
        }

        fileMapper.deleteFile(feedId);
    }
}
