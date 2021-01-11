# :clapper: AGORA

## :pushpin: 개요

&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: Instagram, Facebook 과 같이 평소 사용하던 SNS를 직접 제작하여보기    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: 실행만 되는 서비스가 아닌 대규모 트래픽을 처리할 수 있는 성능적으로 우수한 서비스 제작하기    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: 프로젝트 진행에 따라 ReadMe 업데이트 예정입니다.    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: Jenkins 주소 : http://27.96.135.12:8080/    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: 보다 자세한 내용을 알고 싶으시다면 Wiki를 참고해주시길 바랍니다.     
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;❔ Wiki : https://github.com/f-lab-edu/sns-agora/wiki    


## :pushpin: 사용 기술 및 개발환경

[![framework](https://img.shields.io/badge/spring%20boot-2.2.2-yellowgreen)](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.2-Release-Notes) [![build tool](https://img.shields.io/badge/maven-2.5.3-orange)](https://maven.apache.org/) [![mybatis](https://img.shields.io/badge/MyBatis-3.5.4-blue)](https://mybatis.org/mybatis-3/ko/index.html) [![lettuce](https://img.shields.io/badge/lettuce-5.2.1-brightgreen)](https://lettuce.io/) [![mysql](https://img.shields.io/badge/MySQL-8.0-blue)](https://dev.mysql.com/doc/refman/8.0/en/) ![java](https://img.shields.io/badge/open--jdk-8-brightgreen) ![intellij](https://img.shields.io/badge/IntelliJ-3.0-orange) [![checkstyle](https://img.shields.io/badge/codestyle-Google%20CheckStyle-yellow)](https://checkstyle.sourceforge.io/google_style.html) [![Build Status](https://img.shields.io/badge/build-passing-green)](http://27.96.135.12:8080/job/agora-ci/job/issue%252F87/) [![image](https://img.shields.io/badge/docker-latest-lightgrey)](https://hub.docker.com/r/tax1116/agora) ![OS](https://img.shields.io/badge/ubuntu-16.04-red)   

## :pushpin: 프로젝트 중점사항

&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: 확장성 있는 시스템을 구현하고자 노력했습니다.    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: 쿼리 튜닝을 통해 Full-Table Scan이 발생하는 상황을 줄이고자 노력했습니다.    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: Batch 작업을 통해 잦은 Connection과 RTT를 줄이고자 노력했습니다.    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: 캐싱을 적용하여 메모리 기반 작업의 비율을 높혀 성능을 개선하고자 노력했습니다.    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: nGrinder를 활용하여 성능테스트를 진행하고 성능 개선에 참고했습니다.    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: pinpoint(APM)를 활용하여 성능 모니터링을 하고, 성능 개선에 참고했습니다.    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: Junit 프레임워크를 활용하여 코드 신뢰성을 높이고자 단위테스트를 작성했습니다.    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: CI/CD를 적용하고 자동화된 빌드와 배포를 통해 개발의 생산성을 높히기 위해 노력했습니다.    

## :pushpin: 주요 기능

&nbsp;&nbsp;&nbsp;&nbsp; 1. 피드 기능    
&nbsp;&nbsp;&nbsp;&nbsp; 2. 파일 기능    
&nbsp;&nbsp;&nbsp;&nbsp; 3. 인증 및 인가 기능    
&nbsp;&nbsp;&nbsp;&nbsp; 4. 친구 기능    
&nbsp;&nbsp;&nbsp;&nbsp; 5. 좋아요 기능    
&nbsp;&nbsp;&nbsp;&nbsp; 6. 댓글 기능    
&nbsp;&nbsp;&nbsp;&nbsp; 7. 피드 추천 기능(현재는 등록된 최신 피드들을 추천합니다. 추후 추천 알고리즘을 공부해보고 확장할 예정입니다.)    
&nbsp;&nbsp;&nbsp;&nbsp; 8. 푸시 메시지 기능    

## :pushpin: AGORA 구성도

<p align="center">
  <img src="https://user-images.githubusercontent.com/54772162/101143570-6b4b8500-365a-11eb-8b8e-64c5c756aaef.PNG?raw=true" alt="Sublime's custom image"/>
</p>

## :pushpin: 브랜치 관리 전략

&nbsp;&nbsp;&nbsp;&nbsp; :heavy_check_mark: AGORA는 Git-Flow 를 이용하여 브랜치를 관리하였습니다.

<p align="center">
  <img src="https://user-images.githubusercontent.com/54772162/101170794-45d27180-3682-11eb-8c42-6f4bf8ec73c9.PNG?raw=true" alt="Sublime's custom image"/>
</p>

&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: master : 배포시 사용할 브랜치. 초기 시행착오에 의하여 몇몇 기능이 merge 되어 있으나, 원래 사용 용도는 완벽히 배포가 가능한 상태에만 merge가 되어야만 합니다.        
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: develop : 다음 버전을 개발하는 브랜치, 완전히 배포가 가능하다고 생각되면 master 브랜치에 merge 합니다.    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: feature : 기능을 개발하는 브랜치    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: release : 배포를 준비할 때 사용할 브랜치    
&nbsp;&nbsp;&nbsp;&nbsp;:heavy_check_mark: hotfix : 배포 후에 발생한 버그를 수정 하는 브랜치    

#### 참고 사이트

* 우린 Git-flow를 사용하고 있어요, 우아한 형제들 기술 블로그, Oct 30, 2017, 나동호  
:bookmark_tabs: https://woowabros.github.io/experience/2017/10/30/baemin-mobile-git-branch-strategy.html
