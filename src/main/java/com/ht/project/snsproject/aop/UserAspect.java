package com.ht.project.snsproject.aop;

import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.exception.UnauthorizedException;
import com.ht.project.snsproject.model.user.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;



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
   * @return result advice 가 적용된 메소드의 결과값을 반환
   * @throws Throwable
   */
  @Around("@annotation(com.ht.project.snsproject.annotation.LoginCheck)")
  public Object loginCheck(ProceedingJoinPoint joinPoint) throws Throwable {

    HttpSession httpSession = ((ServletRequestAttributes)RequestContextHolder
            .getRequestAttributes())
            .getRequest()
            .getSession();
    User userInfo = (User) httpSession.getAttribute("userInfo");
    if (userInfo == null) {
      throw new UnauthorizedException("로그인 정보가 존재하지 않습니다.");
    }

    Object[] parameterValues = joinPoint.getArgs();
    String parameterName;

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    for (int i=0; i<method.getParameterCount(); i++) {
      parameterName = method.getParameters()[i].getName();
      if (parameterName.equals("userId")) {

        parameterValues[i] = userInfo.getUserId();
      }
    }

    return joinPoint.proceed(parameterValues);
  }
}