package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.annotation.UserInfo;
import com.ht.project.snsproject.model.comment.Comment;
import com.ht.project.snsproject.model.comment.Reply;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.repository.comment.CommentRepository;
import com.ht.project.snsproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  private final CommentRepository commentRepository;

  /**
   * feed에 댓글을 작성하는 메소드
   * id -> feedId로 변경
   * 명확한 의미의 변수 명이 코드 가독성에 좋음.
   * 의미가 명확한 메소드 명과 변수 명 작성 필요.
   * URL -> '피드 feedId 번에 해당하는 댓글 이라는 의미'로 구성.
   * 의미 있는 API 명명 필요.
   *
   * @param feedId
   * @param content
   * @param user
   * @return
   */
  @PostMapping("/{feedId}/comments")
  @LoginCheck
  public HttpStatus writeCommentOnFeed(@PathVariable int feedId,
                                       @RequestBody String content,
                                       @UserInfo User user) {

    commentService.insertCommentOnFeed(feedId, content, user.getUserId());

    return HttpStatus.OK;
  }

  @GetMapping("/{feedId}/comments")
  @LoginCheck
  public ResponseEntity<List<Comment>> getCommentsOnFeed(@PathVariable int feedId,
                                                         @RequestParam(required = false) Integer cursor) {

    return ResponseEntity.ok(commentRepository.getCommentsOnFeed(feedId, cursor));
  }

  @PutMapping("/{feedId}/comments/{commentId}")
  @LoginCheck
  public HttpStatus updateCommentOnFeed(@PathVariable int commentId,
                                        @RequestBody String content,
                                        @UserInfo User user) {

    commentRepository.updateCommentOnFeed(commentId, user.getUserId(), content);
    return HttpStatus.OK;
  }

  /**
   * 댓글에서 이미 첫번째 대댓글을 가져오기 때문에
   * 첫 번째 대댓글의 id를 cursor 로 필수로 제공해야만 합니다.
   * 그렇지 않으면 중복된 데이터가 발생될 수 있습니다.
   * @param commentId
   * @param cursor
   * @return
   */
  @GetMapping("/{feedId}/comments/{commentId}/replies")
  @LoginCheck
  public ResponseEntity<List<Reply>> getReplyOnComment(@PathVariable int commentId,
                                                       @RequestParam Integer cursor) {

    return ResponseEntity.ok(commentRepository.getRepliesOnComment(commentId, cursor));
  }

  @PostMapping("/{feedId}/comments/{commentId}/replies")
  @LoginCheck
  public HttpStatus writeReplyOnComment(@PathVariable int commentId,
                                  @RequestBody String content,
                                  @UserInfo User user) {

    commentRepository.insertReplyOnComment(commentId, content, user.getUserId());
    return HttpStatus.OK;
  }

  @PutMapping("/{feedId}/comments/{commentId}/replies/{replyId}")
  @LoginCheck
  public HttpStatus updateReplyOnComment(@PathVariable int replyId,
                                         @RequestBody String content,
                                         @UserInfo User user) {

    commentRepository.updateReplyOnComment(replyId, user.getUserId(), content);
    return HttpStatus.OK;
  }

  @DeleteMapping("/{feedId}/comments/{commentId}")
  @LoginCheck
  public HttpStatus deleteCommentOnFeed(@PathVariable int feedId,
                                        @PathVariable int commentId,
                                        @UserInfo User user) {

    commentService.deleteCommentOnFeed(feedId, commentId, user.getUserId());
    return HttpStatus.NO_CONTENT;
  }

  @DeleteMapping("/{feedId}/comments/{commentId}/replys/{replyId}")
  @LoginCheck
  public HttpStatus deleteReplyOnComment(@PathVariable int replyId,
                                         @UserInfo User user) {

    commentRepository.deleteReplyOnComment(replyId, user.getUserId());
    return HttpStatus.NO_CONTENT;
  }
}
