package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.annotation.UserInfo;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.*;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.FeedRecommendService;
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

  @Autowired
  FeedRecommendService feedRecommendService;

  @LoginCheck
  @PostMapping
  public HttpStatus feedUpload(@RequestParam("file") List<MultipartFile> files,
                               FeedWriteDto feedWriteDto, @UserInfo User user) {

    feedService.feedUpload(files, feedWriteDto, user.getUserId());

    return HttpStatus.OK;
  }

  @LoginCheck
  @GetMapping("/{targetId}")
  public ResponseEntity<List<Feed>> getFeedList(@PathVariable String targetId,
                                                @RequestParam(required = false) Integer cursor,
                                                @UserInfo User user) {

    return ResponseEntity.ok(feedService.findFeedListByUserId(user.getUserId(), targetId, new Pagination(cursor)));
  }

  @LoginCheck
  @GetMapping
  public ResponseEntity<List<Feed>> getFriendsFeedList(@RequestParam(required = false) Integer cursor,
                                                       @UserInfo User user) {

    return ResponseEntity.ok(feedService.findFriendsFeedListByUserId(user.getUserId(), new Pagination(cursor)));
  }

  @LoginCheck
  @GetMapping("/{targetId}/{feedId}")
  public ResponseEntity<Feed> getFeed(@PathVariable String targetId,
                                        @PathVariable int feedId, @UserInfo User user) {

    return ResponseEntity.ok(feedService.findFeedByFeedId(user.getUserId(), targetId, feedId));
  }

  @LoginCheck
  @DeleteMapping("/{feedId}")
  public HttpStatus deleteFeed(@PathVariable int feedId, @UserInfo User user) {

    feedService.deleteFeed(feedId, user.getUserId());

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @PutMapping("/{feedId}")
  public HttpStatus updateFeed(@PathVariable int feedId,
                               @RequestParam("file") List<MultipartFile> files,
                               FeedWriteDto feedUpdateParam,
                               @UserInfo User user) {

    feedService.updateFeed(files, feedUpdateParam, feedId, user.getUserId());

    return HttpStatus.OK;
  }

  @LoginCheck
  @GetMapping("/recommends")
  public ResponseEntity<List<FeedInfo>> getFeedRecommendList(
          @RequestParam(required = false) Integer cursor,
          @UserInfo User user) {

    return ResponseEntity.ok(feedRecommendService.findLatestAllFeedList(user.getUserId(), new Pagination(cursor)));
  }
}
