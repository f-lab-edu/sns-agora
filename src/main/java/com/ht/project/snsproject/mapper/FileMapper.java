package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.FileInsert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {
    void fileUpload(FileInsert fileInsert);
}
