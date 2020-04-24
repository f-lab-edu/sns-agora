package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.good.GoodUserDelete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodMapper {

  void decrementGood(int feedId);

  void deleteGoodUser(GoodUserDelete goodUserDelete);

}
