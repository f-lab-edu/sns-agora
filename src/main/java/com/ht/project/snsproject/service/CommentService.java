package com.ht.project.snsproject.service;

import com.ht.project.snsproject.mapper.CommentMapper;
import com.ht.project.snsproject.model.comment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * interface가 꼭 필요한 곳에만 추상화하기.
 * 기계적인 추상화는 오히려 코드 가독성을 떨어뜨리고,
 * 유지보수성을 떨어뜨릴 수 있음.
 */
@Service
public class CommentService{

  @Autowired
  private CommentMapper commentMapper;

  public void insertCommentOnFeed(int feedId, String content, String userId) {

    commentMapper.insertCommentOnFeed(
            CommentInsertParam.builder()
            .feedId(feedId)
            .userId(userId)
            .content(content)
            .writeTime(Timestamp.valueOf(LocalDateTime.now()))
            .build());
  }

  public List<Comment> getCommentsOnFeed(int feedId, Integer cursor) {

    return commentMapper.getCommentsOnFeed(new CommentsParam(feedId, cursor));
  }

  public void updateCommentOnFeed(int commentId, String userId, String content) {

    if(!commentMapper.updateCommentOnFeed(CommentUpdateParam.builder()
            .commentId(commentId)
            .userId(userId)
            .content(content)
            .updateTime(Timestamp.valueOf(LocalDateTime.now()))
            .build())) {

      throw new IllegalArgumentException("해당 Comment가 존재하지 않습니다.");
    }
  }

  public List<Reply> getRepliesOnComment(int commentId,  Integer cursor) {

    return commentMapper.getRepliesOnComment(new RepliesParam(commentId, cursor));
  }

  public void insertReplyOnComment(int commentId, String content, String userId) {

    commentMapper.insertReplyOnComment(ReplyInsertParam.builder()
            .commentId(commentId)
            .userId(userId)
            .content(content)
            .writeTime(Timestamp.valueOf(LocalDateTime.now()))
            .build());
  }

  public void updateReplyOnComment(int replyId, String userId, String content) {

    if(!commentMapper.updateReplyOnComment(ReplyUpdateParam.builder()
            .replyId(replyId)
            .userId(userId)
            .content(content)
            .updateTime(Timestamp.valueOf(LocalDateTime.now()))
            .build())) {

      throw new IllegalArgumentException("해당 Reply가 존재하지 않습니다.");
    }
  }
}
