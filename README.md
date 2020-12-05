# :clapper: AGORA

## :pushpin: 개요

:heavy_check_mark: Instagram, Facebook 과 같이 평소 사용하던 SNS를 직접 제작하여보기    
:heavy_check_mark: 실행만 되는 서비스가 아닌 대규모 트래픽을 처리할 수 있는 성능적으로 우수한 서비스 제작하기    
:heavy_check_mark: 프로젝트 진행에 따라 ReadMe 업데이트 예정      

## :pushpin: 사용 기술 및 개발환경

[![framework](https://img.shields.io/badge/spring%20boot-2.2.2-yellowgreen)](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.2-Release-Notes) [![build tool](https://img.shields.io/badge/maven-2.5.3-orange)](https://maven.apache.org/) [![mybatis](https://img.shields.io/badge/MyBatis-3.5.4-blue)](https://mybatis.org/mybatis-3/ko/index.html) [![lettuce](https://img.shields.io/badge/lettuce-5.2.1-brightgreen)](https://lettuce.io/) [![mysql](https://img.shields.io/badge/MySQL-8.0-blue)](https://dev.mysql.com/doc/refman/8.0/en/) ![java](https://img.shields.io/badge/open--jdk-8-brightgreen) ![intellij](https://img.shields.io/badge/IntelliJ-3.0-orange) [![checkstyle](https://img.shields.io/badge/codestyle-Google%20CheckStyle-yellow)](https://checkstyle.sourceforge.io/google_style.html) [![Build Status](https://img.shields.io/badge/build-passing-green)](http://27.96.135.12:8080/job/agora-ci/job/issue%252F87/) [![image](https://img.shields.io/badge/docker-latest-lightgrey)](https://hub.docker.com/r/tax1116/agora) ![OS](https://img.shields.io/badge/ubuntu-16.04-red)    

## :pushpin: AGORA 구성도
![agora_archetecture_resize](https://user-images.githubusercontent.com/54772162/101143570-6b4b8500-365a-11eb-8b8e-64c5c756aaef.PNG)

## :pushpin: 브랜치 관리 전략

* AGORA는 Git-Flow 를 이용하여 브랜치를 관리하였습니다.

![git_flow](https://user-images.githubusercontent.com/54772162/101170794-45d27180-3682-11eb-8c42-6f4bf8ec73c9.PNG)

:heavy_check_mark: master : 배포시 사용할 브랜치. 초기 시행착오에 의하여 몇몇 기능이 merge 되어 있으나, 원래 사용 용도는 완벽히 배포가 가능한 상태에만 merge가 되어야만 합니다.        
:heavy_check_mark: develop : 다음 버전을 개발하는 브랜치, 완전히 배포가 가능하다고 생각되면 master 브랜치에 merge 합니다.    
:heavy_check_mark: feature : 기능을 개발하는 브랜치    
:heavy_check_mark: release : 배포를 준비할 때 사용할 브랜치    
:heavy_check_mark: hotfix : 배포 후에 발생한 버그를 수정 하는 브랜치    

#### 참고 사이트

* 우린 Git-flow를 사용하고 있어요, 우아한 형제들 기술 블로그, Oct 30, 2017, 나동호  
  https://woowabros.github.io/experience/2017/10/30/baemin-mobile-git-branch-strategy.html

## :pushpin: 프로젝트 중점사항

* 자세한 내용을 확인하시려면 Wiki에서 확인하실 수 있습니다.
현재는 작업 중으로 일부 내용이 누락되어 있을 수 있습니다.

### 대규모 트래픽을 고려한 설계 및 성능 튜닝 작업

:heavy_check_mark: 세션 서버와 캐시 서버를 분리하여 서버의 부하를 분산하고자 노력하였습니다.    
:heavy_check_mark: 캐싱을 활용하여 DB Connection을 줄이고자 노력하였습니다.    
:heavy_check_mark: 쿼리 튜닝을 통해 Full-Table Scan이 발생하는 상황을 줄이고자 노력하였습니다.    
:heavy_check_mark: 스케줄러를 활용하여 Batch Insert를 구현함으로써 DB Connection을 줄이고자 노력하였습니다.    

Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/04.-Large-Traffic-Handling-Task

### 리펙토링을 통한 코드 개선 작업

Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/05.-Refactoring-Task

## :pushpin: API Reference

* API에 대한 자세한 내용을 확인하시려면 Wiki를 참고해주세요.    
https://github.com/f-lab-edu/sns-project/wiki

## :pushpin: CI 프로세스
![ci_process](https://user-images.githubusercontent.com/54772162/101181687-8802af80-3690-11eb-9021-a00ad12c05bf.PNG)
1. Commit을 하고 원격 레포지토리에 Push를 합니다.    
2. GitHub API을 통해 Webhook을 발생하도록 설정합니다.    
3. Jenkins가 Polling을 하면서 Webhook을 인지하면 빌드를 시작합니다.    
4. 빌드 성공여부
    * 빌드 성공: Unit Test를 진행합니다.   
    * 빌드 실패: 통합을 할 수 없으므로 기능을 수정합니다.    
5. 단위 테스트 성공여부
    * 단위 테스트 성공: 통합을 성공합니다.    
    * 단위 테스트 실패: 통합을 할 수 없으므로 기능을 수정합니다.    

## :pushpin: CD 프로세스
![cd_process](https://user-images.githubusercontent.com/54772162/101178161-01e46a00-368c-11eb-844c-9572a2e23f35.PNG)

1. Jenkins 서버에서 Git Parameter를 입력합니다.
2. 입력한 브랜치를 Checkout 합니다.
3. 브랜치를 빌드한 뒤, 레포지토리에 Dockerfile을 읽어서 Docker Image 생성합니다.
4. Docker Hub에 업로드합니다.
5. SSH 명령을 통해 배포 서버에서 Docker Hub에 저장된 Image를 Pull하고 컨테이너를 실행하도록 합니다.

## :pushpin: 프로젝트 디자인 설계

* Wiki를 참고하여 주시기 바랍니다.  
Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/02.-Front-Design

## :pushpin: ERD

* Wiki를 참고하여 주시기 바랍니다.  
Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/03.-ER-Diagram

## :pushpin: nGrinder 성능 테스트
* 자세한 내용은 Wiki를 확인해주시길 바랍니다.    
https://github.com/f-lab-edu/sns-project/wiki/07.-nGrinder-Test-Scenario-&-Analysis
