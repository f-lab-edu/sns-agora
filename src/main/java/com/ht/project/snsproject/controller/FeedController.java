package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.feed.FeedVO;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping
public class FeedController {

    @Autowired
    FeedService feedService;

    @LoginCheck
    @PostMapping("/upload")
    public HttpStatus feedUpload(@RequestParam("file") MultipartFile feed, FeedVO feedVO, HttpSession httpSession) throws IOException {
        User userInfo = (User) httpSession.getAttribute("userInfo");
        feedService.feedUpload(feed, feedVO, userInfo.getUserId());
        return HttpStatus.OK;
    }
}
