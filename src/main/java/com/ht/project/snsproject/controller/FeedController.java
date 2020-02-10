package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.feed.FeedVO;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.FeedService;
import com.ht.project.snsproject.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping
public class FeedController {

    @Autowired
    FeedService feedService;

    @Autowired
    @Qualifier("localFileService")
    FileService fileService;

    @LoginCheck
    @PostMapping("/upload")
    public HttpStatus feedUpload(@RequestParam("file") List<MultipartFile> files, FeedVO feedVO, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String path = fileService.fileUpload(files, userInfo.getUserId());
        feedService.feedUpload(feedVO,userInfo.getUserId(),path);
        return HttpStatus.OK;
    }
}
