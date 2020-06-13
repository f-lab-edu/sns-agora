package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.good.GoodUser;

import java.util.List;
import java.util.Map;

public interface GoodService {

  Integer getGood(int feedId);

  Map<Integer, Integer> getGoods(List<Integer> feedIds);

  Map<Integer, Boolean> getGoodPushedStatusesFromCache(List<Integer> feedIds, String userId);

  List<GoodUser> getGoodList(int feedId, Integer cursor);

  void addGood(int feedId, String userId);

  void cancelGood(int feedId, String userId);

  boolean isGoodPushed(int feedId, String userId);

}
