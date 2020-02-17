package com.ht.project.snsproject.model.alarm;

import com.ht.project.snsproject.enumeration.Type;
import lombok.Value;

import java.sql.Timestamp;

@Value
public class AlarmInsert {

    String userId;

    String targetId;

    Enum<Type> type;

    Timestamp date;

    boolean readCheck = false;

    String url;

}
