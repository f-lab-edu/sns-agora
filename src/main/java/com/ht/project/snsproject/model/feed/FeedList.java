package com.ht.project.snsproject.model.feed;

import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Data
public class FeedList {

    int id;

    String userId;

    String title;

    String content;

    String path;

    Timestamp date;

    Integer publicScope;

    List<FileVo> files;
}
