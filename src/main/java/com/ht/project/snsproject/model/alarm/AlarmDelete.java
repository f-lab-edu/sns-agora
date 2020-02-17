package com.ht.project.snsproject.model.alarm;

import com.ht.project.snsproject.enumeration.Type;
import lombok.Value;

@Value
public class AlarmDelete {

    String userId;

    String targetId;

    Type type;
}
