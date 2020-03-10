package com.ht.project.snsproject.model.feed;

import lombok.Value;

@Value
public class FileUpdate {

    int feedId;

    int fileIndex;

    String fileName;

    public static FileUpdate create(int feedId, int fileIndex, String fileName){
        return new FileUpdate(feedId,fileIndex,fileName);
    }
}
