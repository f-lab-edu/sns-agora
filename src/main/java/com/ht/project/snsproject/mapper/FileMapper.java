package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.FileInfo;
import com.ht.project.snsproject.model.feed.FileVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {

    void fileListUpload(List<FileInfo> fileInfo);

    String getFilePath(int feedId);

    List<FileVo> getFiles(int feedId);

    void deleteFile(int feedId);
}
