package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.FileDelete;
import com.ht.project.snsproject.model.feed.FileInfo;
import com.ht.project.snsproject.model.feed.FileVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {

  void fileListUpload(List<FileInfo> fileInfo);

  String findFilePathByFeedId(int feedId);

  List<FileVo> findFilesByFeedId(int feedId);

  void deleteFile(int feedId);

  void deleteFiles(List<FileDelete> fileDeleteList);

  List<String> findFileNamesByFeedId(int feedId);

  void upsertFiles(List<FileInfo> fileInfoList);
}
