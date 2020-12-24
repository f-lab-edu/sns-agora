package com.ht.project.snsproject.repository.file;

import com.ht.project.snsproject.mapper.FileMapper;
import com.ht.project.snsproject.model.feed.FileDelete;
import com.ht.project.snsproject.model.feed.FileInfo;
import com.ht.project.snsproject.model.feed.FileVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FileRepository {

  private final FileMapper fileMapper;

  @Transactional
  public void insertFileInfoList(List<FileInfo> fileInfoList) {

    fileMapper.fileListUpload(fileInfoList);
  }

  @Transactional
  public void deleteFile(int feedId) {

    fileMapper.deleteFile(feedId);
  }

  @Transactional
  public void deleteFiles(List<FileDelete> fileDeleteList) {

    fileMapper.deleteFiles(fileDeleteList);
  }

  @Transactional
  public void upsertFiles(List<FileInfo> fileInfoList) {

    fileMapper.upsertFiles(fileInfoList);
  }

  @Transactional(readOnly = true)
  public String findFilePathByFeedId(int feedId) {

    return fileMapper.findFilePathByFeedId(feedId);
  }

  @Transactional(readOnly = true)
  public List<String> findFileNamesByFeedId(int feedId) {

    return fileMapper.findFileNamesByFeedId(feedId);
  }

  @Transactional(readOnly = true)
  public List<FileVo> findFilesByFeedId(int feedId) {

    return fileMapper.findFilesByFeedId(feedId);
  }
}
