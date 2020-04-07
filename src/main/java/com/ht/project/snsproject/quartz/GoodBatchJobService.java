package com.ht.project.snsproject.quartz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class GoodBatchJobService {

  public static final long EXECUTION_TIME = 5000L;

  private AtomicInteger count = new AtomicInteger();

  public void executeJob() {

    log.info("The sample job has begun...");
    try {
      Thread.sleep(EXECUTION_TIME);
    } catch (InterruptedException e) {
      log.error("Error while executing sample job", e);
      throw new RuntimeException();
    } finally {
      count.incrementAndGet();
      log.info("Sample job has finished...");
    }
  }

  public int getNumberOfInvocations() {
    return count.get();
  }
}
