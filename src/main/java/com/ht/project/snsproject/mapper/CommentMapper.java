package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.comment.Comment;
import com.ht.project.snsproject.model.comment.CommentInsertParam;
import com.ht.project.snsproject.model.comment.CommentsParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

  void insertCommentOnFeed(CommentInsertParam commentInsertParam);

  List<Comment> getCommentsOnFeed(CommentsParam commentsParam);
}
