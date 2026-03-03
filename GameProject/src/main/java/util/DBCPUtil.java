package util;

/*
 ================================================================================
   파일명 : DBCPUtil.java

   사용 환경 :
     - JSP / Servlet 기반 웹 프로젝트
     - WAS : Apache Tomcat
     - DB  : MySQL
     - 커넥션 풀 : Tomcat 내부 DBCP2

   이 파일의 목적 :
     1. Tomcat이 미리 생성해 둔 커넥션 풀(DataSource)을
        JNDI(Java Naming and Directory Interface)를 통해 가져온다.
     2. DAO에서 DB 연결(Connection)을 필요로 할 때
                   커넥션 풀에서 하나를 빌려준다.

   핵심 개념 요약 :

     우리는 DB 연결을 직접 만들지 않는다.
     우리는 Tomcat이 만들어 둔 DB 연결을 "빌려서 사용"한다.

 ================================================================================
*/


// DB 연결 객체 (SQL 실행을 위해 필요)
import java.sql.Connection;
import java.sql.SQLException;

// JNDI 관련 클래스 (서버 자원을 이름으로 찾기 위해 사용)
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

// DB 연결을 관리하는 표준 인터페이스
import javax.sql.DataSource;


public class DBCPUtil {

    /*
     =============================================================================
       1. DataSource 변수 선언

       DataSource란?

         → 여러 개의 DB 연결(Connection)을 내부에 보관하고 있는
           "커넥션 풀 관리자" 객체

	       여기서 중요한 점 :
	
	         우리가 new DataSource()로 생성하지 않는다.
	         Tomcat이 서버 시작 시 이미 생성해 둔다.
	
	       왜 static으로 선언할까?
	
	         - 이 객체는 애플리케이션 전체에서 하나만 존재해야 한다.
	         - 모든 DAO가 동일한 커넥션 풀을 공유해야 한다.
	         - 여러 개 만들면 DB 과부하 발생 가능
	
	       따라서 static으로 선언하여
	       프로그램 전체에서 하나만 사용하도록 한다.
     =============================================================================
    */
    private static DataSource dataSource;



    /*
     =============================================================================
       2. static 초기화 블럭

	       실행 시점 :
	
	         - 이 클래스가 JVM 메모리에 처음 로딩될 때
	         - 단 한 번만 실행된다.
	
	       즉,
	         DBCPUtil.getConnection()이 처음 호출되는 순간 이 블럭이 먼저 실행된다.
	
	       역할 :
	
	         Tomcat 내부 JNDI 저장소에서
	                      우리가 설정해 둔 DataSource를 찾아와
	        dataSource 변수에 저장한다.

     =============================================================================
    */
    static {

        try {

            /*
             ---------------------------------------------------------------------
              1단계 : InitialContext 생성

              InitialContext란?

                → JNDI 시스템에 접근하기 위한 시작 객체
                → 서버 내부 자원을 검색하기 위한 "입구"

		              쉽게 말하면 :
		                서버 내부 자원을 찾기 위한 검색 시작 버튼
             ---------------------------------------------------------------------
            */
            Context initContext = new InitialContext();



            /*
             ---------------------------------------------------------------------
              2단계 : java:comp/env 영역 접근

              Tomcat은 모든 자원(Resource)을
              "java:comp/env"라는 내부 영역에 저장한다.

		              구조 예시 (서버 내부 메모리 구조) :
		
		                  java:comp/env
		                      └── jdbc/jspbeginner
		                           └── DataSource 객체
		                                ├── Connection1
		                                ├── Connection2
		                                ├── Connection3
		                                └── ...

              				따라서 먼저 이 영역으로 들어가야 한다.
             ---------------------------------------------------------------------
            */
            Context envContext = (Context) initContext.lookup("java:comp/env");



            /*
             ---------------------------------------------------------------------
              3단계 : DataSource 검색

		              우리가 Tomcat의 context.xml 파일에
		              아래와 같이 설정했다고 가정하자:
		
		                  <Resource name="jdbc/jspbeginner"
		                            type="javax.sql.DataSource"
		                            ... />
		
		              이제 그 이름("jdbc/jspbeginner")으로 검색한다.
		
		              검색 결과 :
		
		                Tomcat이 미리 생성해 둔 커넥션 풀 객체 반환
		
		              그 객체를 DataSource 타입으로 형변환하여 저장
             ---------------------------------------------------------------------
            */
            dataSource = (DataSource) envContext.lookup("jdbc/jspbeginner");


            /*
		             여기까지 성공하면 :
		
		               dataSource 안에는
		               Tomcat이 관리하는 커넥션 풀이 들어 있다.
		
		               우리는 이제 이 객체를 통해 DB 연결을 빌려 쓸 수 있다.
            */

        } catch (NamingException e) {

            /*
             NamingException 발생 가능 상황 :

               1. context.xml에 Resource 등록 안 했을 경우
               2. name 오타
               3. Tomcat 설정 오류
               4. 서버 재시작 안 한 경우

             	이 경우 콘솔에 오류 출력
            */
            e.printStackTrace();
        }
    }



    /*
     =============================================================================
       3. Connection 반환 메소드

       DAO에서 사용 예:

           Connection con = DBCPUtil.getConnection();

	       내부 동작 과정 :
	
	         1. dataSource는 커넥션 풀을 관리하는 객체
	         2. getConnection() 호출 시
	         3. 이미 생성되어 있는 Connection 중 하나를 반환
	
	       중요한 점 :
	
	         이때 새로운 DB 연결을 만드는 것이 아니다.
	         이미 만들어 둔 연결을 "재사용"한다.
	
	       왜 빠른가?
	
	         DB 연결 생성은 매우 무거운 작업
	                       커넥션 풀은 그 비용을 줄이기 위해 존재한다.
     =============================================================================
    */
    public static Connection getConnection() throws SQLException {

        /*
		         내부 동작 흐름 :
		
		           - 커넥션 풀에 사용 가능한 연결이 있는지 확인
		           - 있으면 즉시 반환
		           - 없으면 대기
		           - 대기 시간 초과 시 SQLException 발생
		
		         사용 후 반드시 close() 호출해야 한다.
		
		         단,
		         close()는 실제 종료가 아니라
		         "사용 완료" 표시 후 풀로 반환하는 동작이다.
        */
        return dataSource.getConnection();
    }

}



