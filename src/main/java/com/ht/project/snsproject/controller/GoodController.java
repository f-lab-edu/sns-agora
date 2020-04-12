package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.annotation.User;
import com.ht.project.snsproject.model.user.UserVo;
import com.ht.project.snsproject.service.GoodService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
  @PostMapping("/{id}")
  public HttpStatus increaseGood(@PathVariable int id, @User UserVo user) {

    goodService.addGood(id, user.getUserId());

    return HttpStatus.OK;
  }

  @LoginCheck
  @DeleteMapping("/{id}")
  public HttpStatus cancelGood(@PathVariable int id, @User UserVo user) {

    goodService.cancelGood(id, user.getUserId());

    return HttpStatus.NO_CONTENT;
  }
}
