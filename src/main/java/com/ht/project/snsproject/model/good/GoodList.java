package com.ht.project.snsproject.model.good;

import lombok.Value;

import java.util.List;

@Value
public class GoodList {

  List<String> users;

  Integer nextPage;
}
