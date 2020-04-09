package com.ht.project.snsproject.quartz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class GoodBatchJobService {

  private AtomicInteger count = new AtomicInteger();

  public void executeJob() {

    log.info("The sample job has begun...");
    System.out.println("스케줄러 작동 테스트");
    count.incrementAndGet();
    log.info("Sample job has finished...");

  }

  public int getNumberOfInvocations() {
    return count.get();
  }
}
