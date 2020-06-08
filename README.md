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
7. 댓글 작성, 수정, 삭제(예정)
8. 피드 신고(예정)
9. 댓글 신고(예정)

### Administrator(예정)

1. 회원 목록 조회
2. 회원 정지
3. 댓글 삭제

### 그 외 기능

1. 친구 요청 및 댓글 알림 기능

2. 푸시 메시지 발송 기능

## 성능 개선 작업

* cursor based pagination 을 이용한 피드 조회 기능 개선
* 세션과 쿠키를 이용한 로그인 기능 구현
* AspectJ 를 이용한 로그인 기능 분리
* 클라우드 스토리지 서비스를 이용한 파일 업로드 기능 구현
* WAS Session 이 아닌 Redis Session 사용(세션 클러스터링)
* In memory DB 로 Redis 사용
* 개별 insert 작업을 bulk insert 하여 DB 접속 비용 감소
* Firbase API 를 활용한 푸시메시지 구현
* 좋아요 캐싱을 통한 성능 개선
* Quartz API를 사용한 Batch Insert 구현
* DataBase Replication을 통한 read/write 성능 개선
