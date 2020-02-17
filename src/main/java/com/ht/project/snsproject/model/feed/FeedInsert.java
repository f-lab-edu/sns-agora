package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class FeedInsert {

    int id;

    String userId;

    String title;

    String content;

    Timestamp date;

    PublicScope publicScope;

    int like;
}
