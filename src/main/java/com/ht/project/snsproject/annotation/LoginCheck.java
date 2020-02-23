package com.ht.project.snsproject.annotation;

import java.lang.annotation.*;

/**
 * 이 애노테이션을 사용하면 메소드 실행시,
 * 로그인 여부를 체크합니다.
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface LoginCheck {
}


