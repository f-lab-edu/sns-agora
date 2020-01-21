package com.ht.project.snsproject.aop;

import com.ht.project.snsproject.model.User;
import com.ht.project.snsproject.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public HttpStatus loginCheck(JoinPoint joinPoint){

        HttpSession httpSession = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        User userInfo = (User) httpSession.getAttribute("userInfo");

        if(userInfo == null) {
            return HttpStatus.UNAUTHORIZED;
        } else {
            return HttpStatus.OK;
        }
    }

}
