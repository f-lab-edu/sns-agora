package com.ht.project.snsproject.service;

import java.util.List;

public interface GoodService {

  int getGood(int feedId);

  List<String> getGoodList(int feedId);

  void increaseGood(int feedId, String userId);

  void cancelGood(int feedId, String userId);
}
