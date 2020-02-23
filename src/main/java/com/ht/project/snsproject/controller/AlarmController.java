package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.alarm.Alarm;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/alarm")
public class AlarmController {

    @Autowired
    AlarmService alarmService;

    @LoginCheck
    @GetMapping
    public ResponseEntity<List<Alarm>> getAlarmList(@RequestParam(required = false) Integer cursor, HttpSession httpSession){

        User userInfo = (User) httpSession.getAttribute("userInfo");
        return ResponseEntity.ok(alarmService.getAlarmList(cursor, userInfo.getUserId()));
    }
}
