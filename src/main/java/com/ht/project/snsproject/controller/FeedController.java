package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedUpdateParam;
import com.ht.project.snsproject.model.feed.FeedVo;
import com.ht.project.snsproject.service.FeedService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/feeds")
public class FeedController {

  @Autowired
  FeedService feedService;

  @LoginCheck
  @PostMapping
  public HttpStatus feedUpload(@RequestParam("file") List<MultipartFile> files,
                               FeedVo feedVo, String userId) {

    feedService.feedUpload(files, feedVo, userId);

    return HttpStatus.OK;
  }

  @LoginCheck
  @GetMapping("/users/{targetId}/{id}")
  public ResponseEntity<Feed> getFeed(@PathVariable String targetId,
                                      @PathVariable int id, String userId) {

    return ResponseEntity.ok(feedService.getFeed(userId, targetId, id));
  }

  @LoginCheck
  @GetMapping("/{targetId}")
  public ResponseEntity<List<Feed>> getFeedList(@PathVariable String targetId,
                                                @RequestParam(required = false) Integer cursor,
                                                String userId) {

    return ResponseEntity.ok(feedService.getFeedList(userId, targetId,
            Pagination.pageInfo(cursor)));
  }

  @LoginCheck
  @GetMapping
  public ResponseEntity<List<Feed>> getFriendsFeedList(
          @RequestParam(required = false) Integer cursor, String userId) {

    return ResponseEntity.ok(feedService.getFriendsFeedList(userId,Pagination.pageInfo(cursor)));
  }

  @LoginCheck
  @DeleteMapping("/{id}")
  public HttpStatus deleteFeed(@PathVariable int id, String userId) {

    feedService.deleteFeed(id, userId);

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @PutMapping("/{id}")
  public HttpStatus updateFeed(@PathVariable int id,
                               @RequestParam("file") List<MultipartFile> files,
                               FeedUpdateParam feedUpdateParam,
                               String userId) {

    feedService.updateFeed(files, feedUpdateParam, id, userId);

    return HttpStatus.OK;
  }
}
