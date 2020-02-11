package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginMethodCheck;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.FeedList;
import com.ht.project.snsproject.model.feed.FeedVO;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.FeedService;
import com.ht.project.snsproject.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/feeds")
public class FeedController {

    @Autowired
    FeedService feedService;

    @Autowired
    @Qualifier("localFileService")
    FileService fileService;

    @LoginMethodCheck
    @PostMapping("/upload")
    public HttpStatus feedUpload(@RequestParam("file") List<MultipartFile> files, FeedVO feedVO, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String path = fileService.fileUpload(files, userInfo.getUserId());
        feedService.feedUpload(feedVO,userInfo.getUserId(),path);
        return HttpStatus.OK;
    }


    //[WIP] 친구관계 추가, 읽기 권한 추가 필요
    @LoginMethodCheck
    @GetMapping("/{userId}")
    public ResponseEntity<List<FeedList>> getFeedList(@PathVariable String userId, @RequestParam(required = false) Integer cursor){
        Pagination pagination = new Pagination(cursor);
        return new ResponseEntity<>(feedService.getFeedList(userId, pagination),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<FeedList>> getAllFeedList(@RequestParam Integer cursor, HttpSession httpSession){
        return null;
    }

}
