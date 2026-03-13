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

### 개발 목표

- JSP / Servlet 기반 웹 서비스 개발 경험
- MVC 패턴 기반 프로젝트 구조 설계
- 데이터베이스 설계 및 CRUD 기능 구현
- GitHub 협업 및 브랜치 관리 경험

---

## 👥 Team

| 역할 | 이름 |
|---|---|
| 팀장 | 배현우 |
| 팀원 | 김건형 |
| 팀원 | 김규 |
| 팀원 | 박상호 |
| 팀원 | 이수훈 |

---

## Tech Stack

### Backend
- Java
- JSP
- Servlet
- MVC Architecture
- DAO / Service Pattern

### Frontend
- HTML5
- CSS3
- JavaScript
- jQuery

### Database
- MySQL

### Server
- Apache Tomcat

### External API / Crawling
- Naver Web API
- Jsoup Crawling

### Libraries
- json-simple
- jsoup
- commons-fileupload
- jakarta.mail
- mysql-connector
- tomcat-dbcp

---

## Project Architecture

본 프로젝트는 **MVC(Model-View-Controller)** 패턴을 기반으로 설계되었습니다.

Controller  
   ↓  
Service  
   ↓  
DAO  
   ↓  
Database  
### 구성 요소

#### Controller
- 클라이언트의 요청을 받아 적절한 기능으로 분기합니다.
- Service 계층을 호출하여 필요한 비즈니스 로직을 수행합니다.
- 처리 결과를 JSP(View)에 전달합니다.

#### Service
- 프로젝트의 핵심 비즈니스 로직을 처리합니다.
- Controller와 DAO 사이에서 기능 흐름을 연결합니다.
- 데이터 검증 및 기능 단위 처리를 담당합니다.

#### DAO
- 데이터베이스와 직접 연결되는 계층입니다.
- SQL 실행을 통해 CRUD(Create, Read, Update, Delete) 기능을 수행합니다.
- 조회 결과를 VO / DTO 객체에 매핑합니다.

#### VO / DTO
- 계층 간 데이터 전달을 위한 객체입니다.
- 회원, 게시글, 댓글, 신고 정보 등을 저장하고 전달합니다.

#### View
- JSP를 이용해 사용자 화면을 구성합니다.
- Controller에서 전달받은 데이터를 화면에 출력합니다.
- 사용자의 입력을 받아 다시 Controller로 요청을 보냅니다.

---

##  주요 기능

###  메인 화면
- 인기 게임 순위 조회
- 게임별 공지사항 / 패치노트 / 뉴스 크롤링
- 인기 게시글 표시
- 최신 게시글 표시
- 공지사항 표시

###  회원 기능

#### 회원가입 / 로그인
- 회원가입 기능
- 로그인 기능
- 아이디 / 닉네임 실시간 중복 검사
- 이메일 인증 기반 회원가입
- 이메일 인증 기반 비밀번호 찾기
- 로그인 시 CAPTCHA 인증 추가
- 제재 회원 로그인 시도 시 로그인 차단 및 제재 사유 출력

###  마이페이지
- 자신의 정보 상세 조회
- 자신의 정보 수정 가능
  - 닉네임 변경
  - 비밀번호 변경
  - 이메일 변경
  - 전화번호 변경
  - 주소 변경
- 회원 탈퇴 기능

### 관리자 기능

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
- 회원 상태 변경
  - ACTIVE
  - WARNING
  - BANNED
  - WITHDRAWN

#### 신고 관리
- 전체 신고 목록 조회
- 신고 대상 회원 클릭 시 회원 상세 관리 페이지로 이동
- 신고 처리 기능
- 처리 완료 상태 표시

###  게시판 기능

#### 지원 게시판
- 공지사항 게시판
- 자유게시판
- 질문과답변 게시판
- 파티원 모집 게시판

#### 게시글 기능
- 게시글 작성
- 게시글 수정
- 게시글 삭제
- 게시글 조회
- 제목 또는 내용 검색 기능
- 게시글 정렬 기능
  - 최신순
  - 조회수순
  - 추천순
- 게시글 추천 기능
  - 중복 참여 불가능
- 인기 게시글 기능
  - 추천 수
  - 댓글 수
  - 조회수 기반
- 조회수 / 추천수 기반 HOT / NEW UI 적용

###  댓글 기능
- 댓글 작성
- 댓글 수정
- 댓글 삭제
- 댓글 추천 / 비추천 기능
  - 중복 참여 불가능
- 댓글 신고 기능
- 댓글 삭제 시 대댓글도 함께 삭제

###  디자인
- 로고 제작
- UI 디자인
- 발표용 PPT 제작

---

##  External API

### Naver API
- 인기 게임 정보 조회

### Crawling
- 게임별 공지사항 / 패치노트 / 뉴스 수집

---

##  Security

프로젝트에 적용된 보안 요소는 다음과 같습니다.

- PBKDF2 기반 비밀번호 해시 처리
- 이메일 인증 기능
- 로그인 CAPTCHA 인증
- DBCP 기반 DB Connection Pool 사용

---

## 팀 멤버 & 담당 구현 기능

| 이름  | 담당 기능                      |
| --- | -------------------------- |
| 배현우 | 메인페이지 게임순위차트, 게임별 최신정보 크롤링, 커뮤니티 최신글 표시 |
| 김건형 | 로그인, 회원가입, 마이페이지, 관리자 기능   |
| 김규  | 로고 제작, 디자인               |
| 박상호 | 댓글 관련 기능                   |
| 이수훈 | 게시판 관련 기능                  |

---

## 🗄 Database Schema

프로젝트에는 회원, 게시글, 댓글, 추천, 신고, 제재 기능을 위한 테이블이 설계되어 있습니다.

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

### COMMENT_REPORT
```sql
CREATE TABLE `COMMENT_REPORT` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `comment_id` int NOT NULL,
  `member_id` varchar(20) NOT NULL,
  `reason` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
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
  `TYPE` varchar(4) DEFAULT 'WARN',
  `REASON` varchar(50) DEFAULT NULL,
  `start_at` datetime NOT NULL,
  `end_at` datetime NOT NULL,
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `member_status` varchar(7) DEFAULT 'BANNED',
  PRIMARY KEY (`action_id`),
  KEY `target_member_id` (`target_member_id`),
  KEY `admin_member_id` (`admin_member_id`),
  CONSTRAINT `SANCTION_ibfk_1` FOREIGN KEY (`target_member_id`) REFERENCES `MEMBER` (`member_id`),
  CONSTRAINT `SANCTION_ibfk_2` FOREIGN KEY (`admin_member_id`) REFERENCES `MEMBER` (`member_id`)
);
```

### DB 설계 특징
- 회원 정보를 저장하는 `MEMBER` 테이블을 중심으로 전체 기능이 연결됩니다.
- 게시글은 `BOARD_POST`, 댓글은 `COMMENT` 테이블에서 관리됩니다.
- 게시글 추천은 `POST_LIKE`, 댓글 추천/비추천은 `COMMENT_LIKE`, `COMMENT_DISLIKE` 테이블로 분리하여 중복 참여를 방지했습니다.
- 댓글 신고 기능은 `COMMENT_REPORT` 테이블에서 관리됩니다.
- 관리자 제재 내역은 `SANCTION` 테이블을 통해 기록됩니다.
