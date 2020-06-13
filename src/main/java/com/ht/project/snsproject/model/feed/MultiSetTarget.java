package com.ht.project.snsproject.model.feed;

import lombok.Builder;
import lombok.Value;

/**
 * 이거는 패키지도 분리, repository 까지도 나눠서
 * 라이브러리화 해볼 만한 내용
 * 추후에 진행해보기.
 */
@Value
@Builder
public class MultiSetTarget {

  String key;

  String target;

  long expire;
}
