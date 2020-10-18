package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.good.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodMapper {

  int getGood(int feedId);

  boolean getGoodPushedStatus(GoodStatusParam goodStatusParam);

  List<GoodUser> getGoodList(GoodListParam goodListParam);

  List<Good> getGoods(List<Integer> feedIds);

}
