package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.Type;
import com.ht.project.snsproject.mapper.AlarmMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.alarm.Alarm;
import com.ht.project.snsproject.model.alarm.AlarmDelete;
import com.ht.project.snsproject.model.alarm.AlarmInsert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    AlarmMapper alarmMapper;

    @Override
    public void insertAlarm(String userId, String targetId, Type type, String url) {

        Timestamp dateTime = Timestamp.valueOf(LocalDateTime.now());
        AlarmInsert alarmInsert = new AlarmInsert(userId, targetId, type, dateTime, url);
        alarmMapper.insertAlarm(alarmInsert);
    }

    @Override
    public void deleteAlarm(String userId, String targetId, Type type) {
        alarmMapper.deleteAlarm(new AlarmDelete(userId,targetId,type));
    }

    @Override
    public List<Alarm> getAlarmList(Integer cursor, String userId){
        Pagination pagination = new Pagination(cursor);
        return alarmMapper.getAlarmList(userId, pagination);
    }

}
