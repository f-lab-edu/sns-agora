package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.comment.CommentInsertParam;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {

  void insertCommentOnFeed (CommentInsertParam commentInsertParam);
}
