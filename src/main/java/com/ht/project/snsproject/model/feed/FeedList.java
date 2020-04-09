package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Value;

import java.sql.Timestamp;

@Value
public class FeedList {

    int id;

    String userId;

    String title;

    String content;

    Timestamp date;

    PublicScope publicScope;

    int recommend;

    String path;

    String fileNames;
}
