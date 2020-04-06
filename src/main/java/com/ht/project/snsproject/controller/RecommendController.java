package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.RecommendService;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommends")
public class RecommendController {

  @Autowired
  RecommendService recommendService;

  @LoginCheck
  @GetMapping("/{id}")
  public ResponseEntity<List<String>> getRecommendList(@PathVariable int id) {

    return ResponseEntity.ok(recommendService.getRecommendList(id));
  }

  @LoginCheck
  @PutMapping("/{id}")
  public HttpStatus increaseRecommend(@PathVariable int id, HttpSession httpSession) {

    User userInfo = (User) httpSession.getAttribute("userInfo");
    String userId = userInfo.getUserId();
    recommendService.increaseRecommend(id, userId);

    return HttpStatus.OK;
  }

  @LoginCheck
  @DeleteMapping("/{id}")
  public HttpStatus cancelRecommend(@PathVariable int id, HttpSession httpSession) {

    User userInfo = (User) httpSession.getAttribute("userInfo");
    String userId = userInfo.getUserId();
    recommendService.cancelRecommend(id, userId);

    return HttpStatus.NO_CONTENT;
  }
}
