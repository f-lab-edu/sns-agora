package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.good.GoodUser;

import java.util.List;

public interface GoodService {

  List<GoodUser> getGoodList(int feedId, Integer cursor);

  int getGood(int feedId);

  void addGood(int feedId, String userId);

  void cancelGood(int feedId, String userId);

  boolean isGoodPushed(int feedId, String userId);

}
