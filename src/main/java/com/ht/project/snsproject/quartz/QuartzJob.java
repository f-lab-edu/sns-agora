package com.ht.project.snsproject.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 해당 API 는 Job Interface 의 구현체이다.
 * 오직 execute 하나의 메소들을 가진다.
 * 해당 API 는 반드시 수행할 실제 작업, 즉 task 가 포함된 클래스로 구현되어야 한다.
 * 작업의 trigger 가 발생하면 scheduler 는 execute 메소드를 호출하고,
 * JobExecutionContext 오브젝트를 전달한다.
 * JobExecutionContext : Scheduler, Trigger, JobDetail 등을 포함하여
 *                       Job 인스턴스에 대한 정보를 제공하는 객체이다.
 */
@Slf4j
@Component
public class QuartzJob implements Job {

  @Autowired
  private GoodBatchJobService jobService;

  public void execute(JobExecutionContext context) throws JobExecutionException {

    log.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());
    jobService.executeJob();
    log.info("Next job scheduled @ {}", context.getNextFireTime());
  }
}
