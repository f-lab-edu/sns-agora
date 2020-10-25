package com.ht.project.snsproject.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 데이터베이스 설정.
 *
 * @Configuration : 스프링 자바 설정 파일임을 명시.
 *
 * @MapperScan : 하나씩 매퍼를 빈 목록에 등록할 필요없이 마이바티스 스프링 연동 모듈의 자동스캔 기능 사용.
 *               basePackages 속성은 매퍼 인터페이스 파일이 있는 가장 상위 패키지를 지정.
 *               세미콜론이나 콤마 구분자를 사용하여 한 개 이상의 패키지 선택 가능.
 *               마이바티스 스프링 연동모듈 1.2.0에서 추가된 기능.
 *               스프링 3.1 버전 이상에서 동작.
 *               sqlSessionFactory 와 sqlSessionTemplate 프로퍼티를 사용하여
 *               SqlSessionFactory 와 SqlSessionTemplate 을 사용.
 *
 * @EnableTransactionManagement : 정의되어 있는 어플리케이션 컨텍스트 안에서 빈의 @Transactional 을 찾는다.
 *                                이 설정을 DispatcherServlet 에 대한 WebApplicationContext 에 적용한다면
 *                                서비스가 아닌 컨트롤러 안의 빈만을 찾는다.
 *
 */
@Configuration
@MapperScan(basePackages = "com.ht.project.snsproject.mapper")
@EnableTransactionManagement
public class DatabaseConfig {

  /*
  애플리케이션에서 Quartz 스케줄러를 사용함에 따라 스키마를 2개로 분리하여 사용하게 되었습니다.
  Quartz 는 범용 스케줄러 프레임워크 이므로 다른 애플리케이션 프로젝트에서도 활용 범위가 넓다고 생각하였습니다.
  그렇기 때문에 스키마를 별도로 분리하여 관리하고자 하였습니다.
  스키마가 분리되었기 때문에 DataSource 도 2개가 필요하게 되었고,
  이로 인해 datasource 빈의 이름을 분리하여 주입하게 되었습니다.
  주로 사용하는 mybatis 에 필요한 datasource 의 빈을 primary로 선언하여 주입되게 하였습니다.
   */
  @Value("${spring.datasource.master.url}")
  String masterDbUrl;

  @Value("${spring.datasource.master.username}")
  String masterDbUsername;

  @Value("${spring.datasource.master.password}")
  String masterDbPassword;

  @Value("${spring.datasource.master.driverName}")
  String masterDbDriverName;

  @Value("${spring.datasource.slave.url}")
  String slaveDbUrl;

  @Value("${spring.datasource.slave.username}")
  String slaveDbUsername;

  @Value("${spring.datasource.slave.password}")
  String slaveDbPassword;

  @Value("${spring.datasource.slave.driverName}")
  String slaveDbDriverName;

  @Primary
  @Bean(name = "masterDataSource")
  public DataSource masterDataSource() {

    return DataSourceBuilder.create()
            .url(masterDbUrl)
            .username(masterDbUsername)
            .password(masterDbPassword)
            .type(HikariDataSource.class)
            .driverClassName(masterDbDriverName)
            .build();
  }

  /*
  slaveDataSource 는 읽기 전용 DataSource 로
  @Transactional readOnly 속성을 true 로 설정한다.
  default 는 false 이다.
   */
  @Bean(name = "slaveDataSource")
  public DataSource slaveDataSource() {

    return DataSourceBuilder.create()
            .url(slaveDbUrl)
            .username(slaveDbUsername)
            .password(slaveDbPassword)
            .type(HikariDataSource.class)
            .driverClassName(slaveDbDriverName)
            .build();

  }

  /**
   * routingDataSource 를 생성해서 리턴.
   *결국 routingDataSource 여러 개의 Datasource 객체를 Key, Value 형태로 담고 있고
   * determineCurrentLookupKey라는 메소드에서 리턴하는 Key 값과 매칭되는 Datasource 객체를 반환하게 됩니다.
   * @param masterDataSource
   * @param slaveDataSource
   * @return
   */
  @Bean(name = "routingDataSource")
  public DataSource routingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource, @Qualifier("slaveDataSource") DataSource slaveDataSource) {

    ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();
    Map<Object, Object> dataSourceMap = new HashMap<>();
    dataSourceMap.put("master", masterDataSource);
    dataSourceMap.put("slave", slaveDataSource);
    routingDataSource.setTargetDataSources(dataSourceMap);
    routingDataSource.setDefaultTargetDataSource(masterDataSource);

    return routingDataSource;
  }

  /**
   * 실질적인 쿼리 실행 여부와 상관없이 트랜잭션이 걸리면 무조건 Connection 객체를 확보하는 Spring의 단점을 보완하여
   * 트랜잭션 시작시에 Connection Proxy 객체를 리턴하고 실제로 쿼리가 발생할 때
   * 데이터소스에서 getConnection()을 호출하는 역할을 하는 것.
   *
   * TransactionManager 선별 ->
   * LazyConnectionDataSourceProxy에서 Connection Proxy 객체 획득 ->
   * Transaction 동기화(Synchronization) ->
   * 실제 쿼리 호출시에 ReplicationRoutingDataSource.getConnection()/determineCurrentLookupKey() 호출
   *
   * TransactionManager나 영속 계층 프레임워크는 dataSource 이것만 바라보게 해야한다.
   * writeDataSource, readDatasource, routingDataSource는 설정 속에만 존재할 뿐
   * 영속 계층 프레임워크들에게는 그 존재를 모르게 해야한다.
   * @param routingDataSource
   * @return
   */
  @Bean(name = "dataSource")
  public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {

    return new LazyConnectionDataSourceProxy(routingDataSource);
  }


  @Bean(name = "transactionManager")
  public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {

    return new DataSourceTransactionManager(dataSource);
  }

  /**
   * SqlSessionFactory 는 SqlSession 객체를 생성하기 위한 객체이다.
   * SqlSession 객체를 한번 생성하면 매핑구문을 실행하거나 커밋 또는 롤백을 하기 위해 세션을 사용할수 있다.
   * setMapperLocations 을 설정하여 mapper.xml 파일의 경로를 지정할 수있다.
   * @param dataSource 커넥션 풀의 Connection 을 관리하기 위한 객체,
   *                   DataSource 객체를 통해서 필요한 Connection 을 획득, 반납 등의 작업을 한다.
   * @return SqlSessionFactory
   * @throws Exception if fail to create SqlSessionFactory Object
   */
  @Bean(name = "sqlSessionFactory")
  public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {

    final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    sessionFactory.setMapperLocations(resolver.getResources("classpath:mappers/*.xml"));
    return sessionFactory.getObject();
  }

  /**
   * SqlSessionTemplate 은 마이바티스 스프링 연동모듈의 핵심이다.
   * SqlSessionTemplate 은 SqlSession 을 구현하고 코드에서 SqlSession 를 대체하는 역할을 한다.
   * SqlSessionTemplate 은 쓰레드에 안전하고 여러개의 DAO 나 매퍼에서 공유할수 있다.
   * @param sqlSessionFactory SqlSession 객체를 생성하기 위한 객체.
   * @return SqlSessionTemplate
   * @throws Exception if fail to create SqlSessionTemplate Object
   */
  @Bean(name = "sqlSessionTemplate")
  public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory)
          throws Exception {

    return new SqlSessionTemplate(sqlSessionFactory);
  }
}
