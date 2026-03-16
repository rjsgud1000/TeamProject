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

    List<TrendGameVO> trendGamesAll = trendCacheService.getCachedList();
    List<TrendGameVO> trendGamesTop10 = new ArrayList<TrendGameVO>();
    List<TrendGameVO> trendGames11To20 = new ArrayList<TrendGameVO>();

    if (trendGamesAll != null && !trendGamesAll.isEmpty()) {
        for (int i = 0; i < trendGamesAll.size(); i++) {
            if (i < 10) {
                trendGamesTop10.add(trendGamesAll.get(i));
            } else if (i < 20) {
                trendGames11To20.add(trendGamesAll.get(i));
            } else {
                break;
            }
        }
    }

    int featuredCount = trendGamesTop10.size();
%>

<div class="section-title">
  <h2>인기 게임</h2>
  <p>Naver DataLab · PC 검색 기준</p>
</div>

<div class="featured-slider">
  <% if (featuredCount > 0) { %>
    <button type="button" class="featured-slider__btn featured-slider__btn--prev" aria-label="이전 슬라이드">‹</button>

    <div class="featured-slider__viewport">
      <div class="featured-slider__track" data-total-slides="<%= featuredCount %>">
<% for (int i = 0; i < featuredCount; i++) {
     TrendGameVO item = trendGamesTop10.get(i);
     String displayTitle = item.getTitle();
%>
            <div class="featured-slide">
              <a class="game-card trend-card"
                 href="#rank11to20ChartPopup"
                 style="background-image:url('<%= request.getContextPath() + "/img/rank_slide/" + java.net.URLEncoder.encode(displayTitle, "UTF-8").replace("+", "%20") + ".png" %>');">
                <div class="rank-badge">
                  <span class="rank-circle"><%= item.getRank() %></span>
                  <%= item.getRank() %>위
                </div>

                <div class="game-card__content">
                  <div>
                    <div class="game-name"><%= displayTitle %></div>
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

<div id="rank11to20ChartPopup" class="chart-popup">
  <a href="#" class="chart-popup__dim"></a>

  <div class="chart-popup__panel">
    <div class="chart-popup__head">
      <div>
        <h3>인기 게임 11~20위</h3>
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
            if (trendGames11To20 != null && !trendGames11To20.isEmpty()) {
                for (TrendGameVO item : trendGames11To20) {
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
              <td colspan="4">11~20위 차트 데이터를 불러오지 못했습니다.</td>
            </tr>
          <% } %>
        </tbody>
      </table>
    </div>
  </div>
</div>

<!-- Board header -->
<div class="board-header">
  <div class="board-header__title">최신 정보</div>
  <div class="controls">
    <select class="select" id="gameSelect" aria-label="게임 선택">
      <option value="lol">리그 오브 레전드</option>
      <option value="battleground">배틀그라운드</option>
      <option value="valorant">발로란트</option>
      <option value="fconline">FC 온라인</option>
      <option value="maplestory">메이플스토리</option>
      <option value="dnf">던전앤파이터</option>
      <option value="lostark">로스트아크</option>
    </select>

    <select class="select" id="typeSelect" aria-label="정보 선택">
      <option value="notice">공지사항</option>
      <option value="patch">패치노트</option>
      <option value="news">뉴스</option>
    </select>

    <button class="btn" type="button" id="crawlBtn">검색</button>
  </div>
</div>

<style>
  .crawl-result-box {
    margin-top: 20px;
    border: 1px solid #e5e7eb;
    border-radius: 14px;
    padding: 18px;
    background: #fff;
  }
  .crawl-result-meta {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    flex-wrap: wrap;
    margin-bottom: 14px;
    color: #6b7280;
    font-size: 14px;
  }
  .crawl-list {
    display: grid;
    grid-template-columns: 1fr;
    gap: 12px;
    margin: 0;
    padding: 0;
    list-style: none;
  }
  .crawl-item {
    border: 1px solid #edf0f3;
    border-radius: 12px;
    padding: 14px 16px;
    background: #fafafa;
  }
  .crawl-item__title {
    font-size: 16px;
    font-weight: 700;
    margin-bottom: 6px;
  }
  .crawl-item__title a {
    color: #111827;
    text-decoration: none;
  }
  .crawl-item__title a:hover {
    text-decoration: underline;
  }
  .crawl-item__date {
    font-size: 13px;
    color: #6b7280;
    margin-bottom: 6px;
  }
  .crawl-item__summary {
    font-size: 14px;
    color: #374151;
    line-height: 1.6;
  }
  .crawl-message {
    color: #374151;
    line-height: 1.6;
  }
</style>

<div id="crawlResult" class="crawl-result-box">
  <p class="crawl-message">검색 조건을 선택한 뒤 검색 버튼을 눌러주세요.</p>
</div>

<script>
(function () {
  var crawlBtn = document.getElementById('crawlBtn');
  var gameSelect = document.getElementById('gameSelect');
  var typeSelect = document.getElementById('typeSelect');
  var crawlResult = document.getElementById('crawlResult');

  if (!crawlBtn || !gameSelect || !typeSelect || !crawlResult) {
    return;
  }

  function escapeHtml(value) {
    return String(value || '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function renderMessage(message) {
    crawlResult.innerHTML = '<p class="crawl-message">' + escapeHtml(message) + '</p>';
  }

  function renderItems(data) {
    var items = Array.isArray(data.items) ? data.items : [];

    if (!items.length) {
      renderMessage('불러온 데이터가 없습니다.');
      return;
    }

    var html = '';
    html += '<div class="crawl-result-meta">';
    html += '  <div>총 ' + items.length + '건</div>';
    html += '  <div><a href="' + escapeHtml(data.sourceUrl || '#') + '" target="_blank" rel="noopener noreferrer">원본 페이지 바로가기</a></div>';
    html += '</div>';
    html += '<ul class="crawl-list">';

    items.forEach(function (item) {
      html += '<li class="crawl-item">';
      html += '  <div class="crawl-item__title"><a href="' + escapeHtml(item.url || '#') + '" target="_blank" rel="noopener noreferrer">' + escapeHtml(item.title || '제목 없음') + '</a></div>';
      if (item.date) {
        html += '  <div class="crawl-item__date">등록일: ' + escapeHtml(item.date) + '</div>';
      }
      if (item.summary) {
        html += '  <div class="crawl-item__summary">' + escapeHtml(item.summary) + '</div>';
      }
      html += '</li>';
    });

    html += '</ul>';
    crawlResult.innerHTML = html;
  }

  crawlBtn.addEventListener('click', function () {
    var game = gameSelect.value;
    var type = typeSelect.value;
    renderMessage('불러오는 중입니다...');

    var url = '<%= request.getContextPath() %>/crawl/latest?game=' + encodeURIComponent(game) + '&type=' + encodeURIComponent(type);

    fetch(url, {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      }
    })
    .then(function (response) {
      if (!response.ok) {
        throw new Error('서버 응답 오류: ' + response.status);
      }
      return response.json();
    })
    .then(function (data) {
      if (!data.ok) {
        renderMessage(data.message || '데이터를 불러오지 못했습니다.');
        return;
      }
      renderItems(data);
    })
    .catch(function (error) {
      renderMessage('크롤링 요청에 실패했습니다. ' + error.message);
    });
  });
})();
</script>

        <!-- 3 columns -->
        <!-- 각종 게시글 정렬 및 게시판 링크 추가 -->
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
                    <div class="title">
                      🔥
                      <a href="<%=request.getContextPath()%>/board/detail?postId=<%=vo.getMa_post_id()%>&category=<%=vo.getMa_category()%>&page=1"
                         style="text-decoration: none; color: inherit;">
                        <%=vo.getMa_title()%>
                      </a>
                    </div>

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
                      <a href="<%=request.getContextPath()%>/board/detail?postId=<%=vo.getMa_post_id()%>&category=<%=vo.getMa_category()%>&page=1"
                         style="text-decoration: none; color: inherit;">
                        <%=vo.getMa_title()%>
                      </a>
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
                      <a href="<%=request.getContextPath()%>/board/detail?postId=<%=vo.getMa_post_id()%>&category=<%=vo.getMa_category()%>&page=1"
                         style="text-decoration: none; color: inherit;">
                        [공지] <%= vo.getMa_title() %>
                      </a>
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

        </div> <!-- /.grid3 -->
      </div> <!-- /.container -->
    </main>
