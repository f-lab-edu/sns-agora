package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.FileDelete;
import com.ht.project.snsproject.model.feed.FileInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {

  void fileListUpload(List<FileInfo> fileInfo);

  void deleteFile(int feedId);

  void deleteFiles(List<FileDelete> fileDeleteList);

  List<String> findFileNamesByFeedId(int feedId);

  void upsertFiles(List<FileInfo> fileInfoList);
}
