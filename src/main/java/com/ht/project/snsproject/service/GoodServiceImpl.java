package com.ht.project.snsproject.service;

import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.GoodMapper;
import com.ht.project.snsproject.model.good.GoodUserDelete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GoodServiceImpl implements GoodService {

  @Autowired
  GoodMapper goodMapper;

  /*
  @Resource 를 통한 빈 주입이 아닌 @Qualifier 를 통한 부가정보를 사용한 이유
  - @Resource 를 활용할 때, 빈 이름은 변경되기가 쉽고 그 자체로 의미 부여가 쉽지 않음.
  - 빈 이름과는 별도로 추가적인 메타정보를 지정해서 의미를 부여해놓고 @Autowired에서 사용할 수 있도록 하는
    @Autowired 가 훨씬 직관적이고 깔끔하다.
  - 아래와 같이 선언시, goodRedisTemplate 이라는 한정자 값을 가진 빈으로 자동와이어링 대상을 제한할 수 있다.
   */
  @Autowired
  @Qualifier("goodRedisTemplate")
  RedisTemplate<String, Object> goodRedisTemplate;

  /*
  Redis2 에서 good:{feedId} 에 해당하는 값을 가져온다.
   */
  @Override
  public int getGood(int feedId) {

    String goodKey = "good:"+feedId;

    Integer good = (Integer) goodRedisTemplate.boundValueOps(goodKey).get();

    if(good==null){
      throw new IllegalArgumentException("해당 피드가 존재하지 않습니다.");
    }

    return good;
  }

  /*
  zset 페이징 처리 고려.
   */
  @Override
  public List<String> getGoodList(int feedId) {

    String goodListKey = "goodList:" + feedId;
    Set<Object> goodListSet;

    if (goodRedisTemplate.hasKey(goodListKey) == null) {
      throw new NoSuchElementException("목록이 존재하지 않습니다.");
    }

    goodListSet = goodRedisTemplate.boundZSetOps(goodListKey).reverseRange(0, -1);
    return goodListSet.stream().map(x -> (String) x).collect(Collectors.toList());
  }

  /*
  1. goodList:{feedId} 에 contains userId 여부 확인.
  2. userId 가 이미 존재하면 exception
  3. good:{feedId} incr 명령어 수행.
  4. goodList:{feedId} add(userId, 현재시간(스코어)) 명령어 수행.

  redis atomicity 고려.
  lua script 활용 및 redis transaction 사용 공부중입니다.
  둘 다 비교 후 적용해보겠습니다.
   */
  @Override
  public void addGood(int feedId, String userId) {

    String goodKey = "good:"+feedId;

    addGoodUser(feedId, userId);

    goodRedisTemplate.opsForValue().increment(goodKey);
  }

  public void addGoodUser(int feedId, String userId) {

    String goodListKey = "goodList:" + feedId;
    long date = Timestamp.valueOf(LocalDateTime.now()).getTime();

    if(goodRedisTemplate.opsForZSet().rank(goodListKey, userId) != null) {
      throw new DuplicateRequestException("중복된 요청입니다.");
    }

    goodRedisTemplate.opsForZSet().add(goodListKey, userId, date);
  }

  /*
  1. good:{feedId} 가 '0' 여부 확인.
  2. '0' 이면 예외 발생.
  3. '0' 이 아니면 goodList:{feedId}에서 userId 존재 유무 확인.
  4. userId 가 존재하지 않으면 예외 발생.
  5. userId 가 존재하면 redis2, mysql 에서 삭제 처리.
  6. redis2 의 good:{feedId} 를 decr 수행.
   */
  @Transactional
  @Override
  public void cancelGood(int feedId, String userId) {

    String goodKey = "good:" + feedId;
    String goodListKey = "goodList:" +feedId;
    int good = getGood(feedId);
    GoodUserDelete goodUserDelete = new GoodUserDelete(feedId, userId);

    if ((good == 0) || (goodRedisTemplate.opsForZSet().rank(goodListKey, userId) == null)) {
      throw new InvalidApproachException("비정상적인 요청입니다.");
    }

    goodRedisTemplate.opsForZSet().remove(goodListKey, userId);
    goodMapper.deleteGoodUser(goodUserDelete);
    goodRedisTemplate.opsForValue().decrement(goodKey);
    goodMapper.decrementGood(feedId);

  }
}
