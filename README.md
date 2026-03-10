#  G-Universe – Game Community Platform

**G-Universe**는 게이머들이 자유롭게 소통하고 정보를 공유할 수 있도록 만든 **웹 기반 게임 커뮤니티 플랫폼**입니다.  
사용자는 게시판을 통해 게임 정보를 공유하고 질문하거나 파티원을 모집할 수 있으며,  
회원 시스템을 통해 개인화된 커뮤니티 활동이 가능합니다.

본 프로젝트는 **JSP / Servlet 기반 MVC 패턴**을 활용하여 개발되었으며  
팀 협업 및 Git 기반 버전 관리를 경험하기 위한 **팀 프로젝트**로 진행되었습니다.

---

#  Project Overview

- 프로젝트 이름 : **G-Universe**
- 프로젝트 유형 : 웹 기반 게임 커뮤니티
- 개발 방식 : 팀 프로젝트
- 개발 목적
  - JSP/Servlet 기반 **웹 애플리케이션 개발 경험**
  - **MVC 패턴 구조 설계**
  - **DB 설계 및 CRUD 기능 구현**
  - **GitHub 협업 경험**

---

#  Team

| 역할 | 이름 |
|-----|-----|
| 팀장 | 배현우 |
| 팀원 | 김건형 |
| 팀원 | 김규 |
| 팀원 | 박상호 |
| 팀원 | 이수훈 |

---

#  Tech Stack

## Backend
- Java
- JSP
- Servlet
- MVC Architecture
- DAO / Service Pattern

## Frontend
- HTML5
- CSS3
- JavaScript

## Database
- MySQL

## Server
- Apache Tomcat

## External API
- Steam Web API (인기 게임 정보)

## Libraries
- json-simple
- jsoup
- commons-fileupload

---

# 📂 Project Architecture

본 프로젝트는 **MVC(Model-View-Controller) 패턴**을 기반으로 설계되었습니다.

---

# 주요 기능

## 회원 기능
- 회원가입
- 로그인
- 마이페이지
- 회원 정보 수정

## 게시판 기능
- 공지사항 게시판
- 자유게시판
- Q&A 게시판
- 파티원 모집 게시판

## 커뮤니티 기능
- 게시글 작성 / 수정 / 삭제
- 댓글 기능
- 게시글 조회

## 게임 정보 기능
- **Steam API**를 활용한 인기 게임 정보 조회

---

# 팀원 & 담당 기능

| 이름 | 담당 기능 |
|-----|-----|
| 배현우 | 인기게임순위 기능, 게임 패치노트 기능 |
| 김건형 | 로그인, 회원가입, 마이페이지, 관리자 기능 |
| 김규 | 로고 제작, UI 디자인 |
| 박상호 | 댓글관련 기능 |
| 이수훈 | 게시판 관련 기능 |

---

# Database Example

```sql
CREATE TABLE member (
  member_id VARCHAR(50) PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  role VARCHAR(10) DEFAULT 'USER',
  status VARCHAR(10) DEFAULT 'ACTIVE',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
