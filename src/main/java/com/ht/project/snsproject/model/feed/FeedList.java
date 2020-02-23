package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Data
public class FeedList {

    int id;

    String userId;

    String title;

    String content;

    Timestamp date;

    PublicScope publicScope;

    int recommend;

    List<FileVo> files;
}
