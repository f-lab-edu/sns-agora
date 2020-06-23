# Social Network Service Project

* Instagram, Facebook 과 같이 평소 사용하던 SNS를 직접 제작하여보기
* 실행만 되는 서비스가 아닌 대규모 트래픽을 처리할 수 있는 성능적으로 우수한 서비스 제작하기
* 프로젝트 진행에 따라 ReadMe 업데이트 예정  


## 사용 기술 및 개발환경

Spring Boot, Maven, MyBatis, Redis, MySQL, Java, IntelliJ  


## 주요 기능

### User

1. 회원 가입, 탈퇴 요청
2. 로그인 
3. 피드 작성, 조회, 수정, 삭제
4. 친구 추가, 삭제, 차단
5. 피드 목록 조회
6. 피드 좋아요 
7. 댓글 작성, 수정, 삭제
8. 피드 신고(예정)
9. 댓글 신고(예정)

### Administrator(예정)

1. 회원 목록 조회
2. 회원 정지
3. 댓글 삭제

### 그 외 기능

1. 친구 요청 및 댓글 알림 기능

2. 푸시 메시지 발송 기능


## AGORA 구성도

![agora_system_structure2](https://user-images.githubusercontent.com/54772162/84670161-b5cc2e80-af60-11ea-9fc6-90f23a61f676.PNG)


## 프로젝트 관리 전략

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

Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/Large-Traffic-Handling-Task

### 리펙토링을 통한 코드 개선 작업

Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/Refactoring-Task

## 프로젝트 디자인 설계

* Wiki를 참고하여 주시기 바랍니다.  
Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/Front-Design


## ERD

* Wiki를 참고하여 주시기 바랍니다.  
Wiki 주소 : https://github.com/f-lab-edu/sns-project/wiki/ER-Diagram

## API

### API에 대한 자세한 내용을 확인하시려면 Wiki를 참고해주세요.    
https://github.com/f-lab-edu/sns-project/wiki
