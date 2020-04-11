package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.good.FeedCacheUpdateParam;
import com.ht.project.snsproject.model.good.GoodUsersParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodBachJobMapper {

  void batchUpdateFeedInfo(List<FeedCacheUpdateParam> feedCacheUpdateParams);

  void batchUpdateGoodUserList(List<GoodUsersParam> goodUsersParams);
}
