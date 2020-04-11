package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.good.GoodUserDelete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodMapper {

  int hasFeedId(int feedId);

  List<String> getGoodList(int feedId);

  int getGood(int feed);

  boolean decrementGood(int feedId);

  boolean deleteGoodUser(GoodUserDelete goodUserDelete);

}
