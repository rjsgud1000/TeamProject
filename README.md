# G-Universe - Game Community Platform

**G-Universe**는 게이머들이 자유롭게 소통하고 정보를 공유할 수 있도록 만든 **웹 기반 게임 커뮤니티 플랫폼**입니다.

사용자는 게시판을 통해 게임 정보를 공유하고 질문하거나 파티원을 모집할 수 있으며,  
회원 시스템을 통해 개인화된 커뮤니티 활동이 가능합니다.

본 프로젝트는 **JSP / Servlet 기반 MVC 패턴**을 활용하여 개발되었으며,  
팀 협업 및 Git 기반 버전 관리를 경험하기 위한 **팀 프로젝트**로 진행되었습니다.

---

## Project Overview

| 항목 | 내용 |
|---|---|
| 프로젝트 이름 | G-Universe |
| 프로젝트 유형 | 웹 기반 게임 커뮤니티 |
| 개발 방식 | 팀 프로젝트 |
| 개발 목적 | JSP/Servlet 기반 웹 애플리케이션 개발 및 Git 협업 경험 |
| 주요 대상 | 게임 커뮤니티 이용자, 게임 정보 탐색 사용자, 파티 모집 사용자 |

### 개발 목표

- JSP / Servlet 기반 웹 서비스 개발 경험
- MVC 패턴 기반 프로젝트 구조 설계
- 데이터베이스 설계 및 CRUD 기능 구현
- 외부 API 연동 및 크롤링 기능 구현
- GitHub 협업 및 브랜치 관리 경험
- 사용자 인증, 신고, 제재 등 커뮤니티 운영 기능 구현

---

## Team

| 역할 | 이름 |
|---|---|
| 팀장 | 배현우 |
| 팀원 | 김건형 |
| 팀원 | 김규 |
| 팀원 | 박상호 |
| 팀원 | 이수훈 |

---

## Team Members & Responsibilities

| 이름 | 담당 기능 |
|---|---|
| 배현우 | 게임 순위 차트, 게임 트렌드 조회, 최신 게임 정보 크롤링, 메인페이지 기능 |
| 김건형 | 로그인, 회원가입, 이메일 인증, 비밀번호 찾기, 마이페이지, 관리자 기능, 로그인 기록 |
| 김규 | 로고 제작, UI 디자인, 발표 자료 디자인 |
| 박상호 | 댓글 작성/수정/삭제, 대댓글, 댓글 추천/비추천, 댓글 신고 |
| 이수훈 | 게시판 작성/조회/수정/삭제, 검색/정렬/페이징, 추천, 인기글, 파티 모집 게시판 |

---

## Tech Stack

### Backend
- Java
- JSP
- Servlet
- MVC Architecture
- DAO / Service Pattern
- JSTL

### Frontend
- HTML5
- CSS3
- JavaScript
- jQuery

### Database
- MySQL

### Server
- Apache Tomcat 9.0

### Development Environment
- Eclipse
- Windows 11
- Git / GitHub

### External API / Crawling
- Naver DataLab API
- Steam Top Seller API
- Google Play Store Crawling
- Naver 뉴스 / 공지사항 / 패치노트 크롤링
- Jsoup Crawling

### Libraries
- json-simple
- jsoup
- commons-fileupload
- commons-io
- jakarta.mail
- jakarta.activation
- mysql-connector
- tomcat-dbcp
- JSTL

---

## Project Architecture

본 프로젝트는 **MVC(Model-View-Controller)** 패턴을 기반으로 설계되었습니다.

```text
Client Request
   ↓
Controller
   ↓
Service
   ↓
DAO
   ↓
Database
   ↓
View(JSP)
```

### 구성 요소

#### Controller
- 클라이언트의 요청 URL을 받아 적절한 기능으로 분기합니다.
- 요청 파라미터를 수집하고 Service 계층에 전달합니다.
- 처리 결과를 request/session 영역에 저장한 뒤 JSP(View)로 전달합니다.

#### Service
- 프로젝트의 핵심 비즈니스 로직을 처리합니다.
- Controller와 DAO 사이에서 기능 흐름을 연결합니다.
- 데이터 검증, 상태 변경, 권한 검사, 예외 처리를 담당합니다.

#### DAO
- 데이터베이스와 직접 연결되는 계층입니다.
- SQL 실행을 통해 CRUD(Create, Read, Update, Delete) 기능을 수행합니다.
- 조회 결과를 VO / DTO 객체에 매핑합니다.

#### VO / DTO
- 계층 간 데이터 전달을 위한 객체입니다.
- 회원, 게시글, 댓글, 신고, 제재, 로그인 기록 등 각 도메인 데이터를 저장하고 전달합니다.

#### View
- JSP를 이용해 사용자 화면을 구성합니다.
- Controller에서 전달받은 데이터를 화면에 출력합니다.
- 사용자의 입력을 받아 다시 Controller로 요청을 보냅니다.

#### Utility
- `DBCPUtil` : DB 커넥션 풀 관리
- `PasswordUtil` : PBKDF2 기반 비밀번호 해시 처리
- `RecaptchaUtil` : 로그인 CAPTCHA 검증
- `NaverMailSend` : 이메일 인증 및 메일 발송 처리
- 기타 API/크롤링 유틸리티 : 외부 데이터 수집 및 파싱 지원

#### Listener / Scheduler
- 애플리케이션 시작 시 필요한 초기 작업을 수행합니다.
- 게임 트렌드 데이터 갱신 스케줄러를 실행하여 메인페이지 정보를 주기적으로 업데이트합니다.

---

## Main Features

### 1. 메인 화면
- 인기 게임 순위 조회
- PC 인기 게임 순위 표시
- 모바일 인기 게임 순위 표시
- Steam 인기 게임 정보 조회
- 게임별 공지사항 / 패치노트 / 뉴스 크롤링
- Naver DataLab 기반 게임 트렌드 조회
- 인기 게시글 표시
- 최신 게시글 표시
- 공지사항 표시
- 수집 데이터 캐싱 및 자동 갱신 처리

---

### 2. 회원 기능

#### 회원가입 / 로그인
- 회원가입 기능
- 로그인 기능
- 아이디 실시간 중복 검사
- 닉네임 실시간 중복 검사
- 이메일 인증 기반 회원가입
- 이메일 인증 기반 비밀번호 찾기
- 로그인 시 CAPTCHA 인증 추가
- 제재 회원 로그인 차단 및 제재 사유 출력
- 탈퇴 회원 / 비활성 회원 로그인 제한

#### 비밀번호 및 인증
- PBKDF2 기반 비밀번호 해시 저장
- 이메일 인증 코드 발송
- 인증 코드 유효시간 관리
- 비밀번호 재설정 처리

#### 로그인 기록
- 로그인 성공 / 실패 / 차단 이력 저장
- 로그인 시간 확인
- 로그인 IP 확인
- 브라우저 / OS 정보(User-Agent) 저장
- 실패 사유 기록 및 표시

---

### 3. 마이페이지
- 자신의 정보 상세 조회
- 자신의 정보 수정 가능
  - 닉네임 변경
  - 비밀번호 변경
  - 이메일 변경
  - 전화번호 변경
  - 주소 변경
- 회원 탈퇴 기능
- 내 로그인 기록 조회
- 내가 작성한 게시글 조회
- 내가 작성한 댓글 조회

---

### 4. 게시판 기능

#### 지원 게시판
- 공지사항 게시판
- 자유게시판
- 질문과답변 게시판
- 파티원 모집 게시판

#### 게시글 기능
- 게시글 작성
- 게시글 수정
- 게시글 삭제
- 게시글 상세 조회
- 카테고리별 게시글 목록 조회
- 제목 / 내용 / 작성자 검색 기능
- 페이징 처리
- 게시글 정렬 기능
  - 최신순
  - 조회수순
  - 추천순
- 게시글 추천 기능
  - 중복 참여 불가능
- 인기 게시글 기능
  - 추천 수
  - 댓글 수
  - 조회수 기반 반영
- 조회수 / 추천수 기반 HOT / NEW UI 적용

#### 질문과답변(Q&A)
- 질문글 등록
- 답변형 댓글 구조 지원
- 채택 댓글 처리
- 채택된 답변 표시 기능

#### 파티원 모집 게시판
- 파티 모집 게시글 작성
- 모집 상태 관리
  - 모집중
  - 모집완료
- 현재 인원 / 최대 인원 관리
- 모집 완료 상태 UI 반영

#### 게시글 신고 및 블라인드
- 게시글 신고 기능
- 동일 사용자의 중복 신고 방지
- 관리자 신고 처리 기능
- 신고 처리 시 게시글 블라인드 가능
- 관리자에 의한 블라인드 해제 가능

---

### 5. 댓글 기능
- 댓글 작성
- 댓글 수정
- 댓글 삭제
- 대댓글 작성
- 부모 댓글 / 자식 댓글 구조 지원
- 댓글 페이징 처리
- 댓글 추천 / 비추천 기능
  - 중복 참여 불가능
- 댓글 신고 기능
- 댓글 삭제 시 대댓글 처리
- 채택 댓글 표시 기능(Q&A 연동)

---

### 6. 관리자 기능

#### 회원 관리
- 회원 목록 조회
- 회원 검색 기능
  - 아이디 검색
  - 이름 검색
  - 닉네임 검색
- 회원 상세 관리 페이지
  - 회원의 전체 정보 조회
  - 누적 경고 확인
  - 제재 상태 확인
  - 최근 제재 이력 확인
- 회원 상태 변경
  - ACTIVE
  - WARNING
  - BANNED
  - WITHDRAWN
- 경고 / 제재 등록 및 관리

#### 신고 관리
- 전체 신고 목록 조회
- 댓글 신고 목록 조회
- 게시글 신고 목록 조회
- 신고 대상 회원 상세 정보 확인
- 신고 처리 기능
- 처리 상태 관리
  - PENDING
  - RESOLVED
  - REJECTED
- 처리 완료 상태 표시
- 게시글 블라인드 / 복구
- 신고된 댓글 상태 처리

#### 운영 관리
- 커뮤니티 질서 유지를 위한 제재 기록 관리
- 신고 이력 및 제재 이력 조회
- 관리자 권한 기반 기능 접근 제어

---

### 7. 디자인
- 프로젝트 로고 제작
- 게임 커뮤니티 컨셉 기반 UI 디자인
- 발표용 PPT 제작
- 게시판 / 관리자 페이지 / 마이페이지 화면 구성

---

## External API & Crawling

### Naver DataLab API
- 게임 관련 검색 트렌드 데이터 조회
- 메인화면 인기 게임 관심도 반영

### Steam API / Store Data
- Steam 인기 게임 정보 조회
- 게임 순위 및 게임명 표시

### Crawling
- 게임별 공지사항 / 패치노트 / 뉴스 수집
- Google Play Store 인기 게임 정보 수집
- 외부 사이트 HTML 파싱 및 데이터 가공

---

## Security

프로젝트에 적용된 보안 요소는 다음과 같습니다.

- PBKDF2 기반 비밀번호 해시 처리
- 이메일 인증 기능
- 로그인 CAPTCHA 인증
- DBCP 기반 DB Connection Pool 사용
- 중복 추천 / 중복 신고 방지
- 제재 회원 로그인 차단
- 로그인 실패 이력 저장
- 관리자 기능 접근 제어

---

## Project Flow Summary

### 회원 관련 흐름
1. 사용자는 회원가입 시 아이디/닉네임 중복 여부를 확인합니다.
2. 이메일 인증 완료 후 회원가입이 가능합니다.
3. 로그인 시 CAPTCHA 검증과 계정 상태 검사를 수행합니다.
4. 로그인 성공/실패 결과는 로그인 기록 테이블에 저장됩니다.

### 게시판 관련 흐름
1. 사용자가 게시글을 작성하면 카테고리에 따라 게시판에 저장됩니다.
2. 게시글은 검색, 정렬, 페이징 기능을 통해 조회할 수 있습니다.
3. 추천 수, 댓글 수, 조회수에 따라 인기글이 노출됩니다.
4. 신고된 게시글은 관리자가 검토 후 블라인드 처리할 수 있습니다.

### 관리자 관련 흐름
1. 관리자는 회원 목록, 신고 목록, 제재 이력을 조회할 수 있습니다.
2. 신고 처리 후 상태를 변경하고, 필요한 경우 게시글/댓글을 제재할 수 있습니다.
3. 회원 상태를 경고, 정지, 탈퇴 등으로 관리할 수 있습니다.

---

## Database Schema

프로젝트에는 회원, 게시글, 댓글, 추천, 신고, 제재, 로그인 기록 기능을 위한 테이블이 설계되어 있습니다.

### MEMBER
```sql
CREATE TABLE `MEMBER` (
  `member_id` varchar(50) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `nickname` varchar(50) NOT NULL,
  `zipcode` varchar(10) DEFAULT NULL,
  `addr1` varchar(255) DEFAULT NULL,
  `addr2` varchar(255) DEFAULT NULL,
  `addr3` varchar(255) DEFAULT NULL,
  `addr4` varchar(255) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` varchar(10) NOT NULL DEFAULT 'USER',
  `status` varchar(10) NOT NULL DEFAULT 'ACTIVE',
  `updated_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `nickname` (`nickname`)
);
```

### BOARD_POST
```sql
CREATE TABLE `BOARD_POST` (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `member_id` varchar(20) NOT NULL,
  `nickname` varchar(20) NOT NULL,
  `category` int NOT NULL,
  `title` varchar(200) DEFAULT NULL,
  `content` text,
  `viewcount` int DEFAULT '0',
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL,
  `is_deleted` int DEFAULT '0',
  `accepted_comment_id` int DEFAULT NULL,
  `is_blinded` tinyint(1) NOT NULL DEFAULT '0',
  `recruit_status` int DEFAULT NULL,
  `current_members` int DEFAULT NULL,
  `max_members` int DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  KEY `FK` (`member_id`),
  KEY `FK2` (`nickname`),
  CONSTRAINT `FK` FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`),
  CONSTRAINT `FK2` FOREIGN KEY (`nickname`) REFERENCES `MEMBER` (`nickname`)
);
```

### COMMENT
```sql
CREATE TABLE `COMMENT` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `member_id` varchar(20) DEFAULT NULL,
  `content` text NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  `parent_comment_id` int DEFAULT NULL,
  `is_accepted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`comment_id`),
  KEY `post_id` (`post_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `COMMENT_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `BOARD_POST` (`post_id`),
  CONSTRAINT `COMMENT_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`)
);
```

### POST_LIKE
```sql
CREATE TABLE `POST_LIKE` (
  `post_id` int NOT NULL,
  `member_id` varchar(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`, `member_id`),
  KEY `idx_like_member` (`member_id`),
  CONSTRAINT `fk_like_member` FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_like_post` FOREIGN KEY (`post_id`) REFERENCES `BOARD_POST` (`post_id`) ON DELETE CASCADE
);
```

### COMMENT_LIKE
```sql
CREATE TABLE `COMMENT_LIKE` (
  `comment_id` int NOT NULL,
  `member_id` varchar(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`, `member_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `COMMENT_LIKE_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `COMMENT` (`comment_id`),
  CONSTRAINT `COMMENT_LIKE_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`)
);
```

### COMMENT_DISLIKE
```sql
CREATE TABLE `COMMENT_DISLIKE` (
  `comment_id` int NOT NULL,
  `member_id` varchar(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`, `member_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `COMMENT_DISLIKE_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `COMMENT` (`comment_id`),
  CONSTRAINT `COMMENT_DISLIKE_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`)
);
```

### POST_REPORT
```sql
CREATE TABLE `POST_REPORT` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `member_id` varchar(50) NOT NULL,
  `reason` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL DEFAULT 'PENDING',
  PRIMARY KEY (`report_id`)
);
```

### COMMENT_REPORT
```sql
CREATE TABLE `COMMENT_REPORT` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `comment_id` int NOT NULL,
  `member_id` varchar(20) NOT NULL,
  `reason` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL DEFAULT 'PENDING',
  PRIMARY KEY (`report_id`),
  KEY `comment_id` (`comment_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `COMMENT_REPORT_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `COMMENT` (`comment_id`),
  CONSTRAINT `COMMENT_REPORT_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`)
);
```

### SANCTION
```sql
CREATE TABLE `SANCTION` (
  `action_id` int NOT NULL AUTO_INCREMENT,
  `target_member_id` varchar(50) NOT NULL,
  `admin_member_id` varchar(50) NOT NULL,
  `TYPE` varchar(10) DEFAULT 'WARN',
  `REASON` varchar(255) DEFAULT NULL,
  `start_at` datetime NOT NULL,
  `end_at` datetime NOT NULL,
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `member_status` varchar(20) DEFAULT 'BANNED',
  PRIMARY KEY (`action_id`),
  KEY `target_member_id` (`target_member_id`),
  KEY `admin_member_id` (`admin_member_id`),
  CONSTRAINT `SANCTION_ibfk_1` FOREIGN KEY (`target_member_id`) REFERENCES `MEMBER` (`member_id`),
  CONSTRAINT `SANCTION_ibfk_2` FOREIGN KEY (`admin_member_id`) REFERENCES `MEMBER` (`member_id`)
);
```

### LOGIN_HISTORY
```sql
CREATE TABLE `LOGIN_HISTORY` (
  `login_history_id` bigint NOT NULL AUTO_INCREMENT,
  `input_member_id` varchar(50) DEFAULT NULL,
  `member_id` varchar(50) DEFAULT NULL,
  `login_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `login_ip` varchar(45) DEFAULT NULL,
  `user_agent` text,
  `login_result` varchar(20) NOT NULL,
  `fail_reason` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`login_history_id`)
);
```

---

## DB 설계 특징

- 회원 정보를 저장하는 `MEMBER` 테이블을 중심으로 전체 기능이 연결됩니다.
- 게시글은 `BOARD_POST`, 댓글은 `COMMENT` 테이블에서 관리됩니다.
- 게시글 추천은 `POST_LIKE`, 댓글 추천/비추천은 `COMMENT_LIKE`, `COMMENT_DISLIKE` 테이블로 분리하여 중복 참여를 방지했습니다.
- 게시글 신고와 댓글 신고를 각각 `POST_REPORT`, `COMMENT_REPORT` 테이블로 분리하여 관리했습니다.
- 신고 처리 상태를 `PENDING`, `RESOLVED`, `REJECTED` 등으로 관리할 수 있도록 설계했습니다.
- 관리자 제재 내역은 `SANCTION` 테이블을 통해 기록됩니다.
- 로그인 성공/실패/차단 이력은 `LOGIN_HISTORY` 테이블에 저장하여 보안성과 추적성을 높였습니다.
- `BOARD_POST.accepted_comment_id`를 통해 Q&A 게시판의 채택 답변 구조를 지원합니다.
- `BOARD_POST.is_blinded`를 통해 신고 처리 후 게시글 노출을 제어할 수 있습니다.
- 파티 모집 게시판은 모집 상태, 현재 인원, 최대 인원을 컬럼으로 관리할 수 있도록 설계했습니다.

---

## Expected Effects

- 게임 정보를 한 곳에서 확인하고 소통할 수 있는 커뮤니티 환경 제공
- 회원 관리, 신고 처리, 제재 기능을 통한 안정적인 커뮤니티 운영
- MVC 패턴 기반 설계 경험과 팀 협업 역량 강화
- 외부 API 연동 및 크롤링 기술 활용 경험 확보
- 보안 기능과 사용자 인증 절차를 적용한 웹 서비스 구현 경험 축적

---

## Improvement Points

- 소셜 로그인 기능 추가
- 실시간 알림 기능 추가
- 이미지 업로드 기능 고도화
- 게시판 카테고리 확장
- 관리자 통계 대시보드 추가
- 반응형 웹 UI 개선
- API 키 및 민감정보 외부 설정 파일 분리
- 배포 환경 보안 강화

---

## Conclusion

**G-Universe**는 단순한 게시판 프로젝트를 넘어,  
회원 인증, 게시판 운영, 신고 및 제재, 외부 데이터 수집, 로그인 기록 관리 등  
실제 커뮤니티 서비스에 필요한 핵심 기능을 종합적으로 구현한 프로젝트입니다.

본 프로젝트를 통해 팀원들은 **JSP / Servlet 기반 MVC 구조**,  
**DB 설계**, **보안 처리**, **Git 협업**, **외부 API 및 크롤링 연동** 등  
웹 개발 전반에 대한 실무형 경험을 쌓을 수 있었습니다.
