package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.annotation.UserInfo;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

  @Autowired
  private CommentService commentService;

  @PostMapping("/{id}")
  @LoginCheck
  public HttpStatus writeCommentOnFeed(@PathVariable int id,
                                       @RequestBody String content,
                                       @UserInfo User user) {

    commentService.insertCommentOnFeed(id, content, user.getUserId());

    return HttpStatus.OK;
  }
}
