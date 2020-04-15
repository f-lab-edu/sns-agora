package com.ht.project.snsproject.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.annotation.UserInfo;
import com.ht.project.snsproject.exception.UnauthorizedException;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.model.user.UserCache;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
public class UserAspect {


  @Autowired
  StringRedisTemplate redisTemplate;
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
    
    String userId = (String) httpSession.getAttribute("userId");

    if (userId == null) {
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

  @Around("execution(* *(.., @com.ht.project.snsproject.annotation.UserInfo (*), ..))")
  public Object injectUserSession(ProceedingJoinPoint joinPoint) throws Throwable{

    ObjectMapper mapper = new ObjectMapper();
    HttpSession httpSession = ((ServletRequestAttributes)RequestContextHolder
            .getRequestAttributes())
            .getRequest()
            .getSession();

    String userId = (String) httpSession.getAttribute("userId");

    if (userId == null) {
      throw new UnauthorizedException("로그인 정보가 존재하지 않습니다.");
    }

    User userInfo = User.create(
            mapper.readValue(
                    redisTemplate.boundValueOps("userInfo:"+userId).get(), UserCache.class));

    Object[] args = joinPoint.getArgs();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    /*
    Annotation[] 에는 입력된 argument 가 대입 되고,
    Annotation[][] 에는 각 Argument 를 감싸고 있는 Annotation 을 가져올 수 있습니다.
    Annotation 의 instance 를 비교해서 user 캐시를 aop 로 주입해 줄 수 있습니다.
     */
    Annotation[][] annotations = method.getParameterAnnotations();

    for (int i = 0; i < args.length; i++) {
      for (Annotation annotation : annotations[i]) {
        if (annotation instanceof UserInfo) {
          args[i] = userInfo;
          break;
        }
      }
    }

    return joinPoint.proceed(args);
  }
}