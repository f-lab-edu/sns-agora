package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.recommend.FeedCacheUpdateParam;
import com.ht.project.snsproject.model.recommend.RecommendUserDelete;
import com.ht.project.snsproject.model.recommend.RecommendUsersParam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecommendMapper {

  List<String> getRecommendList(int feedId);

  int getRecommend(int feed);

  boolean decrementRecommend(int feedId);

  boolean deleteRecommendUser(RecommendUserDelete recommendUserDelete);

  void updateFeedCacheDb(List<FeedCacheUpdateParam> feedCacheUpdateParamList);

  void updateRecommendUsers(List<RecommendUsersParam> recommendUsersParams);
}
