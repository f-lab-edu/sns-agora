package com.ht.project.snsproject.model.alarm;

import com.ht.project.snsproject.enumeration.AlarmType;
import com.ht.project.snsproject.enumeration.ReadStatus;
import java.sql.Timestamp;
import lombok.Value;

@Value
public class Alarm {

  int id;

  String userId;

  String targetId;

  AlarmType alarmType;

  Timestamp date;

  Integer feedId;

  ReadStatus readCheck;

}
