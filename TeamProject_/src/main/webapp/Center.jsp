<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="Dao.mainDAO" %>
<%@ page import="Vo.mainVO" %>

<main>
    <div class="container">
      <section class="page">
        <!-- Featured -->
        <div class="section-title">
          <h2>이번 주 인기 게임</h2>
          <p>Weekly Top Games</p>
        </div>

        <div class="games">
          <a class="game-card" href="#" aria-label="1위 Lost Ark">
            <div class="game-card__bg bg-lostark"></div>
            <div class="rank-badge"><span class="rank-circle">1</span> 1위</div>
            <div class="game-card__content">
              <div class="game-name">LOL</div>
              <div class="game-meta"><span class="meta-dot"></span> 5,412</div>
            </div>
          </a>

          <a class="game-card" href="#" aria-label="2위 Valorant">
            <div class="game-card__bg bg-valorant"></div>
            <div class="rank-badge"><span class="rank-circle">2</span> 2위</div>
            <div class="game-card__content">
              <div class="game-name">VALORANT</div>
              <div class="game-meta"><span class="meta-dot"></span> 3,876</div>
            </div>
          </a>

          <a class="game-card" href="#" aria-label="3위 Zelda">
            <div class="game-card__bg bg-zelda"></div>
            <div class="rank-badge"><span class="rank-circle">3</span> 3위</div>
            <div class="game-card__content">
              <div class="game-name">ZELDA</div>
              <div class="game-meta"><span class="meta-dot"></span> 2,965</div>
            </div>
          </a>
        </div>

        <div class="center-link">
          <span class="bar"></span>
          <a href="#" style="font-weight:1000;">더보기</a>
          <span class="arrow">›</span>
        </div>

        <!-- Board header -->
        <div class="board-header">
          <div class="board-header__title">파티원 모집 게시판</div>
          <div class="controls">
            <select class="select" aria-label="게임 장르">
              <option>플랫폼</option>
              <option>PC</option>
              <option>모바일</option>
              <option>콘솔</option>
              <option>기타</option>
            </select>

            <select class="select" aria-label="전체 목록">
              <option>게임 장르</option>
              <option>RTS</option>
              <option>RPG</option>
              <option>FPS</option>
              <option>기타</option>
            </select>

            <button class="btn" type="button">검색</button>
            <button class="btn secondary" type="button">전체글보기</button>
          </div>
        </div>

        <!-- 3 columns -->
        <div class="grid3">
<!-- Popular -->
<section class="panel">
  <div class="panel__head">
    <h3 class="panel__title">인기 게시글</h3>
        <span style="color:red; font-weight:900; font-size:12px;">HOT!</span>
  </div>

  <div class="panel__body">
    <ul class="list">
<%
    mainDAO dao = new mainDAO();
    ArrayList<mainVO> popularList = dao.popularList();

    if(popularList != null && !popularList.isEmpty()) {
        for(mainVO vo : popularList) {
%>
      <li class="item">
        <div class="item__text">
          <div class="title">🔥 <%= vo.getMa_title() %></div>
          <div class="meta">
            <span><%= vo.getMa_nickname() %></span>
            <span>추천수 <%= vo.getLike_count() %></span>
            <span><%= vo.getMa_create_at() %></span>
          </div>
        </div>
      </li>
<%
        }
    } else {
%>
      <li class="item">
        <div class="item__text">
          <div class="title">인기 게시글이 없습니다.</div>
          <div class="meta">
            <span>-</span>
            <span>-</span>
          </div>
        </div>
      </li>
<%
    }
%>
    </ul>
  </div>
</section>

          <!-- Latest -->
<section class="panel">
  <div class="panel__head">
    <h3 class="panel__title">최신 게시글</h3>
    <span style="color:var(--sub); font-weight:900; font-size:12px;">NEW!</span>
  </div>

  <div class="panel__body">
    <ul class="list">

<%
    mainDAO dao2 = new mainDAO();
    ArrayList<mainVO> list = dao2.mainList();

    if(list != null && !list.isEmpty()){
        for(mainVO vo : list){
%>

      <li class="item">

        <div class="item__text">
          <div class="title">
            <%= vo.getMa_title() %>
          </div>

          <div class="meta">
            <span><%= vo.getMa_nickname() %></span>
            <span>조회수 <%= vo.getMa_viewcount() %></span>
            <span><%= vo.getMa_create_at() %></span>
          </div>
        </div>

      </li>

<%
        }
    } else {
%>

      <li class="item">
        <div class="item__text">
          <div class="title">게시글이 없습니다.</div>
        </div>
      </li>

<%
    }
%>

    </ul>
  </div>
</section>

 <!-- Notices -->
<section class="panel">

  <div class="panel__head">
    <h3 class="panel__title">공지사항</h3>
        <span style="color:var(--sub); font-weight:900; font-size:12px;">NOTICE</span>
  </div>

  <div class="panel__body">
    <ul class="list">

<%
    mainDAO dao3 = new mainDAO();
    ArrayList<mainVO> noticeList = dao3.noticeList();

    if(noticeList != null && !noticeList.isEmpty()){

        for(mainVO vo : noticeList){
%>

      <li class="item">

        <div class="item__text">

          <div class="title">
            [공지] <%= vo.getMa_title() %>
          </div>

          <div class="meta">
            <span><%= vo.getMa_nickname() %></span>
            <span>조회수 <%= vo.getMa_viewcount() %></span>
            <span><%= vo.getMa_create_at() %></span>
          </div>

        </div>

      </li>

<%
        }

    } else {
%>

      <li class="item">
        <div class="item__text">
          <div class="title">등록된 공지사항이 없습니다.</div>
        </div>
      </li>

<%
    }
%>

    </ul>
  </div>

</section>
        </div>
      </section>
    </div>
  </main>
