package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.annotation.UserInfo;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedUpdateParam;
import com.ht.project.snsproject.model.feed.FeedVo;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/feeds")
public class FeedController {

  @Autowired
  FeedService feedService;

  @LoginCheck
  @PostMapping
  public HttpStatus feedUpload(@RequestParam("file") List<MultipartFile> files,
                               FeedVo feedVo, @UserInfo User user) {

    feedService.feedUpload(files, feedVo, user.getUserId());

    return HttpStatus.OK;
  }

  @LoginCheck
  @GetMapping("/users/{targetId}/{id}")
  public ResponseEntity<Feed> getFeed(@PathVariable String targetId,
                                      @PathVariable int id, @UserInfo User user) {

    return ResponseEntity.ok(feedService.getFeed(user.getUserId(), targetId, id));
  }

  @LoginCheck
  @GetMapping("/{targetId}")
  public ResponseEntity<List<Feed>> getFeedList(@PathVariable String targetId,
                                                @RequestParam(required = false) Integer cursor,
                                                @UserInfo User user) {

    return ResponseEntity.ok(feedService.getFeedListByUser(user.getUserId(), targetId,
            Pagination.pageInfo(cursor)));
  }

  @LoginCheck
  @GetMapping
  public ResponseEntity<List<Feed>> getFriendsFeedList(
          @RequestParam(required = false) Integer cursor, @UserInfo User user) {

    return ResponseEntity.ok(feedService.getFriendsFeedList(user.getUserId(),Pagination.pageInfo(cursor)));
  }

  @LoginCheck
  @DeleteMapping("/{id}")
  public HttpStatus deleteFeed(@PathVariable int id, @UserInfo User user) {

    feedService.deleteFeed(id, user.getUserId());

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @PutMapping("/{id}")
  public HttpStatus updateFeed(@PathVariable int id,
                               @RequestParam("file") List<MultipartFile> files,
                               FeedUpdateParam feedUpdateParam,
                               @UserInfo User user) {

    feedService.updateFeed(files, feedUpdateParam, id, user.getUserId());

    return HttpStatus.OK;
  }
}
