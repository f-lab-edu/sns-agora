package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.FileDelete;
import com.ht.project.snsproject.model.feed.FileInfo;
import com.ht.project.snsproject.model.feed.FileVo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {

  void fileListUpload(List<FileInfo> fileInfo);

  String getFilePath(int feedId);

  List<FileVo> getFiles(int feedId);

  void deleteFile(int feedId);

  void deleteFiles(List<FileDelete> fileDeleteList);

  List<String> getFileNames(int feedId);

  void upsertFiles(List<FileInfo> fileInfoList);
}
