package com.ht.project.snsproject.model.feed;

import lombok.Value;

@Value
public class FileInfo {

    String path;

    String fileName;

    int fileIndex;

    int feedId;
}
