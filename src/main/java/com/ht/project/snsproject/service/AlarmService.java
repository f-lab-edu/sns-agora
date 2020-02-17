package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.Type;
import com.ht.project.snsproject.model.alarm.Alarm;

import java.util.List;

public interface AlarmService {

    void insertAlarm(String userId, String targetId, Type type, String url);

    void deleteAlarm(String userId, String targetId, Type type);

    List<Alarm> getAlarmList(Integer cursor, String userId);
}
