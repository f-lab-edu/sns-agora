package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.GoodService;
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
@RequestMapping("/goods")
public class GoodController {

  @Autowired
  GoodService goodService;

  @LoginCheck
  @GetMapping("/{id}")
  public ResponseEntity<List<String>> getGoodList(@PathVariable int id) {

    return ResponseEntity.ok(goodService.getGoodList(id));
  }

  @LoginCheck
  @PutMapping("/{id}")
  public HttpStatus increaseGood(@PathVariable int id, HttpSession httpSession) {

    User userInfo = (User) httpSession.getAttribute("userInfo");
    String userId = userInfo.getUserId();
    goodService.increaseGood(id, userId);

    return HttpStatus.OK;
  }

  @LoginCheck
  @DeleteMapping("/{id}")
  public HttpStatus cancelGood(@PathVariable int id, HttpSession httpSession) {

    User userInfo = (User) httpSession.getAttribute("userInfo");
    String userId = userInfo.getUserId();
    goodService.cancelGood(id, userId);

    return HttpStatus.NO_CONTENT;
  }
}
