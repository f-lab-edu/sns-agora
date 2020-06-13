package com.ht.project.snsproject.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 다양한 대상 데이터 소스 중 하나로 라우팅하는 추상 데이터 소스 구현
 * Spring에 있는 AbstractRoutingDataSource 는 여러개의 데이터소스를 하나로 묶고
 * 자동으로 분기처리를 해주는 Spring 기본 클래스이다.
 *
 */
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

  /*
    현재 요청의 연결할 Datasource를 결정할 Key 값을 리턴합니다
   */
  @Override
  protected Object determineCurrentLookupKey() {
    return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "slave" : "master";
  }


}
