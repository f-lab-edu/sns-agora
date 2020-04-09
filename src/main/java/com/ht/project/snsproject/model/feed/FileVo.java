package com.ht.project.snsproject.model.feed;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileVo {

    int fileIndex;

    String path;

    String fileName;

    public static FileVo getInstance(int fileIndex, String path, String fileName){
        return new FileVo(fileIndex,path,fileName);
    }
}
