package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.good.GoodList;

public interface GoodService {

  Integer getGood(int feedId);

  GoodList getGoodList(int feedId, long cursor);

  void addGood(int feedId, String userId);

  void cancelGood(int feedId, String userId);
}
