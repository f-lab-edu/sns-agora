package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.comment.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

  void insertCommentOnFeed(CommentInsertParam commentInsertParam);

  List<Comment> getCommentsOnFeed(CommentsParam commentsParam);

  boolean updateCommentOnFeed(CommentUpdateParam commentUpdateParam);

  List<Reply> getRepliesOnComment(RepliesParam repliesParam);

  int getCommentCount(int feedId);

  void insertReplyOnComment(ReplyInsertParam replyInsertParam);

  boolean updateReplyOnComment(ReplyUpdateParam replyUpdateParam);

  boolean deleteCommentOnFeed(CommentDeleteParam commentDeleteParam);

  boolean deleteReplyOnComment(ReplyDeleteParam replyDeleteParam);

  List<CommentCount> findCommentCountList(List<Integer> feedIdCopyList);
}
