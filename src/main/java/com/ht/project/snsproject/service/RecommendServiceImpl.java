package com.ht.project.snsproject.service;

import com.ht.project.snsproject.mapper.RecommendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    RecommendMapper recommendMapper;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Override
    @Cacheable(value="feeds", key="recommend:"+"#feedId")
    public int getRecommend(int feedId) {

        return recommendMapper.getRecommend(feedId);
    }
    @Override
    @Cacheable(value="feeds", key="recommendList:"+"#feedId")
    public List<String> getRecommendList(int feedId){
        return recommendMapper.getRecommendList(feedId);
    }


/*    @Scheduled(fixedRate = 1000 * 60 * 60)//1시간마다 수행(1 sec/1000)
    public void updateFeedCacheDb(){

        Set<String> keys = new HashSet<>();
        List<FeedCacheUpdateParam> feedCacheUpdateParamList = new ArrayList<>();
        RedisConnection redisConnection = null;

        try {
            redisConnection = redisTemplate.getConnectionFactory().getConnection();
            ScanOptions options = ScanOptions.scanOptions().match("feeds:*").count(10).build();
            Cursor<byte[]> cursor = redisConnection.scan(options);
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
        } finally {
            redisConnection.close();
        }
        if(!keys.isEmpty()) {
            for (String key : keys) {
                BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps(key);
                String[] keyName = key.split(":");
                int feedId = Integer.parseInt(keyName[1]);


                Map<?, ?> cache = (Map<?, ?>) boundValueOperations.get();
                List<?> date = (List<?>) cache.get("date");
                Timestamp timestamp = new Timestamp((Long) date.get(1));

                feedCacheUpdateParamList.add(FeedCacheUpdateParam.builder()
                        .feedId(feedId)
                        .userId((String) cache.get("userId"))
                        .title((String) cache.get("title"))
                        .content((String) cache.get("content"))
                        .date(timestamp)
                        .publicScope(PublicScope.valueOf((String) cache.get("publicScope")))
                        .recommend((Integer) cache.get("recommend"))
                        .build());
            }

            recommendMapper.updateFeedCacheDb(feedCacheUpdateParamList);
        }
    }
*/

    public void initRecommendList(int feedId){
        recommendMapper.initRecommendList(feedId);
    }

/*    public List<String> getRecommendList(int feedId){
        BoundListOperations<String, Object> boundListOperations = redisTemplate.boundListOps("recommends:"+feedId);
        Long size = boundListOperations.size();
        if(size!=null){
            List<String> userIds = (List<String>) boundListOperations;
            recommendMapper.updateRecommendUsers(new RecommendUserUpdateParam(feedId, userIds));
        }
        return recommendMapper.getRecommendList(feedId);
    }
*/
}
