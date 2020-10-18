package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.good.GoodUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodBachJobMapper {

  void batchDeleteGoodUserList(List<GoodUser> goodUserDeleteList);

  void batchInsertGoodUserList(List<GoodUser> goodUserAddList);
}
