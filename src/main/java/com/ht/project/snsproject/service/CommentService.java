package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.CommentMapper;
import com.ht.project.snsproject.model.comment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

  @Resource(name = "cacheRedisTemplate")
  private ValueOperations<String, Object> valueOps;

  @Autowired
  private RedisCacheService redisCacheService;

  @Autowired
  private CommentService commentService;

  @Transactional
  public void insertCommentOnFeed(int feedId, String content, String userId) {

    commentMapper.insertCommentOnFeed(
            CommentInsertParam.builder()
            .feedId(feedId)
            .userId(userId)
            .content(content)
            .writeTime(Timestamp.valueOf(LocalDateTime.now()))
            .build());

    increaseCommentCountInCache(feedId);
  }

  private void increaseCommentCountInCache(int feedId) {

    commentService.getCommentCount(feedId);
    valueOps.increment(redisCacheService.makeCacheKey(CacheKeyPrefix.COMMENT_COUNT, feedId));

  }

  @Transactional(readOnly = true)
  public List<Comment> getCommentsOnFeed(int feedId, Integer cursor) {

    return commentMapper.getCommentsOnFeed(new CommentsParam(feedId, cursor));
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "commentCount", key = "'commentCount:' + #feedId")
  private int getCommentCount(int feedId) {

    return commentMapper.getCommentCount(feedId);
  }

  @Transactional
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

  @Transactional(readOnly = true)
  public List<Reply> getRepliesOnComment(int commentId,  Integer cursor) {

    return commentMapper.getRepliesOnComment(new RepliesParam(commentId, cursor));
  }

  @Transactional
  public void insertReplyOnComment(int commentId, String content, String userId) {

    commentMapper.insertReplyOnComment(ReplyInsertParam.builder()
            .commentId(commentId)
            .userId(userId)
            .content(content)
            .writeTime(Timestamp.valueOf(LocalDateTime.now()))
            .build());
  }

  @Transactional
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

  /**
   * 외래키 설정을 통해 commentId 와 연관된 대댓글까지 삭제 필요.
   * @param commentId
   * @param userId
   */
  @Transactional
  public void deleteCommentOnFeed(int feedId, int commentId, String userId) {

    if(!commentMapper.deleteCommentOnFeed(new CommentDeleteParam(commentId, userId))) {

      throw new InvalidApproachException("올바르지 않은 접근입니다.");
    }

    decreaseCommentInCache(feedId);
  }

  @Transactional
  private void decreaseCommentInCache(int feedId) {

    getCommentCount(feedId);
    valueOps.decrement(redisCacheService.makeCacheKey(CacheKeyPrefix.COMMENT_COUNT, feedId));
  }

  @Transactional
  public void deleteReplyOnComment(int replyId, String userId) {

    if(!commentMapper.deleteReplyOnComment(new ReplyDeleteParam(replyId, userId))) {

      throw new InvalidApproachException("올바르지 않은 접근입니다.");
    }
  }
}
