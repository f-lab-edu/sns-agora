package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.good.GoodStatusPram;
import com.ht.project.snsproject.model.good.GoodUserDelete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodMapper {

  int getGood(int feedId);

  void deleteGoodUser(GoodUserDelete goodUserDelete);

}
