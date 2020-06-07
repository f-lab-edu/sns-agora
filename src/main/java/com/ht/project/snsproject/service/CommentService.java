package com.ht.project.snsproject.service;

/**
 * 댓글을 저장할 Main DataBase 가 MySQL 이지만
 * 이 후에 NoSQL 이나 다른 종류의 DataBase로 변경할 가능성이
 * 있기 때문에 전략패턴을 사용하여 느슨한 결합도를 유지합니다.
 */
public interface CommentService {

  void insertCommentOnFeed(int id, String content, String userId);
}
