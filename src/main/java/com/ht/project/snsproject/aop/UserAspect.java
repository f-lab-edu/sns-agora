package com.ht.project.snsproject.aop;

import com.ht.project.snsproject.annotation.UserInfo;
import com.ht.project.snsproject.exception.UnauthorizedException;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class UserAspect {

  private final UserService userService;

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

    HttpSession httpSession = ((ServletRequestAttributes)RequestContextHolder
            .getRequestAttributes())
            .getRequest()
            .getSession();

    String userId = (String) httpSession.getAttribute("userId");

    if (userId == null) {
      throw new UnauthorizedException("로그인 정보가 존재하지 않습니다.");
    }

    User userInfo = userService.findUserByUserId(userId);

    Object[] args = joinPoint.getArgs();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    /*
    annotation[argument][annotation] 형태로
    1차 배열의 index 는 argument 의 갯수 입니다.
    2차 배열의 index 는 annotation 의 갯수 입니다.
    즉, public a (@Test1 @Test2 String hello, @Test3 int num) { }
    이라는 method 가 존재할 때, annotation[0][0] 는 @Test1 를 의미 합니다.
    annotation[0][1] 은 @Test2 를 의미하며 마지막으로 annotation[1][0] 는 @Test3 을 의미합니다.
    그러므로 annotation 이차원 배열을 비교하면 parameter 에 적용된 annotation 을 가져올 수 있습니다.
    joinPoint interface 로 가져온 args 배열의 index 와 annotation argument 의 1차 index 는 동일하기 때문에
    적용된 annotation 에 따라 index 에 해당하는 argument 를 구하여 aop 로 값을 주입할 수 있습니다.
    이에 따라 Annotation 의 instance 를 비교해서 args 객체의 user 캐시를 aop 로 주입해 줄 수 있습니다.
     */
    Annotation[][] annotations = method.getParameterAnnotations();

    for(int i=0; i<annotations.length; i++) {
      for(Annotation annotation : annotations[i]) {
        if(annotation instanceof UserInfo) {
          args[i] = userInfo;
          break;
        }
      }
    }

    return joinPoint.proceed(args);
  }
}