package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.alarm.Alarm;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/alarms")
public class AlarmController {

    @Autowired
    AlarmService alarmService;

    @LoginCheck
    @GetMapping
    public ResponseEntity<List<Alarm>> getAlarmList(@RequestParam(required = false) Integer cursor, HttpSession httpSession){

        User userInfo = (User) httpSession.getAttribute("userInfo");

        return ResponseEntity.ok(alarmService.getAlarmList(cursor, userInfo.getUserId()));
    }

    @LoginCheck
    @GetMapping("/{id}")
    public ResponseEntity<Alarm> getAlarm(@PathVariable int id, HttpSession httpSession){

        User userInfo = (User) httpSession.getAttribute("userInfo");

        return ResponseEntity.ok(alarmService.getAlarm(id,userInfo.getUserId()));
    }

    @LoginCheck
    @DeleteMapping("/{id}")
    public HttpStatus deleteAlarm(@PathVariable int id, HttpSession httpSession){

        User userInfo = (User) httpSession.getAttribute("userInfo");
        alarmService.deleteAlarm(id, userInfo.getUserId());

        return HttpStatus.NO_CONTENT;
    }
}
