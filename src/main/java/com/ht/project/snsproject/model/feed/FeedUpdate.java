package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;

@Builder
@Value
public class FeedUpdate {

    int id;

    String userId;

    String title;

    String content;

    Timestamp date;

    PublicScope publicScope;

}
