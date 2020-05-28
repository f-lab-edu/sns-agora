package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.alarm.Alarm;
import com.ht.project.snsproject.model.alarm.AlarmDelete;
import com.ht.project.snsproject.model.alarm.AlarmInsert;
import com.ht.project.snsproject.model.alarm.AlarmRead;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AlarmMapper {

  void insertAlarm(AlarmInsert alarmInsert);

  void deleteRequestAlarm(AlarmDelete alarmDelete);

  List<Alarm> getAlarmList(@Param("userId") String userId,
                           @Param("pagination") Pagination pagination);

  void readAlarm(AlarmRead alarmRead);

  Alarm getAlarm(AlarmRead alarmRead);

  boolean deleteAlarm(AlarmRead alarmRead);
}
