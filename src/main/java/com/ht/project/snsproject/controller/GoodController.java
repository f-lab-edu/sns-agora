package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.annotation.UserInfo;
import com.ht.project.snsproject.model.good.GoodUser;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.GoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodController {

  private final GoodService goodService;
  
  @LoginCheck
  @GetMapping("/{id}")
  public ResponseEntity<List<GoodUser>> getGoodList(@PathVariable int id,
                                                    @RequestParam(required = false) Integer cursor) {

    return ResponseEntity.ok(goodService.getGoodList(id, cursor));
  }

  @LoginCheck
  @PostMapping("/{id}")
  public HttpStatus addGood(@PathVariable int id, @UserInfo User user) {

    goodService.addGood(id, user.getUserId());

    return HttpStatus.OK;
  }

  @LoginCheck
  @DeleteMapping("/{id}")
  public HttpStatus cancelGood(@PathVariable int id, @UserInfo User user) {

    goodService.cancelGood(id, user.getUserId());

    return HttpStatus.NO_CONTENT;
  }
}
