package com.ht.project.snsproject.model.recommend;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;

@Builder
@Value
public class FeedCacheUpdateParam {

    int feedId;

    String userId;

    String title;

    String content;

    Timestamp date;

    PublicScope publicScope;

    int recommend;
}
