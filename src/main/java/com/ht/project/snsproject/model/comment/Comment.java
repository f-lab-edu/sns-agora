package com.ht.project.snsproject.model.comment;

import lombok.NoArgsConstructor;
import lombok.Value;

import java.sql.Timestamp;

/**
 * 필드들이 final로 생성되어 있는 경우에는 필드를 초기화 할 수 없기 때문에
 * 생성자를 만들 수 없고 Exception이 발생하게 됩니다.
 * 이 때, @NoArgsConstructor(force = true) 옵션을 이용해서
 * final 필드를 0, false, null 등으로 초기화를 강제로 시켜서 생성자를 만들 수 있습니다.
 *
 * 주의햘 점은 @NonNull 같이 필드에 제약조건이 설정되어 있는 경우,
 * 생성자내 null-check 로직이 생성되지 않습니다.
 * 후에 초기화를 진행하기 전까지 null-check 로직이 발생하지 않는 점을 염두하고 코드를 개발해야 합니다.
 */
@Value
@NoArgsConstructor(force = true)
public class Comment {

  int id;

  int feedId;

  String userId;

  String content;

  Timestamp writeTime;

  Reply reply;
}
