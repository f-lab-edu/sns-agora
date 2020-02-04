package com.ht.project.snsproject.model.feed;

import lombok.Value;

import java.sql.Timestamp;

@Value
public class FeedInsert {

    String userId;

    String title;

    String path;

    String content;

    Timestamp date;

    int publicScope;
}
