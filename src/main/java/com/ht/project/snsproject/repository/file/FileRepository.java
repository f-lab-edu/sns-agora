package com.ht.project.snsproject.repository.file;

import com.ht.project.snsproject.mapper.FileMapper;
import com.ht.project.snsproject.model.feed.FileDelete;
import com.ht.project.snsproject.model.feed.FileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FileRepository {

  private final FileMapper fileMapper;

  public void insertFileInfoList(List<FileInfo> fileInfoList) {

    fileMapper.fileListUpload(fileInfoList);
  }

  public void deleteFile(int feedId) {

    fileMapper.deleteFile(feedId);
  }

  public void deleteFiles(List<FileDelete> fileDeleteList) {

    fileMapper.deleteFiles(fileDeleteList);
  }

  public void upsertFiles(List<FileInfo> fileInfoList) {

    fileMapper.upsertFiles(fileInfoList);
  }

  public List<String> findFileNamesByFeedId(int feedId) {

    return fileMapper.findFileNamesByFeedId(feedId);
  }
}
