package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.good.FeedCacheUpdateParam;
import com.ht.project.snsproject.model.good.GoodUserDelete;
import com.ht.project.snsproject.model.good.GoodUsersParam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodMapper {

  List<String> getGoodList(int feedId);

  int getGood(int feed);

  boolean decrementGood(int feedId);

  boolean deleteGoodUser(GoodUserDelete recommendUserDelete);

  void updateFeedCacheDb(List<FeedCacheUpdateParam> feedCacheUpdateParamList);

  void updateGoodUsers(List<GoodUsersParam> recommendUsersParams);
}
