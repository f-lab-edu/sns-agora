package com.ht.project.snsproject.service;

import com.ht.project.snsproject.mapper.CommentMapper;
import com.ht.project.snsproject.model.comment.CommentInsertParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService{

  @Autowired
  private CommentMapper commentMapper;

  @Override
  public void insertCommentOnFeed(int id, String content, String userId) {

    commentMapper.insertCommentOnFeed(
            CommentInsertParam.builder()
            .feedId(id)
            .userId(userId)
            .content(content)
            .writeTime(Timestamp.valueOf(LocalDateTime.now()))
            .build());
  }
}
