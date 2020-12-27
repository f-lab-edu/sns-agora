package com.ht.project.snsproject.model.feed;

import lombok.Value;

@Value
public class FileDelete {

  int feedId;

  String fileName;

  public static FileDelete create(int feedId, String fileName) {
    return new FileDelete(feedId, fileName);
  }
}
