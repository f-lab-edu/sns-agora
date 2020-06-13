package com.ht.project.snsproject.quartz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class GoodBatchJobService {

  private AtomicInteger count = new AtomicInteger();

  @Transactional
  public void executeJob() {

    log.info("The batch job has begun...");

    count.incrementAndGet();
    log.info("Batch job has finished...");

  }

  public int getNumberOfInvocations() {
    return count.get();
  }
}
