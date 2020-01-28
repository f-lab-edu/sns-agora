package com.ht.project.snsproject.aop;

import com.ht.project.snsproject.model.User;
import com.ht.project.snsproject.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;


@Aspect
@Component
public class LoginCheckAspect {

    @Autowired
    UserService userService;

    @Before("@annotation(com.ht.project.snsproject.annotation.LoginCheck)")
    public void loginCheck(JoinPoint joinPoint) throws UnauthorizedException {

        HttpSession httpSession = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        User userInfo = (User) httpSession.getAttribute("userInfo");

        if(userInfo == null) {
            throw new UnauthorizedException("로그인 정보가 존재하지 않습니다.");
        }
    }

}

/*
 로그인 인증이 되어있지 않을 때 발생하는 Exception 정의
 */
class UnauthorizedException extends Exception {
    public UnauthorizedException(String message){
        super(message);
    }
}