package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.AlarmType;
import com.ht.project.snsproject.model.alarm.Alarm;

import java.util.List;

public interface AlarmService {

    void insertAlarm(String userId, String targetId, AlarmType alarmType);

    void deleteRequestAlarm(String userId, String targetId, AlarmType alarmType);

    List<Alarm> getAlarmList(Integer cursor, String userId);

    Alarm getAlarm(int id, String userId);

    void deleteAlarm(int id, String userId);
}
