package com.ht.project.snsproject.aop;

import com.ht.project.snsproject.exception.UnauthorizedException;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.model.user.UserVo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;



@Aspect
@Component
public class UserAspect {

  /**
   * 로그인 정보를 확인하고,
   * 타깃 메소드에 userId 의 value 를 주입하는 Advice 이다.
   * getSignature() : 호출되는 메서드에 대한 정보를 구한다. (Signature 타입으로 반환한다.
   * getArgs() : 파라미터의 목록을 구한다.
   * @param joinPoint advice 가 적용될 수 있는 위치.
   *        타깃 오브젝트가 구현한 인터페이스의 모든 메소드는 joinPoint 가 된다.
   */
  @Before("@annotation(com.ht.project.snsproject.annotation.LoginCheck)")
  public void loginCheck(JoinPoint joinPoint) {

    HttpSession httpSession = ((ServletRequestAttributes)RequestContextHolder
            .getRequestAttributes())
            .getRequest()
            .getSession();
    User userInfo = (User) httpSession.getAttribute("userInfo");

    if (userInfo == null) {
      throw new UnauthorizedException("로그인 정보가 존재하지 않습니다.");
    }
  }

  /*
  포인트컷 작성시, execution(* *(.., @User (*), ..))
  이러한 방식으로 작성하면,
  java.lang.IllegalArgumentException: warning no match for this type name: [Xlint:invalidAbsoluteTypeName]
  Exception 을 발생시킨다.
  해당 annotation 외에도 User 라는 이름의 Class 가 존재하여 정확한 Class 를 찾지 못해서 발생하는 것 같다고 생각하여
  어노테이션의 절대 타입을 적용 시키니 해결되었다.
  */
  @Around("execution(* *(.., @com.ht.project.snsproject.annotation.User (*), ..))")
  public Object injectUserSession(ProceedingJoinPoint joinPoint) throws Throwable{
    HttpSession httpSession = ((ServletRequestAttributes)RequestContextHolder
            .getRequestAttributes())
            .getRequest()
            .getSession();
    User userInfo = (User) httpSession.getAttribute("userInfo");
    if (userInfo == null) {
      throw new UnauthorizedException("로그인 정보가 존재하지 않습니다.");
    }

    Object[] args = joinPoint.getArgs();

    for (int i=0; i<args.length; i++) {
      if (args[i] instanceof UserVo) {
        args[i] = UserVo.create(userInfo);
      }
    }
    return joinPoint.proceed(args);
  }
}