package com.ht.project.snsproject.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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


  @Value("${spring.datasource.url}")
  String url;

  @Value("${spring.datasource.username}")
  String userName;

  @Value("${spring.datasource.password}")
  String password;

  @Value("${spring.datasource.driver-class-name}")
  String driverClassName;

  /*
  애플리케이션에서 Quartz 스케줄러를 사용함에 따라 스키마를 2개로 분리하여 사용하게 되었습니다.
  Quartz 는 범용 스케줄러 프레임워크 이므로 다른 애플리케이션 프로젝트에서도 활용 범위가 넓다고 생각하였습니다.
  그렇기 때문에 스키마를 별도로 분리하여 관리하고자 하였습니다.
  스키마가 분리되었기 때문에 DataSource 도 2개가 필요하게 되었고,
  이로 인해 datasource 빈의 이름을 분리하여 주입하게 되었습니다.
  주로 사용하는 mybatis 에 필요한 datasource 의 빈을 primary로 선언하여 주입되게 하였습니다.
   */
  @Bean(name = "db1DataSource")
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource dataSource() {

    return DataSourceBuilder.create()
            .url(url)
            .username(userName)
            .password(password)
            .type(HikariDataSource.class)
            .driverClassName(driverClassName)
            .build();
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
  @Bean
  @Primary
  public SqlSessionFactory sqlSessionFactory(@Qualifier("db1DataSource") DataSource dataSource) throws Exception {

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
  @Bean
  @Primary
  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory)
          throws Exception {

    final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
    return sqlSessionTemplate;
  }
}
