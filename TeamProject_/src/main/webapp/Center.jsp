<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="Dao.mainDAO" %>
<%@ page import="Vo.mainVO" %>
<%@ page import="java.util.List" %>
<%@ page import="Service.NaverTrendCacheService" %>
<%@ page import="Vo.TrendGameVO" %>


<main>
    <div class="container">
<%
    NaverTrendCacheService trendCacheService = NaverTrendCacheService.getInstance();
    List<TrendGameVO> trendGames = trendCacheService.getCachedList();
    int featuredCount = trendGames.size() >= 10 ? 10 : trendGames.size();
%>

<div class="section-title">
  <h2>인기 게임 차트</h2>
  <p>Naver DataLab · PC 검색 기준</p>
</div>

<div class="featured-slider">
  <% if (featuredCount > 0) { %>
    <button type="button" class="featured-slider__btn featured-slider__btn--prev" aria-label="이전 슬라이드">‹</button>

    <div class="featured-slider__viewport">
      <div class="featured-slider__track" data-total-slides="<%= featuredCount %>">
        <% for (int i = 0; i < featuredCount; i++) {
             TrendGameVO item = trendGames.get(i);
        %>
          <div class="featured-slide">
            <a class="game-card trend-card" href="#top10ChartPopup">
              <div class="rank-badge">
                <span class="rank-circle"><%= item.getRank() %></span>
                <%= item.getRank() %>위
              </div>

              <div class="game-card__content">
                <div>
                  <div class="game-name"><%= item.getTitle() %></div>
                  <div class="game-submeta">평균 점수 <%= String.format("%.1f", item.getScore()) %></div>
                </div>

                <div class="game-meta">
                  <span class="meta-dot"></span>
                  <%= item.getSource() %>
                </div>
              </div>
            </a>
          </div>
        <% } %>
      </div>
    </div>

    <button type="button" class="featured-slider__btn featured-slider__btn--next" aria-label="다음 슬라이드">›</button>
  <% } else { %>
    <div class="featured-empty">
      인기 게임 데이터를 불러오지 못했습니다.
    </div>
  <% } %>
</div>

<script>
  (function () {
    var slider = document.querySelector('.featured-slider');
    if (!slider) return;

    var track = slider.querySelector('.featured-slider__track');
    var prevBtn = slider.querySelector('.featured-slider__btn--prev');
    var nextBtn = slider.querySelector('.featured-slider__btn--next');
    if (!track || !prevBtn || !nextBtn) return;

    var currentIndex = 0;

    function getVisibleCount() {
      var width = window.innerWidth || document.documentElement.clientWidth;
      if (width <= 640) return 1;
      if (width <= 900) return 2;
      return 3;
    }

    function updateSlider() {
      var totalSlides = track.children.length;
      var visibleCount = getVisibleCount();
      var maxIndex = Math.max(totalSlides - visibleCount, 0);

      if (currentIndex > maxIndex) {
        currentIndex = maxIndex;
      }

      var translatePercent = (100 / visibleCount) * currentIndex;
      track.style.transform = 'translateX(-' + translatePercent + '%)';

      prevBtn.disabled = currentIndex <= 0;
      nextBtn.disabled = currentIndex >= maxIndex;
    }

    prevBtn.addEventListener('click', function () {
      if (currentIndex > 0) {
        currentIndex -= 1;
        updateSlider();
      }
    });

    nextBtn.addEventListener('click', function () {
      var maxIndex = Math.max(track.children.length - getVisibleCount(), 0);
      if (currentIndex < maxIndex) {
        currentIndex += 1;
        updateSlider();
      }
    });

    window.addEventListener('resize', updateSlider);
    updateSlider();
  })();
</script>

<div id="top10ChartPopup" class="chart-popup">
  <a href="#" class="chart-popup__dim"></a>

  <div class="chart-popup__panel">
    <div class="chart-popup__head">
      <div>
        <h3>인기 게임 TOP 10</h3>
        <p>Naver DataLab · 최근 7일 평균 · PC 기준</p>
      </div>
      <a href="#" class="chart-popup__close">✕</a>
    </div>

    <div class="chart-popup__body">
      <table class="chart-table">
        <thead>
          <tr>
            <th>순위</th>
            <th>게임명</th>
            <th>점수</th>
            <th>기간</th>
          </tr>
        </thead>
        <tbody>
          <%
            if (trendGames != null && !trendGames.isEmpty()) {
                for (TrendGameVO item : trendGames) {
          %>
            <tr>
              <td class="rank-col"><%= item.getRank() %></td>
              <td class="name-col"><%= item.getTitle() %></td>
              <td><%= String.format("%.1f", item.getScore()) %></td>
              <td><%= item.getPeriod() %></td>
            </tr>
          <%
                }
            } else {
          %>
            <tr>
              <td colspan="4">차트 데이터를 불러오지 못했습니다.</td>
            </tr>
          <% } %>
        </tbody>
      </table>
    </div>
  </div>
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
