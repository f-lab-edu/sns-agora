package com.ht.project.snsproject.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeedRecommendCacheJob implements Job {

  @Autowired
  private FeedRecommendCacheService feedRecommendCacheService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    log.info("Job ** {} ** fired @ {}", jobExecutionContext.getJobDetail()
            .getKey().getName(), jobExecutionContext.getFireTime());
    feedRecommendCacheService.executeJob();
    log.info("Next job scheduled @ {}", jobExecutionContext.getNextFireTime());

  }
}
