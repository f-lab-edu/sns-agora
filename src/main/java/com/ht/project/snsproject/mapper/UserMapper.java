package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.UserJoin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void join(UserJoin userJoin);
}
