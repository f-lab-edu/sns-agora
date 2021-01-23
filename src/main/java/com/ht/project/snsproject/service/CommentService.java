package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.comment.Comment;
import com.ht.project.snsproject.model.comment.Reply;
import com.ht.project.snsproject.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * interface가 꼭 필요한 곳에만 추상화하기.
 * 기계적인 추상화는 오히려 코드 가독성을 떨어뜨리고,
 * 유지보수성을 떨어뜨릴 수 있음.
 */
@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  @Transactional
  public void insertCommentOnFeed(int feedId, String content, String userId) {

    commentRepository.insertCommentOnFeed(feedId, content, userId);
    commentRepository.getCommentCount(feedId);
    commentRepository.increaseCommentCountInCache(feedId);
  }

  @Transactional(readOnly = true)
  public List<Comment> getCommentsOnFeed(int feedId, Integer cursor) {

    return commentRepository.getCommentsOnFeed(feedId, cursor);
  }

  @Transactional
  public void updateCommentOnFeed(int commentId, String userId, String content) {

    commentRepository.updateCommentOnFeed(commentId, userId, content);
  }

  @Transactional(readOnly = true)
  public List<Reply> getRepliesOnComment(int commentId, Integer cursor) {

    return commentRepository.getRepliesOnComment(commentId, cursor);
  }

  @Transactional
  public void insertReplyOnComment(int commentId, String content, String userId) {

    commentRepository.insertReplyOnComment(commentId, content, userId);
  }

  @Transactional
  public void updateReplyOnComment(int replyId, String userId, String content) {

    commentRepository.updateReplyOnComment(replyId, userId, content);
  }

  @Transactional
  public void deleteReplyOnComment(int replyId, String userId) {

    commentRepository.deleteReplyOnComment(replyId, userId);
  }

  /**
   * 외래키 설정을 통해 commentId 와 연관된 대댓글까지 삭제 필요.
   * @param commentId
   * @param userId
   */
  @Transactional
  public void deleteCommentOnFeed(int feedId, int commentId, String userId) {

    commentRepository.deleteCommentOnFeed(commentId, userId);
    commentRepository.getCommentCount(feedId);
    commentRepository.decreaseCommentInCache(feedId);
  }
}
