package com.ht.project.snsproject.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

/**
 * 데이터베이스 설정
 *
 * @Configuration 애노테이션
 * 스프링 자바 설정 파일임을 명시
 *
 * @MapperScan
 * 하나씩 매퍼를 빈 목록에 등록할 필요없이 마이바티스 스프링 연동 모듈의 자동스캔 기능 사용.
 * basePackages 속성은 매퍼 인터페이스 파일이 있는 가장 상위 패키지를 지정.
 * 세미콜론이나 콤마 구분자를 사용하여 한 개 이상의 패키지 선택 가능.
 * 마이바티스 스프링 연동모듈 1.2.0에서 추가된 기능.
 * 스프링 3.1 버전 이상에서 동작
 * sqlSessionFactory와 sqlSessionTemplate 프로퍼티를 사용하여 SqlSessionFactory와 SqlSessionTemplate을 사용.
 *
 * @EnableTransactionManagement
 * 정의되어 있는 어플리케이션 컨텍스트 안에서 빈의 @Transactional 을 찾는다.
 * 이 설정을 DispatcherServlet 에 대한 WebApplicationContext 에 적용한다면 서비스가 아닌 컨트롤러 안의 빈만을 찾는다.
 *
 */
@Configuration
@MapperScan(basePackages = "com.ht.project.snsproject.mapper")
@EnableTransactionManagement
public class DatabaseConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{

        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mappers/*.xml"));
        return sessionFactory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory)
            throws Exception {
        final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        return sqlSessionTemplate;
    }
}
