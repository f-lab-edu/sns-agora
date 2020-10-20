# Social Network Service Project

* Instagram, Facebook 과 같이 평소 사용하던 SNS를 직접 제작하여보기
* 실행만 되는 서비스가 아닌 대규모 트래픽을 처리할 수 있는 성능적으로 우수한 서비스 제작하기
* 프로젝트 진행에 따라 ReadMe 업데이트 예정  


## 사용 기술 및 개발환경

Spring Boot, Maven, MyBatis, Redis, MySQL, Java, IntelliJ, Jenkins, Naver Cloud Platform


## 주요 기능

* wiki에서 Use case를 참고 바랍니다.    
https://github.com/f-lab-edu/sns-project/wiki/01.-Use-Case


## AGORA 구성도

![agora_system_structure2](https://user-images.githubusercontent.com/54772162/84670161-b5cc2e80-af60-11ea-9fc6-90f23a61f676.PNG)


## 코딩 컨벤션

* 코딩 컨벤션은 예쁜 코드를 위해서도 필요하지만, 협업시, 장기 프로젝트를 운영할 때 등 유용합니다.

* 해당 프로젝트에서는 **Google CheckStyle**을 적용하였습니다. (최대한 코딩 컨벤션을 신경쓰려하였으나, 수정 중에 일부 적용이 누락된 부분이 있을 수 있습니다.)


## 브랜치 관리 전략

* AGORA는 Git-Flow 를 이용하여 브랜치를 관리하였습니다.

<img src="https://user-images.githubusercontent.com/54772162/84594283-2a816900-ae8c-11ea-9e88-0c1c7e4709a4.png" alt="git_flow" style="zoom:50%;" />

* master : 배포시 사용할 브랜치. 초기 시행착오에 의하여 몇몇 기능이 merge 되어 있으나, 
           원래 사용 용도는 완벽히 배포가 가능한 상태에만 merge가 되어야만 합니다.
* develop : 다음 버전을 개발하는 브랜치, 완전히 배포가 가능하다고 생각되면 master 브랜치에 merge 합니다.
* feature : 기능을 개발하는 브랜치
* release : 배포를 준비할 때 사용할 브랜치
* hotfix : 배포 후에 발생한 버그를 수정 하는 브랜치

#### 참고 사이트

* 우린 Git-flow를 사용하고 있어요, 우아한 형제들 기술 블로그, Oct 30, 2017, 나동호  
  https://woowabros.github.io/experience/2017/10/30/baemin-mobile-git-branch-strategy.html


## 프로젝트 중점사항

자세한 내용을 확인하시려면 Wiki에서 확인하실 수 있습니다.
현재는 작업 중으로 일부 내용이 누락되어 있을 수 있습니다.

### 대규모 트래픽을 고려한 설계 및 성능 튜닝 작업

Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/04.-Large-Traffic-Handling-Task

### 리펙토링을 통한 코드 개선 작업

Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/05.-Refactoring-Task

## 프로젝트 디자인 설계

* Wiki를 참고하여 주시기 바랍니다.  
Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/02.-Front-Design

## ERD

* Wiki를 참고하여 주시기 바랍니다.  
Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/03.-ER-Diagram
## API

### API에 대한 자세한 내용을 확인하시려면 Wiki를 참고해주세요.    
https://github.com/f-lab-edu/sns-project/wiki

## CD 적용
![cd](https://user-images.githubusercontent.com/54772162/91732703-8cc44d00-ebe3-11ea-9d04-009fce30bc72.png)
* CD란 무엇인가?(참고 사이트)    
https://www.redhat.com/ko/topics/devops/what-is-ci-cd    

## ngrinder 성능 테스트
* 자세한 내용은 Wiki를 확인해주시길 바랍니다.    
https://github.com/f-lab-edu/sns-project/wiki/07.-nGrinder-Test-Scenario-&-Analysis
