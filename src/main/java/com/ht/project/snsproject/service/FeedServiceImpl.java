package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.mapper.FeedMapper;
import com.ht.project.snsproject.model.feed.FeedInsert;
import com.ht.project.snsproject.model.feed.FeedVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class FeedServiceImpl implements FeedService{

    @Resource
    FeedMapper feedMapper;

    private static final String FILEPATH = "C:\\images\\";


    @Override
    public void feedUpload(MultipartFile feed, FeedVO feedVO, String userId) throws IOException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmSS");
        String time = dateFormat.format(System.currentTimeMillis());
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        int publicScope = Integer.parseInt(feedVO.getPublicScope());

        try {
            File destDir = new File(FILEPATH + time + userId);

            if(!destDir.exists()){ destDir.mkdirs(); }

            String originalFileName = feed.getOriginalFilename();
            String filePath = FILEPATH + time + userId + File.separator + originalFileName;

            File destFile = new File(filePath);
            feed.transferTo(destFile);

            FeedInsert feedInsert = new FeedInsert(userId,
                    feedVO.getTitle(),
                    filePath,
                    feedVO.getContent(),
                    date,
                    publicScope);
            feedMapper.feedUpload(feedInsert);
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
