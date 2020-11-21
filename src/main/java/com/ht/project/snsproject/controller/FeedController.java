package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.annotation.UserInfo;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedWriteDto;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.FeedRecommendService;
import com.ht.project.snsproject.service.FeedService;
import com.ht.project.snsproject.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedController {

  private final FeedService feedService;

  private final FeedRecommendService feedRecommendService;

  private final TestService testService;

  @LoginCheck
  @PostMapping
  public HttpStatus feedUpload(@RequestParam("file") List<MultipartFile> files,
                               FeedWriteDto feedWriteDto, @UserInfo User user) {

    feedService.feedUpload(files, feedWriteDto, user.getUserId());

    return HttpStatus.OK;
  }

  @LoginCheck
  @GetMapping
  public ResponseEntity<List<Feed>> getFeedList(@RequestParam String targetId,
                                                @RequestParam(required = false) Integer cursor,
                                                @UserInfo User user) {

    return ResponseEntity.ok(feedService.findFeedListByUserId(user.getUserId(), targetId, new Pagination(cursor)));
  }

  @LoginCheck
  @GetMapping("/friends")
  public ResponseEntity<List<Feed>> getFriendsFeedList(@RequestParam(required = false) Integer cursor,
                                                       @UserInfo User user) {

    return ResponseEntity.ok(feedService.findFriendsFeedListByUserId(user.getUserId(), new Pagination(cursor)));
  }

  @LoginCheck
  @GetMapping("/{feedId}")
  public ResponseEntity<Feed> getFeed(@PathVariable int feedId, @UserInfo User user) {

    return ResponseEntity.ok(feedService.findFeedByFeedId(user.getUserId(), feedId));
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
                               FeedWriteDto feedWriteDto,
                               @UserInfo User user) {

    feedService.updateFeed(files, feedWriteDto, feedId, user.getUserId());

    return HttpStatus.OK;
  }

  @LoginCheck
  @GetMapping("/recommends")
  public ResponseEntity<List<FeedInfo>> getFeedRecommendList(
          @RequestParam(required = false) Integer cursor) {

    return ResponseEntity.ok(feedRecommendService.findLatestAllFeedList(new Pagination(cursor)));
  }

  @LoginCheck
  @GetMapping("/test")
  public ResponseEntity<Feed> getFeedTest(@UserInfo User user) {

    int feedId = (int) (Math.random() * 10_000_000) + 1;
    return ResponseEntity.ok(testService.findFeedByFeedId(user.getUserId(), feedId));
  }
}
