package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.annotation.UserInfo;
import com.ht.project.snsproject.model.alarm.Alarm;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alarms")
@RequiredArgsConstructor
public class AlarmController {

  private final AlarmService alarmService;

  /**
   * 알람 목록을 가져오는 메소드.
   * @param cursor 알람 리스트의 페이징의 커서
   * @param user 세션에 저장된 user 객체;
   * @return List
   */
  @LoginCheck
  @GetMapping
  public ResponseEntity<List<Alarm>> getAlarmList(@RequestParam(required = false) Integer cursor,
                                                  @UserInfo User user) {

    return ResponseEntity.ok(alarmService.getAlarmList(cursor, user.getUserId()));
  }

  /**
   * 특정 인데스에 해당하는 알람을 가져오는 메소드.
   * @param id alarm 의 인덱스
   * @param user session user
   * @return Alarm
   */
  @LoginCheck
  @GetMapping("/{id}")
  public ResponseEntity<Alarm> getAlarm(@PathVariable int id, @UserInfo User user) {

    return ResponseEntity.ok(alarmService.getAlarm(id, user.getUserId()));
  }

  /**
   * 특정 인덱스의 알람을 지우는 메소드.
   * @param id alarm 의 인덱스
   * @param user session user
   * @return HttpStatus
   */
  @LoginCheck
  @DeleteMapping("/{id}")
  public HttpStatus deleteAlarm(@PathVariable int id, @UserInfo User user) {

    alarmService.deleteAlarm(id, user.getUserId());

    return HttpStatus.NO_CONTENT;
  }
}
