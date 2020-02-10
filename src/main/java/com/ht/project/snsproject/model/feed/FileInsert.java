package com.ht.project.snsproject.model.feed;

import lombok.Value;

@Value
public class FileInsert {

    String userId;

    String path;

    String fileName;

    int fileIndex;
}
