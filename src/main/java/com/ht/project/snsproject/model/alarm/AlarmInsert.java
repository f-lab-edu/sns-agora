package com.ht.project.snsproject.model.alarm;

import com.ht.project.snsproject.enumeration.AlarmType;
import lombok.Value;

import java.sql.Timestamp;

@Value
public class AlarmInsert {

    String userId;

    String targetId;

    AlarmType alarmType;

    Timestamp date;

    boolean readCheck = false;

}
