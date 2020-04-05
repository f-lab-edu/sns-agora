package com.ht.project.snsproject.model.recommend;

import lombok.Value;

import java.util.List;

@Value
public class RecommendUsersParam {

    int feedId;

    List<Object> recommendUsers;
}
