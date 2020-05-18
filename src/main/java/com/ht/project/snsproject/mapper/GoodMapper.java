package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.good.GoodList;
import com.ht.project.snsproject.model.good.GoodListParam;
import com.ht.project.snsproject.model.good.GoodUserDelete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodMapper {

  int getGood(int feedId);

  List<GoodList> getGoodList(GoodListParam goodListParam);

  void deleteGoodUser(GoodUserDelete goodUserDelete);

}
