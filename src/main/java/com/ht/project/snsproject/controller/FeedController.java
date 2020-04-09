package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedUpdateParam;
import com.ht.project.snsproject.model.feed.FeedVO;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @LoginCheck
    @PostMapping
    public HttpStatus feedUpload(@RequestParam("file") List<MultipartFile> files,
                                 FeedVO feedVO, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        feedService.feedUpload(files, feedVO,userInfo.getUserId());

        return HttpStatus.OK;
    }

    @LoginCheck
    @GetMapping("/users/{targetId}/{id}")
    public ResponseEntity<List<Feed>> getFeed(@PathVariable String targetId,
                                              @PathVariable int id, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();
        return ResponseEntity.ok(feedService.getFeed(userId, targetId, id));
    }

    @LoginCheck
    @GetMapping("/{targetId}")
    public ResponseEntity<List<Feed>> getFeedList(@PathVariable String targetId,
                                                  @RequestParam(required = false) Integer cursor,
                                                  HttpSession httpSession){

        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();

        return ResponseEntity.ok(feedService.getFeedList(userId, targetId, Pagination.pageInfo(cursor)));
    }

    @LoginCheck
    @GetMapping
    public ResponseEntity<List<Feed>> getFriendsFeedList(@RequestParam(required = false) Integer cursor,
                                                             HttpSession httpSession){

        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();

        return ResponseEntity.ok(feedService.getFriendsFeedList(userId,Pagination.pageInfo(cursor)));
    }

    @LoginCheck
    @DeleteMapping("/{id}")
    public HttpStatus deleteFeed(@PathVariable int id, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();
        feedService.deleteFeed(id, userId);
        return HttpStatus.NO_CONTENT;
    }

    @LoginCheck
    @PutMapping("/{id}")
    public HttpStatus updateFeed(@PathVariable int id,
                                 @RequestParam("file") List<MultipartFile> files,
                                 FeedUpdateParam feedUpdateParam,
                                 HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();

        feedService.updateFeed(files, feedUpdateParam, id, userId);

        return HttpStatus.OK;
    }
}
