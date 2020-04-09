package com.ht.project.snsproject.model.feed;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

@Value
public class FileAdd {

    int fileIndex;

    MultipartFile file;

    public static FileAdd create(int fileIndex, MultipartFile file){
        return new FileAdd(fileIndex, file);
    }
}
