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

<div class="section-title section-title--games">
  <div>
    <h2>인기 게임</h2>
    <p id="rankingSourceText">Naver DataLab · PC 검색량 기준</p>
  </div>

  <div class="chart-toggle" aria-label="차트 전환">
    <button type="button"
            class="chart-toggle__btn is-active"
            data-chart-source="naver">
      온라인 게임
    </button>

    <button type="button"
            class="chart-toggle__btn"
            data-chart-source="steam">
      스팀 게임
    </button>

    <button type="button"
            class="chart-toggle__btn"
            data-chart-source="playstore">
      모바일 게임
    </button>
  </div>
</div>

<div class="game-chart-panel is-active" data-chart-panel="naver">
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
</div>

<div class="game-chart-panel" data-chart-panel="steam">
  <div class="featured-slider featured-slider--steam" id="steamFeaturedSlider">
    <div class="featured-loading">스팀 차트를 불러오는 중입니다...</div>
  </div>
</div>

<div class="game-chart-panel" data-chart-panel="playstore">
  <div class="featured-slider featured-slider--playstore" id="playstoreFeaturedSlider">
    <div class="featured-loading">플레이스토어 차트를 불러오는 중입니다...</div>
  </div>
</div>

<script>
  (function () {
    var contextPath = '<%= request.getContextPath() %>';
    var sourceText = document.getElementById('rankingSourceText');
    var toggleButtons = document.querySelectorAll('.chart-toggle__btn');
    var panels = document.querySelectorAll('.game-chart-panel');

    var steamSliderContainer = document.getElementById('steamFeaturedSlider');
    var steamPopupBody = document.getElementById('steamChartPopupBody');

    var playstoreSliderContainer = document.getElementById('playstoreFeaturedSlider');
    var playstorePopupBody = document.getElementById('playstoreChartPopupBody');

    var steamLoaded = false;
    var steamLoading = false;

    var playstoreLoaded = false;
    var playstoreLoading = false;

    function escapeHtml(value) {
      if (value == null) return '';
      return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
    }

    function initFeaturedSlider(slider) {
    	  if (!slider) return;

    	  var oldAutoTimer = slider._autoSlideTimer;
    	  if (oldAutoTimer) {
    	    clearInterval(oldAutoTimer);
    	  }

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

    	  function getTotalSlides() {
    	    return track.children.length;
    	  }

    	  function getMaxIndex() {
    	    return Math.max(getTotalSlides() - getVisibleCount(), 0);
    	  }

    	  function updateSlider(animate) {
    	    var visibleCount = getVisibleCount();
    	    var totalSlides = getTotalSlides();

    	    if (!totalSlides) return;

    	    var maxIndex = Math.max(totalSlides - visibleCount, 0);

    	    if (currentIndex < 0) {
    	      currentIndex = maxIndex;
    	    }
    	    if (currentIndex > maxIndex) {
    	      currentIndex = 0;
    	    }

    	    track.style.transition = animate === false ? 'none' : 'transform 0.45s ease';
    	    var translatePercent = (100 / visibleCount) * currentIndex;
    	    track.style.transform = 'translateX(-' + translatePercent + '%)';

    	    prevBtn.disabled = false;
    	    nextBtn.disabled = false;
    	  }

    	  function goPrev() {
    	    var maxIndex = getMaxIndex();
    	    if (maxIndex <= 0) {
    	      currentIndex = 0;
    	      updateSlider();
    	      return;
    	    }

    	    if (currentIndex <= 0) {
    	      currentIndex = maxIndex;
    	    } else {
    	      currentIndex -= 1;
    	    }

    	    updateSlider();
    	  }

    	  function goNext() {
    	    var maxIndex = getMaxIndex();
    	    if (maxIndex <= 0) {
    	      currentIndex = 0;
    	      updateSlider();
    	      return;
    	    }

    	    if (currentIndex >= maxIndex) {
    	      currentIndex = 0;
    	    } else {
    	      currentIndex += 1;
    	    }

    	    updateSlider();
    	  }

    	  function restartAutoSlide() {
    	    if (slider._autoSlideTimer) {
    	      clearInterval(slider._autoSlideTimer);
    	    }

    	    slider._autoSlideTimer = setInterval(function () {
    	      goNext();
    	    }, 10000);
    	  }

    	  prevBtn.onclick = null;
    	  nextBtn.onclick = null;

    	  prevBtn.addEventListener('click', function () {
    	    goPrev();
    	    restartAutoSlide();
    	  });

    	  nextBtn.addEventListener('click', function () {
    	    goNext();
    	    restartAutoSlide();
    	  });

    	  slider.addEventListener('mouseenter', function () {
    	    if (slider._autoSlideTimer) {
    	      clearInterval(slider._autoSlideTimer);
    	    }
    	  });

    	  slider.addEventListener('mouseleave', function () {
    	    restartAutoSlide();
    	  });

    	  window.addEventListener('resize', function () {
    	    var maxIndex = getMaxIndex();
    	    if (currentIndex > maxIndex) {
    	      currentIndex = maxIndex;
    	    }
    	    updateSlider(false);
    	  });

    	  updateSlider(false);
    	  restartAutoSlide();
    	  slider.dataset.sliderReady = 'true';
    	}

    function renderSteamChart(items) {
      var top10 = items.slice(0, 10);
      var top11to20 = items.slice(10, 20);

      if (!top10.length) {
        steamSliderContainer.innerHTML =
          '<div class="featured-empty">스팀 차트를 불러오지 못했습니다.</div>';
        return;
      }

      var sliderHtml = '';
      sliderHtml += '<button type="button" class="featured-slider__btn featured-slider__btn--prev" aria-label="이전 슬라이드">‹</button>';
      sliderHtml += '<div class="featured-slider__viewport">';
      sliderHtml += '  <div class="featured-slider__track" data-total-slides="' + top10.length + '">';

      top10.forEach(function (item) {
        var title = escapeHtml(item.title || ('Steam App #' + item.appId));
        var storeUrl = escapeHtml(item.storeUrl || '#');
        var bgStyle = item.headerImage
          ? ' style="background-image:url(\'' + escapeHtml(item.headerImage) + '\');"'
          : '';

        sliderHtml += ''
          + '<div class="featured-slide">'
          + '  <a class="game-card trend-card trend-card--steam" href="' + storeUrl + '" target="_blank" rel="noopener noreferrer"' + bgStyle + '>'
          + '    <div class="rank-badge">'
          + '      <span class="rank-circle">' + item.rank + '</span>'
          + '      ' + item.rank + '위'
          + '    </div>'
          + '    <div class="game-card__content game-card__content--steam">'
          + '      <div class="game-card__left">'
          + '        <div class="game-name">' + title + '</div>'
          + '        <div class="game-submeta">Steam Top Sellers · KR</div>'
          + '      </div>'
          + '      <div class="game-meta">'
          + '        <span class="meta-dot"></span>'
          + '        실시간 매출 순위'
          + '      </div>'
          + '    </div>'
          + '  </a>'
          + '</div>';
      });

      sliderHtml += '  </div>';
      sliderHtml += '</div>';
      sliderHtml += '<button type="button" class="featured-slider__btn featured-slider__btn--next" aria-label="다음 슬라이드">›</button>';

      steamSliderContainer.innerHTML = sliderHtml;
      initFeaturedSlider(steamSliderContainer);

      if (steamPopupBody) {
        var popupRows = '';

        if (top11to20.length) {
          top11to20.forEach(function (item) {
            var title = escapeHtml(item.title || ('Steam App #' + item.appId));
            var storeUrl = escapeHtml(item.storeUrl || '#');

            popupRows += ''
              + '<tr>'
              + '  <td class="rank-col">' + item.rank + '</td>'
              + '  <td class="name-col"><a href="' + storeUrl + '" target="_blank" rel="noopener noreferrer">' + title + '</a></td>'
              + '  <td>Steam KR</td>'
              + '  <td><a href="' + storeUrl + '" target="_blank" rel="noopener noreferrer">상점 바로가기</a></td>'
              + '</tr>';
          });
        } else {
          popupRows = '<tr><td colspan="4">11~20위 차트 데이터가 없습니다.</td></tr>';
        }

        steamPopupBody.innerHTML = popupRows;
      }
    }

    function renderPlayStoreChart(items) {
    	  var top10 = items.slice(0, 10);
    	  var top11to20 = items.slice(10, 20);

    	  if (!top10.length) {
    	    playstoreSliderContainer.innerHTML =
    	      '<div class="featured-empty">플레이스토어 차트를 불러오지 못했습니다.</div>';
    	    return;
    	  }

    	  var sliderHtml = '';
    	  sliderHtml += '<button type="button" class="featured-slider__btn featured-slider__btn--prev" aria-label="이전 슬라이드">‹</button>';
    	  sliderHtml += '<div class="featured-slider__viewport">';
    	  sliderHtml += '  <div class="featured-slider__track" data-total-slides="' + top10.length + '">';

    	  top10.forEach(function (item) {
    	    var title = escapeHtml(item.title || 'Google Play App');
    	    var developer = escapeHtml(item.developer || 'Google Play');
    	    var storeUrl = escapeHtml(item.storeUrl || '#');
    	    var heroImageUrl = escapeHtml(item.heroImageUrl || '');
    	    var screenshotUrl = escapeHtml(item.screenshotUrl || '');

    	    var bgImage = heroImageUrl || screenshotUrl;
    	    var bgStyle = bgImage
    	      ? ' style="background-image:url(\'' + bgImage + '\');"'
    	      : '';

    	    var useImageClass = bgImage ? ' trend-card--playstore-image' : '';

    	    sliderHtml += ''
    	      + '<div class="featured-slide">'
    	      + '  <a class="game-card trend-card trend-card--playstore' + useImageClass + '" href="' + storeUrl + '" target="_blank" rel="noopener noreferrer"' + bgStyle + '>'
    	      + '    <div class="rank-badge">'
    	      + '      <span class="rank-circle">' + item.rank + '</span>'
    	      + '      ' + item.rank + '위'
    	      + '    </div>'
    	      + '    <div class="game-card__content game-card__content--playstore">'
    	      + '      <div class="game-card__left">'
    	      + '        <div class="game-name">' + title + '</div>'
    	      + '        <div class="game-submeta">' + developer + '</div>'
    	      + '      </div>'
    	      + '      <div class="game-meta">'
    	      + '        <span class="meta-dot"></span>'
    	      + '        최고 매출'
    	      + '      </div>'
    	      + '    </div>'
    	      + '  </a>'
    	      + '</div>';
    	  });

    	  sliderHtml += '  </div>';
    	  sliderHtml += '</div>';
    	  sliderHtml += '<button type="button" class="featured-slider__btn featured-slider__btn--next" aria-label="다음 슬라이드">›</button>';

    	  playstoreSliderContainer.innerHTML = sliderHtml;
    	  initFeaturedSlider(playstoreSliderContainer);

    	  if (playstorePopupBody) {
    	    var popupRows = '';

    	    if (top11to20.length) {
    	      top11to20.forEach(function (item) {
    	        var title = escapeHtml(item.title || 'Google Play App');
    	        var developer = escapeHtml(item.developer || 'Google Play');
    	        var storeUrl = escapeHtml(item.storeUrl || '#');

    	        popupRows += ''
    	          + '<tr>'
    	          + '  <td class="rank-col">' + item.rank + '</td>'
    	          + '  <td class="name-col"><a href="' + storeUrl + '" target="_blank" rel="noopener noreferrer">' + title + '</a></td>'
    	          + '  <td>' + developer + '</td>'
    	          + '  <td><a href="' + storeUrl + '" target="_blank" rel="noopener noreferrer">스토어 바로가기</a></td>'
    	          + '</tr>';
    	      });
    	    } else {
    	      popupRows = '<tr><td colspan="4">11~20위 차트 데이터가 없습니다.</td></tr>';
    	    }

    	    playstorePopupBody.innerHTML = popupRows;
    	  }
    	}

    function loadSteamChart() {
      if (steamLoaded || steamLoading) return;

      steamLoading = true;
      steamSliderContainer.innerHTML =
        '<div class="featured-loading">스팀 차트를 불러오는 중입니다...</div>';

      fetch(contextPath + '/api/steam/top-sellers?limit=20', {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
      })
      .then(function (response) {
        if (!response.ok) throw new Error('HTTP ' + response.status);
        return response.json();
      })
      .then(function (items) {
        steamLoaded = true;
        steamLoading = false;
        renderSteamChart(Array.isArray(items) ? items : []);
      })
      .catch(function (error) {
        steamLoading = false;
        console.error(error);
        steamSliderContainer.innerHTML =
          '<div class="featured-empty">스팀 차트를 불러오지 못했습니다.</div>';
      });
    }

    function loadPlayStoreChart() {
    	  if (!playstoreSliderContainer) return;
    	  if (playstoreLoaded || playstoreLoading) return;

    	  playstoreLoading = true;
    	  playstoreSliderContainer.innerHTML =
    	    '<div class="featured-loading">플레이스토어 차트를 불러오는 중입니다...</div>';

    	  fetch(contextPath + '/api/playstore/top-grossing?limit=20', {
    	    method: 'GET',
    	    headers: { 'Accept': 'application/json' }
    	  })
    	  .then(function (response) {
    	    return response.json().then(function (data) {
    	      return { ok: response.ok, data: data };
    	    });
    	  })
    	  .then(function (result) {
    	    playstoreLoading = false;

    	    if (!result.ok || !result.data.success) {
    	      playstoreSliderContainer.innerHTML =
    	        '<div class="featured-empty">' +
    	        escapeHtml((result.data && result.data.message) || '플레이스토어 차트를 불러오지 못했습니다.') +
    	        '</div>';
    	      return;
    	    }

    	    playstoreLoaded = true;
    	    renderPlayStoreChart(Array.isArray(result.data.items) ? result.data.items : []);
    	  })
    	  .catch(function (error) {
    	    playstoreLoading = false;
    	    console.error(error);
    	    playstoreSliderContainer.innerHTML =
    	      '<div class="featured-empty">플레이스토어 차트를 불러오지 못했습니다.</div>';
    	  });
    	}

    toggleButtons.forEach(function (btn) {
      btn.addEventListener('click', function () {
        var source = btn.getAttribute('data-chart-source');

        toggleButtons.forEach(function (b) {
          b.classList.toggle('is-active', b === btn);
        });

        panels.forEach(function (panel) {
          panel.classList.toggle(
            'is-active',
            panel.getAttribute('data-chart-panel') === source
          );
        });

        if (source === 'steam') {
          sourceText.textContent = 'Steam Top Sellers · KR 기준';
          loadSteamChart();
        } else if (source === 'playstore') {
          sourceText.textContent = 'Google Play 최고 매출 · KR 기준';
          loadPlayStoreChart();
        } else {
          sourceText.textContent = 'Naver DataLab · PC 검색 기준';
        }
      });
    });

    document.querySelectorAll('.featured-slider').forEach(function (slider) {
      initFeaturedSlider(slider);
    });
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

<div id="steamRankChartPopup" class="chart-popup">
  <a href="#" class="chart-popup__dim"></a>

  <div class="chart-popup__panel">
    <div class="chart-popup__head">
      <div>
        <h3>스팀 인기 게임 11~20위</h3>
        <p>Steam Top Sellers · KR 기준</p>
      </div>
      <a href="#" class="chart-popup__close">✕</a>
    </div>

    <div class="chart-popup__body">
      <table class="chart-table">
        <thead>
          <tr>
            <th>순위</th>
            <th>게임명</th>
            <th>기준</th>
            <th>링크</th>
          </tr>
        </thead>
        <tbody id="steamChartPopupBody">
          <tr>
            <td colspan="4">스팀 차트를 불러오는 중입니다...</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>

<div id="playstoreRankChartPopup" class="chart-popup">
  <a href="#" class="chart-popup__dim"></a>

  <div class="chart-popup__panel">
    <div class="chart-popup__head">
      <div>
        <h3>플레이스토어 인기 게임 11~20위</h3>
        <p>Google Play 최고 매출 · KR 기준</p>
      </div>
      <a href="#" class="chart-popup__close">✕</a>
    </div>

    <div class="chart-popup__body">
      <table class="chart-table">
        <thead>
          <tr>
            <th>순위</th>
            <th>게임명</th>
            <th>기준</th>
            <th>링크</th>
          </tr>
        </thead>
        <tbody id="playstoreChartPopupBody">
          <tr>
            <td colspan="4">플레이스토어 차트를 불러오는 중입니다...</td>
          </tr>
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
