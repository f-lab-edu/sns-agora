package com.ht.project.snsproject.model.alarm;

import com.ht.project.snsproject.enumeration.AlarmType;
import com.ht.project.snsproject.enumeration.ReadStatus;
import lombok.Value;

import java.sql.Timestamp;

@Value
public class AlarmInsert {

    String userId;

    String targetId;

    AlarmType alarmType;

    Timestamp date;

    Integer feedId = 0;

    ReadStatus readCheck = ReadStatus.NO_READ;

}
