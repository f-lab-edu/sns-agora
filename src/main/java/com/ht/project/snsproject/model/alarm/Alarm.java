package com.ht.project.snsproject.model.alarm;

import com.ht.project.snsproject.enumeration.Type;
import lombok.Value;

import java.sql.Timestamp;

@Value
public class Alarm {

    int id;

    String userId;

    String targetId;

    Type type;

    Timestamp date;

    boolean readCheck;

    String url;
}
