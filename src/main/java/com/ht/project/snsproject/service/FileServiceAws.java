package com.ht.project.snsproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ht.project.snsproject.Exception.FileUploadException;
import com.ht.project.snsproject.enumeration.ErrorCode;
import com.ht.project.snsproject.mapper.FileMapper;
import com.ht.project.snsproject.model.feed.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Qualifier("awsFileService")
@PropertySource("application-aws.properties")
public class FileServiceAws implements FileService {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Autowired
    private FileServiceAws fileServiceAws;

    private static final Logger logger = LoggerFactory.getLogger(FileServiceAws.class);

    @Autowired
    FileMapper fileMapper;
    /** ThreadLocal 객체를 사용하면 SimpleDateFormat 의 Thread Safety 를 보장할 수 있다.
     * ThreadLocal 은 한 쓰레드에서 실행되는 코드가 동일한 객체를 사용할 수 있도록 해 주기 때문에
     * 쓰레드와 관련된 코드에서 파라미터를 사용하지 않고 객체를 전파하기 위한 용도로 주로 사용된다.
     *
     * 톰캣,웹로직등의 웹어플리케이션서버에서 ThreadLocal 을 사용할 경우 로직이 종료되도 GC가 안된다.
     * 어플리케이션 쓰레드 내부에서는 ThreadLocal 객체를 strong reference 하기 때문에
     * 어플리케이션의 lifecycle 이 끝났으면 GC가 되어야 하지만
     * 웹서버 입장에서는 실제 thread 를 생성한 것은 was 이고,
     * 여기서도 ThreadLocal 객체를 참조하고 있기 때문이다.
     * 그렇기 때문에 해당 객체를 사용 후에는 반드시 제거해주어야 한다.
     * 그렇지 않으면 재사용되는 Thread 가 올바르지 않은 데이터를 참조할 수 있으며,
     * 혹은 Garbage 들이 쌓여 Memory Leak 문제가 발생할 수 있다.
     * */
    private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd_HHmmSS");
        }
    };

    @Override
    @Transactional
    public void fileUpload(List<MultipartFile> files, String userId, int feedId) {

        String time = dateFormat.get().format(new Date());
        String dirPath = userId + File.separator + time;
        ObjectMetadata metadata = new ObjectMetadata();
        int fileIndex = 1;
        List<FileInfo> fileInfoList = new ArrayList<>();

        try {
            for (MultipartFile file : files) {

                metadata.setContentType(MediaType.IMAGE_JPEG_VALUE);
                metadata.setContentLength(file.getSize());
                String keyName = dirPath + File.separator + file.getOriginalFilename();

                fileInfoList.add(new FileInfo(dirPath, file.getOriginalFilename(), fileIndex, feedId));
                s3Client.putObject(bucketName, keyName, file.getInputStream(), metadata);
                fileIndex++;
            }
            fileMapper.fileListUpload(fileInfoList);
        } catch (IOException ioe){
            throw new FileUploadException("파일 업로드에 실패하였습니다.", ioe, ErrorCode.UPLOAD_ERROR);
        }finally {
            dateFormat.remove();
        }

    }

    @Transactional
    @Override
    public void deleteAllFiles(int feedId) {

        List<FileVo> files = fileMapper.getFiles(feedId);
        List<DeleteObjectsRequest.KeyVersion> keyVersions = new ArrayList<>();

        if(files!=null){
            for (FileVo file : files) {
                keyVersions.add(new DeleteObjectsRequest.KeyVersion(file.getFilePath()
                        +File.separator
                        +file.getFileName()));
            }

            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keyVersions);
            s3Client.deleteObjects(deleteObjectsRequest);
        }
        fileMapper.deleteFile(feedId);
    }

    @Transactional
    @Override
    public void updateFiles(List<MultipartFile> files, String userId, int feedId) {

        List<String> originFiles = fileMapper.getFileNames(feedId);
        String path = fileMapper.getFilePath(feedId);
        List<FileAdd> uploadFiles = new ArrayList<>();
        List<FileInfo> fileInfoList = new ArrayList<>();

        int fileIndex = 0;

        if(originFiles.isEmpty()){
            fileServiceAws.fileUpload(files,userId,feedId);
            return;
        }

        if(!files.isEmpty()) {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                ++fileIndex;
                if (originFiles.contains(fileName)) {
                    fileInfoList.add(new FileInfo(path,fileName,fileIndex,feedId));
                    originFiles.remove(fileName);
                } else {
                    uploadFiles.add(FileAdd.create(fileIndex, file));
                }
            }
        } else{
            deleteAllFiles(feedId);
            return;
        }

        if(!uploadFiles.isEmpty()){
            fileInfoList.addAll(fileServiceAws.addFiles(feedId, path, uploadFiles));
        }

        if(!originFiles.isEmpty()){
            fileServiceAws.deleteFiles(feedId, path, originFiles);
        }

        fileMapper.insertAndUpdateFiles(fileInfoList);
    }

    @Transactional
    @Override
    public void deleteFiles(int feedId, String path, List<String> fileNames) {

        List<FileDelete> fileDeleteList = new ArrayList<>();
        List<DeleteObjectsRequest.KeyVersion> keyVersionList = new ArrayList<>();

        if (path != null) {
            for(String fileName : fileNames){
                fileDeleteList.add(FileDelete.create(feedId,fileName));
                keyVersionList.add(new DeleteObjectsRequest.KeyVersion(path+File.separator+fileName));
            }
            fileMapper.deleteFiles(fileDeleteList);
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keyVersionList);
            s3Client.deleteObjects(deleteObjectsRequest);
        }
    }

    @Transactional
    @Override
    public List<FileInfo> addFiles(int feedId, String path, List<FileAdd> fileAddList) {

        List<FileInfo> fileInfoList = new ArrayList<>();
        ObjectMetadata objectMetadata =new ObjectMetadata();

        try {
            for (FileAdd fileAdd : fileAddList) {

                MultipartFile file = fileAdd.getFile();
                String originalFileName = file.getOriginalFilename();
                objectMetadata.setContentType(MediaType.IMAGE_JPEG_VALUE);
                objectMetadata.setContentLength(file.getSize());
                String keyName = path + File.separator + originalFileName;
                int fileIndex = fileAdd.getFileIndex();

                fileInfoList.add(new FileInfo(path, originalFileName, fileIndex, feedId));
                s3Client.putObject(bucketName, keyName, file.getInputStream(), objectMetadata);
            }
            return fileInfoList;
        }catch (IOException ioe){
            throw new FileUploadException("파일 업로드에 실패하였습니다.", ioe, ErrorCode.UPLOAD_ERROR);
        }finally {
            dateFormat.remove();
        }
    }
}
