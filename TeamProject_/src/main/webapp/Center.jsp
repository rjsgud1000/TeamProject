<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
              <div class="game-name">LOSTARK</div>
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
              <span style="color:var(--sub); font-weight:900; font-size:12px;">TOP</span>
            </div>
            <div class="panel__body">
              <ul class="list">
                <li class="item">
                  <span class="hot">🔥</span>
                  <div class="item__text">
                    <div class="title">[질문] 발로란트 듀오 같이 하실 분?</div>
                  </div>
                  <div class="count"><span class="lock"></span> 22</div>
                </li>

                <li class="item">
                  <span class="hot">🔥</span>
                  <div class="item__text">
                    <div class="title">[자유] 로스트아크 유저분들께 팁 공유</div>
                  </div>
                  <div class="count"><span class="lock"></span> 27</div>
                </li>

                <li class="item">
                  <span class="hot">🔥</span>
                  <div class="item__text">
                    <div class="title">[자유] 닌텐도 스위치 추천 게임 알려주세요</div>
                  </div>
                  <div class="count"><span class="lock"></span> 21</div>
                </li>

                <li class="item">
                  <span class="hot">🔥</span>
                  <div class="item__text">
                    <div class="title">[질문] 차지 후 회복스탯 버그 요안</div>
                  </div>
                  <div class="count"><span class="lock"></span> 19</div>
                </li>

                <li class="item">
                  <span class="hot">🔥</span>
                  <div class="item__text">
                    <div class="title">[자유] 닌텐도의 미래 가능성 어떨까요?</div>
                  </div>
                  <div class="count"><span class="lock"></span> 17</div>
                </li>
              </ul>
            </div>
          </section>

          <!-- Latest -->
          <section class="panel">
            <div class="panel__head">
              <h3 class="panel__title">최신 게시글</h3>
              <span style="color:var(--sub); font-weight:900; font-size:12px;">NEW</span>
            </div>
            <div class="panel__body">
              <ul class="list">
                <li class="item">
                  <span class="avatar">A</span>
                  <div class="item__text">
                    <div class="title">[협력] PSS 모 같이 하실 분 구합니다</div>
                    <div class="meta"><span>루틴밍</span><span>방금 전</span></div>
                  </div>
                </li>

                <li class="item">
                  <span class="avatar">B</span>
                  <div class="item__text">
                    <div class="title">[소식] 이번 게임 BJ 방송 논란이네요 ㅋㅋ</div>
                    <div class="meta"><span>밤하늘토끼</span><span>17분 전</span></div>
                  </div>
                </li>

                <li class="item">
                  <span class="avatar">C</span>
                  <div class="item__text">
                    <div class="title">[질문] 발로란트 듀오 같이 하실 분?</div>
                    <div class="meta"><span>BlueSky</span><span>1시간 전</span></div>
                  </div>
                </li>

                <li class="item">
                  <span class="avatar">D</span>
                  <div class="item__text">
                    <div class="title">[자유] 로스트아크 유저분들께 팁 공유</div>
                    <div class="meta"><span>Gamingring</span><span>2시간 전</span></div>
                  </div>
                </li>

                <li class="item">
                  <span class="avatar">E</span>
                  <div class="item__text">
                    <div class="title">[자유] 닌텐도 스위치 추천 게임 알려주세요</div>
                    <div class="meta"><span>Meun</span><span>6시간 전</span></div>
                  </div>
                </li>
              </ul>
            </div>
          </section>

          <!-- Notices -->
          <section class="panel">
            <div class="panel__head">
              <h3 class="panel__title">공지사항</h3>
              <div class="tabs" role="tablist" aria-label="공지 탭">
                <button class="tab is-active" type="button" data-tab="notice">공지</button>
                <button class="tab" type="button" data-tab="update">최신작업</button>
              </div>
            </div>

            <div class="notice" data-pane="notice">
              <div class="notice-row">
                <span class="bullet"></span>
                <div class="notice-title">게시판 이용규칙 안내</div>
                <div class="date">2024.04.22</div>
              </div>
              <div class="notice-row">
                <span class="bullet"></span>
                <div class="notice-title">새로운 커뮤니티 기능 업데이트</div>
                <div class="date">2024.04.22</div>
              </div>
              <div class="notice-row">
                <span class="bullet"></span>
                <div class="notice-title">게임 티어 이벤트 당첨자 발표</div>
                <div class="date">2024.04.20</div>
              </div>
            </div>

            <div class="notice" data-pane="update" style="display:none;">
              <div class="notice-row">
                <span class="bullet"></span>
                <div class="notice-title">게시판 이용규칙 미세 수정</div>
                <div class="date">2024.04.23</div>
              </div>
              <div class="notice-row">
                <span class="bullet"></span>
                <div class="notice-title">새로운 커뮤니티 감정 가이드</div>
                <div class="date">2024.04.22</div>
              </div>
              <div class="notice-row">
                <span class="bullet"></span>
                <div class="notice-title">게임 티어 이벤트 당첨자 발표</div>
                <div class="date">2024.04.20</div>
              </div>
            </div>
          </section>
        </div>
      </section>
    </div>
  </main>
