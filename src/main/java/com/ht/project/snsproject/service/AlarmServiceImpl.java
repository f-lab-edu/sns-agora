package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.AlarmType;
import com.ht.project.snsproject.mapper.AlarmMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.alarm.Alarm;
import com.ht.project.snsproject.model.alarm.AlarmDelete;
import com.ht.project.snsproject.model.alarm.AlarmInsert;
import com.ht.project.snsproject.model.alarm.AlarmRead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    AlarmMapper alarmMapper;

    @Override
    public void insertAlarm(String userId, String targetId, AlarmType alarmType) {

        Timestamp dateTime = Timestamp.valueOf(LocalDateTime.now());
        AlarmInsert alarmInsert = new AlarmInsert(userId, targetId, alarmType, dateTime);
        alarmMapper.insertAlarm(alarmInsert);
    }

    @Override
    public void deleteRequestAlarm(String userId, String targetId, AlarmType alarmType) {
        alarmMapper.deleteRequestAlarm(new AlarmDelete(userId,targetId,alarmType));
    }

    @Override
    public List<Alarm> getAlarmList(Integer cursor, String userId){
        Pagination pagination = new Pagination(cursor);
        return alarmMapper.getAlarmList(userId, pagination);
    }

    @Transactional
    @Override
    public Alarm getAlarm(int id, String userId) {

        AlarmRead alarmRead = AlarmRead.create(id, userId);
        alarmMapper.readAlarm(alarmRead);

        return alarmMapper.getAlarm(alarmRead);
    }

    @Override
    public void deleteAlarm(int id, String userId) {
        boolean result = alarmMapper.deleteAlarm(AlarmRead.create(id, userId));

        if(!result){
            throw new IllegalArgumentException("일치하는 자료가 없습니다.");
        }
    }
}
