# TeamProject_ - MVC2 로그인(MySQL/JNDI) 구성

이 문서는 `TeamProject_` 프로젝트에서 **MVC2 패턴(Controller → Service → DAO → DB)**으로 **로그인 기능만** MySQL(DataSource/JNDI)과 연동해 동작시키는 최소 구성을 설명합니다.

## 1) 준비사항

- Tomcat에 프로젝트가 정상 배포되어 있어야 합니다.
- MySQL에 `member` 테이블이 있어야 합니다.
- `src/main/webapp/META-INF/context.xml`의 JNDI 리소스(`jdbc/jspbeginner`) 설정이 본인 DB 정보로 맞아야 합니다.

## 2) DB 테이블(예시)

프로젝트 DAO는 아래 컬럼을 사용합니다.

- `member.id` (VARCHAR)
- `member.pass` (VARCHAR)
- `member.name` (VARCHAR)

예시 SQL:

```sql
CREATE TABLE member (
  id   VARCHAR(50) PRIMARY KEY,
  pass VARCHAR(255) NOT NULL,
  name VARCHAR(100) NOT NULL
);

INSERT INTO member(id, pass, name)
VALUES ('test', '1234', '테스트');
```

> 참고: 현재 구현은 학습용으로 **평문 비밀번호** 비교입니다. 실서비스는 BCrypt 등으로 해시 처리하세요.

## 3) URL 흐름

- 로그인 화면: `/member/login.me`
- 로그인 처리: `/member/loginPro.me` (POST)
- 로그아웃: `/member/logout.me`

로그인 성공 시: `main.jsp`로 리다이렉트

## 4) 생성/추가된 Java 코드 (MVC2)

- `Controller/MemberController.java`
- `Service/MemberService.java`
- `Dao/MemberDAO.java`
- `Vo/MemberVO.java`

부가 스모크 테스트(개발용):

- `Controller/DbPingController.java`  
  → `/dev/dbPing` 호출 시 DataSource 연결 여부를 텍스트로 출력합니다.

## 5) 빠른 점검

1. Tomcat 시작
2. 브라우저에서 접속
   - `http://localhost:포트/컨텍스트/dev/dbPing`
   - `http://localhost:포트/컨텍스트/member/login.me`

`dbPing`이 FAIL이면, 보통 아래를 확인하면 됩니다.

- `context.xml`의 URL/계정/비번
- MySQL 드라이버 jar 존재 여부 (`WEB-INF/lib/mysql-connector-java-8.0.25.jar`)
- `<resource-ref>`의 이름이 `jdbc/jspbeginner`로 일치하는지 (`WEB-INF/web.xml`)
- Tomcat 재시작 여부
