package com.ht.project.snsproject.repository.comment;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.CommentMapper;
import com.ht.project.snsproject.model.comment.*;
import com.ht.project.snsproject.model.feed.MultiSetTarget;
import com.ht.project.snsproject.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class CommentRepository {

  private final CommentMapper commentMapper;

  private final RedisCacheService redisCacheService;

  private final RedisTemplate<String, Object> redisTemplate;

  private final StringRedisTemplate stringRedisTemplate;

  public CommentRepository(CommentMapper commentMapper,
                           RedisCacheService redisCacheService,
                           @Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                           @Qualifier("cacheStrRedisTemplate") StringRedisTemplate stringRedisTemplate) {
    this.commentMapper = commentMapper;
    this.redisCacheService = redisCacheService;
    this.redisTemplate = redisTemplate;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Cacheable(value = "commentCount", key = "'commentCount:' + #feedId")
  public int getCommentCount(int feedId) {

    return commentMapper.getCommentCount(feedId);
  }

  public List<Comment> getCommentsOnFeed(int feedId, Integer cursor) {

    return commentMapper.getCommentsOnFeed(new CommentsParam(feedId, cursor));
  }

  public Map<Integer, Integer> findCommentCountMap(List<Integer> feedIdList, List<MultiSetTarget> multiSetTargetList) {

    Map<Integer, Integer> commentCountMap = new HashMap<>();
    List<Integer> feedIdCopyList = new ArrayList<>(feedIdList);
    List<CommentCount> commentCountList = new ArrayList<>();

    findCommentCountListInCache(feedIdCopyList, commentCountList);

    if (!feedIdCopyList.isEmpty()) {
      commentCountList.addAll(findCommentCountList(feedIdList));
    }
    commentCountList.forEach(commentCount ->
            commentCountMap.put(commentCount.getFeedId(), commentCount.getCommentCount()));

    redisCacheService.addCommentCountListInCacheList(commentCountList, multiSetTargetList);

    return commentCountMap;

  }

  public List<CommentCount> findCommentCountList(List<Integer> feedIdList) {

    return commentMapper.findCommentCountList(feedIdList);
  }

  private void findCommentCountListInCache(List<Integer> feedIdList, List<CommentCount> commentCountList) {

    List<String> cacheKeys = redisCacheService.makeMultiKeyList(CacheKeyPrefix.COMMENT_COUNT, feedIdList);
    List<String> commentCountCacheList = stringRedisTemplate.opsForValue().multiGet(cacheKeys);

    if(commentCountCacheList != null) {
      for (int i=0; i<commentCountCacheList.size(); i++) {

        String commentCount = commentCountCacheList.get(i);

        if(commentCount != null) {

          commentCountList.add(new CommentCount(feedIdList.get(i), Integer.parseInt(commentCount)));
          feedIdList.set(i, null);
        }
      }
    }

    feedIdList.removeIf(Objects::isNull);
  }

  public void insertCommentOnFeed(int feedId, String content, String userId) {

    commentMapper.insertCommentOnFeed(
            CommentInsertParam.builder()
                    .feedId(feedId)
                    .userId(userId)
                    .content(content)
                    .writeTime(Timestamp.valueOf(LocalDateTime.now()))
                    .build());
  }

  public void increaseCommentCountInCache(int feedId) {

    redisTemplate.opsForValue()
            .increment(redisCacheService.makeCacheKey(CacheKeyPrefix.COMMENT_COUNT, feedId));
  }

  public void deleteCommentOnFeed(int commentId, String userId) {

    if(!commentMapper.deleteCommentOnFeed(new CommentDeleteParam(commentId, userId))) {

      throw new InvalidApproachException("올바르지 않은 접근입니다.");
    }
  }

  public void decreaseCommentInCache(int feedId) {

    redisTemplate.opsForValue()
            .decrement(redisCacheService.makeCacheKey(CacheKeyPrefix.COMMENT_COUNT, feedId));
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

  public List<Reply> getRepliesOnComment(int commentId, Integer cursor) {

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

  public void deleteReplyOnComment(int replyId, String userId) {

    if(!commentMapper.deleteReplyOnComment(new ReplyDeleteParam(replyId, userId))) {

      throw new InvalidApproachException("올바르지 않은 접근입니다.");
    }
  }
}
