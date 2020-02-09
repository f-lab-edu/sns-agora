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
import java.text.DateFormat;
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

    /* ThreadLocal 객체를 사용하면 SimpleDateFormat 의 Thread Safety 를 보장할 수 있다.
    * ThreadLocal 은 한 쓰레드에서 실행되는 코드가 동일한 객체를 사용할 수 있도록 해 주기 때문에
    * 쓰레드와 관련된 코드에서 파라미터를 사용하지 않고 객체를 전파하기 위한 용도로 주로 사용된다.
    * */
    private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd_HHmmSS");
        }
    };

    @Override
    public String fileUpload(List<MultipartFile> files, String userId) {

        String time = dateFormat.get().format(System.currentTimeMillis());
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
        }finally {
            dateFormat.remove();
        }

        return dirPath;
    }
}
