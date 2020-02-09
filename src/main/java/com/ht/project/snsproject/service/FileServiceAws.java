package com.ht.project.snsproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ht.project.snsproject.Exception.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@Qualifier("mainFileService")
@PropertySource("application-aws.properties")
public class FileServiceAws implements FileService {

    @Autowired
    private AmazonS3 S3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public String fileUpload(List<MultipartFile> files, String userId) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmSS");
        String time = dateFormat.format(System.currentTimeMillis());
        String dirPath = time + userId;
        ObjectMetadata metadata = new ObjectMetadata();

        try {
            for (MultipartFile file : files) {

                metadata.setContentType(MediaType.IMAGE_JPEG_VALUE);
                metadata.setContentLength(file.getSize());
                String keyName = dirPath + "/" + file.getOriginalFilename();

                S3Client.putObject(bucketName, keyName, file.getInputStream(), metadata);
            }
        } catch (IOException ioe){

            throw new FileUploadException("파일 업로드에 실패하였습니다.");
        }

        return dirPath;
    }
}
