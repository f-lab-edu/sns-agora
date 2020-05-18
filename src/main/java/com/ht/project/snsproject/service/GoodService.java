package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.good.GoodList;

import java.util.List;

public interface GoodService {

  Integer getGood(int feedId);

  List<GoodList> getGoodList(int feedId, Integer cursor);

  void addGood(int feedId, String userId);

  void cancelGood(int feedId, String userId);
}
