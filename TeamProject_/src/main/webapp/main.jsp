<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	String loginFlash = (String) session.getAttribute("loginFlash");
	if (loginFlash != null) {
		session.removeAttribute("loginFlash");
%>

<script type="text/javascript">
	alert("<%= loginFlash.replace("\\", "\\\\").replace("\"", "\\\"") %>");
</script>
<%
	}
%>
<!doctype html>
<html lang="ko">
<head>
  <!-- 인기글 이모티콘 사이즈 -->
  <link rel="stylesheet" href="/css/main.css">
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>G-UNIVERSE - 홈</title>

  <style>
  .chart-popup__more-wrap{
  display:flex;
  justify-content:center;
  margin-top:18px;
}

<!-- 인기글 이모티콘 사이즈 -->
.material-icons{
    font-size:20px;
    vertical-align:middle;
    margin-right:6px;
    color:#1f4fa3;
}
.section-title--games{
  align-items:center;
}

.chart-toggle{
  display:flex;
  align-items:center;
  gap:8px;
}

.chart-toggle__btn{
  min-width:84px;
  height:38px;
  padding:0 14px;
  border:1px solid rgba(37, 99, 235, .18);
  border-radius:999px;
  background:#fff;
  color:#475569;
  font-size:13px;
  font-weight:900;
  cursor:pointer;
  box-shadow:0 8px 20px rgba(15, 23, 42, .08);
  transition:.18s ease;
}

.chart-toggle__btn:hover{
  background:#eff6ff;
  color:#1d4ed8;
}

.chart-toggle__btn.is-active{
  background:#2563eb;
  border-color:#2563eb;
  color:#fff;
  box-shadow:0 12px 24px rgba(37, 99, 235, .24);
}

.game-chart-panel{
  display:none;
}

.game-chart-panel.is-active{
  display:block;
}

.featured-loading{
  background:#fff;
  border:1px solid #e5e7eb;
  border-radius:20px;
  padding:40px 20px;
  text-align:center;
  font-weight:900;
  color:#64748b;
  box-shadow:0 10px 25px rgba(15, 23, 42, .06);
}

.featured-empty{
  background:#fff;
  border:1px solid #e5e7eb;
  border-radius:20px;
  padding:40px 20px;
  text-align:center;
  font-weight:900;
  color:#64748b;
  box-shadow:0 10px 25px rgba(15, 23, 42, .06);
}

.trend-card--steam{
  position:relative;
  overflow:hidden;
  background-size:cover;
  background-position:center;
  background-repeat:no-repeat;
  border-radius:20px;
  min-height:210px;
  display:block;
  text-decoration:none;
}

.trend-card--steam::before{
  content:"";
  position:absolute;
  inset:0;
  background:linear-gradient(180deg, rgba(15,23,42,.06) 0%, rgba(15,23,42,.18) 42%, rgba(15,23,42,.72) 100%);
  pointer-events:none;
}

.trend-card--steam .rank-badge,
.trend-card--steam .game-card__content{
  position:relative;
  z-index:2;
}

.trend-card--steam .rank-badge{
  position:absolute;
  top:14px;
  left:14px;
  display:inline-flex;
  align-items:center;
  gap:8px;
  padding:7px 12px;
  border-radius:999px;
  background:rgba(255,255,255,.18);
  color:#fff;
  font-weight:900;
  font-size:13px;
  backdrop-filter:blur(10px);
  border:1px solid rgba(255,255,255,.24);
  box-shadow:0 8px 24px rgba(0,0,0,.16);
}

.trend-card--steam .rank-circle{
  width:22px;
  height:22px;
  border-radius:50%;
  background:rgba(255,255,255,.26);
  display:inline-flex;
  align-items:center;
  justify-content:center;
  font-size:12px;
  font-weight:900;
}

.trend-card--steam .game-card__content--steam{
  position:absolute;
  left:0;
  right:0;
  bottom:0;
  width:100%;
  padding:18px 18px 16px;
  display:flex;
  align-items:flex-end;
  justify-content:space-between;
  gap:14px;
  box-sizing:border-box;
}

.trend-card--steam .game-card__left{
  min-width:0;
  flex:1 1 auto;
}

.trend-card--steam .game-name{
  font-size:22px;
  font-weight:900;
  line-height:1.1;
  letter-spacing:-0.02em;
  color:#fff;
  text-shadow:0 2px 12px rgba(0,0,0,.35);
  display:-webkit-box;
  -webkit-line-clamp:2;
  -webkit-box-orient:vertical;
  overflow:hidden;
  word-break:keep-all;
}

.trend-card--steam .game-submeta{
  margin-top:4px;
  font-size:12px;
  font-weight:700;
  color:rgba(255,255,255,.82);
  line-height:1.2;
}

.trend-card--steam .game-meta{
  flex:0 0 auto;
  display:inline-flex;
  align-items:center;
  gap:7px;
  padding:7px 12px;
  border-radius:999px;
  background:rgba(15,23,42,.58);
  border:1px solid rgba(255,255,255,.16);
  color:#fff;
  font-size:12px;
  font-weight:800;
  line-height:1;
  white-space:nowrap;
  backdrop-filter:blur(4px);
}

.trend-card--steam .meta-dot{
  width:7px;
  height:7px;
  border-radius:50%;
  background:#22c55e;
  flex:0 0 auto;
}

@media (max-width: 900px){
  .trend-card--steam{
    min-height:190px;
  }

  .trend-card--steam .game-card__content--steam{
    padding:16px 16px 14px;
  }

  .trend-card--steam .game-name{
    font-size:20px;
  }

  .trend-card--steam .game-meta{
    font-size:11px;
    padding:6px 10px;
  }
}

@media (max-width: 640px){
  .trend-card--steam{
    min-height:180px;
  }

  .trend-card--steam .game-card__content--steam{
    padding:14px 14px 13px;
    gap:10px;
  }

  .trend-card--steam .game-name{
    font-size:18px;
  }

  .trend-card--steam .game-submeta{
    font-size:11px;
  }

  .trend-card--steam .game-meta{
    font-size:10px;
    padding:6px 9px;
  }
}

.chart-popup__more-btn{
  min-width:160px;
  height:44px;
  padding:0 18px;
  border:1px solid rgba(37, 99, 235, .18);
  border-radius:999px;
  background:#eff6ff;
  color:#1d4ed8;
  font-size:14px;
  font-weight:1000;
  cursor:pointer;
  box-shadow:0 10px 24px rgba(15, 23, 42, .08);
}

.chart-popup__more-btn:hover{
  background:#dbeafe;
}
  
    :root{
      --bgTop:#1f4a9a;
      --bgMid:#2c5db6;
      --bgBottom:#0f2f6d;
      --surface:#ffffff;
      --muted:#f3f6fb;
      --line:#e6edf7;
      --text:#0f172a;
      --sub:#64748b;
      --accent:#2d6cdf;
      --accent2:#1d4ed8;
      --chip:#0b2a68;
      --shadow: 0 18px 40px rgba(2, 18, 52, .14);
      --shadow2: 0 10px 26px rgba(2, 18, 52, .10);
      --r16:16px;
      --r20:20px;
      --r24:24px;
      --max: 1160px;
    }
    
    .trend-card--playstore{
  position:relative;
  overflow:hidden;
  border-radius:20px;
  min-height:210px;
  display:block;
  text-decoration:none;
  background:
    radial-gradient(circle at top right, rgba(255,255,255,.18), transparent 28%),
    linear-gradient(135deg, #16a34a 0%, #2563eb 100%);
  box-shadow:0 18px 40px rgba(37, 99, 235, .18);
}

.trend-card--playstore::before{
  content:"";
  position:absolute;
  inset:0;
  background:linear-gradient(180deg, rgba(15,23,42,.03) 0%, rgba(15,23,42,.16) 48%, rgba(15,23,42,.54) 100%);
  pointer-events:none;
}

.trend-card--playstore{
  position:relative;
  overflow:hidden;
  border-radius:20px;
  min-height:210px;
  display:block;
  text-decoration:none;
  background:
    radial-gradient(circle at top right, rgba(255,255,255,.18), transparent 28%),
    linear-gradient(135deg, #16a34a 0%, #2563eb 100%);
  box-shadow:0 18px 40px rgba(37, 99, 235, .18);
  background-size:cover;
  background-position:center;
  background-repeat:no-repeat;
}

.trend-card--playstore-image{
  background-color:#0f172a;
}

.trend-card--playstore::before{
  content:"";
  position:absolute;
  inset:0;
  background:linear-gradient(
    180deg,
    rgba(15,23,42,.04) 0%,
    rgba(15,23,42,.18) 42%,
    rgba(15,23,42,.68) 100%
  );
  pointer-events:none;
}

.trend-card--playstore .rank-badge,
.trend-card--playstore .game-card__content,
.trend-card--playstore .mobile-rank-card__icon{
  position:relative;
  z-index:2;
}

.trend-card--playstore .rank-badge{
  position:absolute;
  top:14px;
  left:14px;
  display:inline-flex;
  align-items:center;
  gap:8px;
  padding:7px 12px;
  border-radius:999px;
  background:rgba(255,255,255,.16);
  color:#fff;
  font-weight:900;
  font-size:13px;
  backdrop-filter:blur(10px);
  border:1px solid rgba(255,255,255,.24);
}

.trend-card--playstore .rank-circle{
  width:22px;
  height:22px;
  border-radius:50%;
  background:rgba(255,255,255,.24);
  display:inline-flex;
  align-items:center;
  justify-content:center;
  font-size:12px;
  font-weight:900;
}

.trend-card--playstore .game-card__content--playstore{
  position:absolute;
  left:0;
  right:0;
  bottom:0;
  width:100%;
  padding:18px 18px 16px;
  display:flex;
  align-items:flex-end;
  justify-content:space-between;
  gap:14px;
  box-sizing:border-box;
}

.trend-card--playstore .game-card__left{
  min-width:0;
  flex:1 1 auto;
}

.trend-card--playstore .game-name{
  font-size:22px;
  font-weight:900;
  line-height:1.1;
  letter-spacing:-0.02em;
  color:#fff;
  text-shadow:0 2px 12px rgba(0,0,0,.35);
  display:-webkit-box;
  -webkit-line-clamp:2;
  -webkit-box-orient:vertical;
  overflow:hidden;
  word-break:keep-all;
}

.trend-card--playstore .game-submeta{
  margin-top:4px;
  font-size:12px;
  font-weight:700;
  color:rgba(255,255,255,.84);
  line-height:1.2;
  display:-webkit-box;
  -webkit-line-clamp:1;
  -webkit-box-orient:vertical;
  overflow:hidden;
}

.trend-card--playstore .game-meta{
  flex:0 0 auto;
  display:inline-flex;
  align-items:center;
  gap:7px;
  padding:7px 12px;
  border-radius:999px;
  background:rgba(15,23,42,.48);
  border:1px solid rgba(255,255,255,.16);
  color:#fff;
  font-size:12px;
  font-weight:800;
  line-height:1;
  white-space:nowrap;
  backdrop-filter:blur(4px);
}

.trend-card--playstore .meta-dot{
  width:7px;
  height:7px;
  border-radius:50%;
  background:#22c55e;
  flex:0 0 auto;
}

@media (max-width: 900px){
  .trend-card--playstore{
    min-height:190px;
  }

  .trend-card--playstore .game-card__content--playstore{
    padding:16px 16px 14px;
  }

  .trend-card--playstore .game-name{
    font-size:20px;
  }

  .mobile-rank-card__icon{
    width:58px;
    height:58px;
  }
}

@media (max-width: 640px){
  .trend-card--playstore{
    min-height:180px;
  }

  .trend-card--playstore .game-card__content--playstore{
    padding:14px 14px 13px;
    gap:10px;
  }

  .trend-card--playstore .game-name{
    font-size:18px;
  }

  .trend-card--playstore .game-submeta{
    font-size:11px;
  }

  .trend-card--playstore .game-meta{
    font-size:10px;
    padding:6px 9px;
  }

  .mobile-rank-card__icon{
    width:52px;
    height:52px;
    border-radius:16px;
  }
}

@media (max-width: 900px){
  .trend-card--playstore{
    min-height:190px;
  }

  .trend-card--playstore .game-card__content--playstore{
    padding:16px 16px 14px;
  }

  .trend-card--playstore .game-name{
    font-size:20px;
  }

  .mobile-rank-card__icon{
    width:58px;
    height:58px;
  }
}

@media (max-width: 640px){
  .trend-card--playstore{
    min-height:180px;
  }

  .trend-card--playstore .game-card__content--playstore{
    padding:14px 14px 13px;
    gap:10px;
  }

  .trend-card--playstore .game-name{
    font-size:18px;
  }

  .trend-card--playstore .game-submeta{
    font-size:11px;
  }

  .trend-card--playstore .game-meta{
    font-size:10px;
    padding:6px 9px;
  }

  .mobile-rank-card__icon{
    width:52px;
    height:52px;
    border-radius:16px;
  }
}

    *{ box-sizing:border-box; }
    html,body{ height:100%; }
    body{
      margin:0;
      font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, "Apple SD Gothic Neo","Noto Sans KR","Malgun Gothic", Arial, sans-serif;
      color:var(--text);
      background:
        radial-gradient(1100px 520px at 26% 0%, rgba(255,255,255,.10), transparent 60%),
        radial-gradient(900px 460px at 82% 6%, rgba(255,255,255,.10), transparent 55%),
        linear-gradient(180deg, var(--bgTop) 0%, var(--bgMid) 36%, #f1f5fb 36%, #f1f5fb 100%);
    }

    a{ color:inherit; text-decoration:none; }
    .container{ width:min(var(--max), calc(100% - 40px)); margin:0 auto; }

    /* ===== Top bar ===== */
    .topbar{
      position:sticky; top:0; z-index:10;
      background: linear-gradient(180deg, rgba(13,40,92,.92), rgba(21,65,154,.82));
      backdrop-filter: blur(10px);
      border-bottom: 1px solid rgba(255,255,255,.10);
    }
    .topbar__inner{
      height:76px;
      display:flex; align-items:center; gap:22px;
    }
    .brand{
      display:flex; align-items:center; gap:10px;
      color:#fff; font-weight:800; letter-spacing:.4px;
    }
    .brand__logo{
      width:34px; height:34px; border-radius:10px;
      display:grid; place-items:center;
      background: rgba(255,255,255,.12);
      border:1px solid rgba(255,255,255,.18);
      box-shadow: 0 10px 22px rgba(0,0,0,.12);
      font-size:18px;
    }
    .brand__name{ font-size:20px; }

    .nav{
      display:flex; align-items:center; gap:18px;
      margin-left:auto;
    }
    .nav a{
      color:rgba(255,255,255,.86);
      font-weight:700;
      font-size:14px;
      padding:8px 10px;
      border-radius:10px;
      transition:.15s ease;
    }
    .nav a:hover{
      background: rgba(255,255,255,.12);
      color:#fff;
    }

    .search{
      display:flex; align-items:center; gap:10px;
      margin-left:14px;
    }
    .search__box{
      width:320px;
      height:38px;
      border-radius:999px;
      display:flex; align-items:center;
      background: rgba(255,255,255,.12);
      border:1px solid rgba(255,255,255,.22);
      overflow:hidden;
    }
    .search__box input{
      flex:1;
      height:100%;
      border:0;
      outline:none;
      background:transparent;
      color:#fff;
      padding:0 14px;
      font-size:13px;
    }
    .search__box input::placeholder{ color:rgba(255,255,255,.70); }
    .search__btn{
      width:44px; height:38px;
      border:0;
      cursor:pointer;
      background: rgba(255,255,255,.12);
      border-left:1px solid rgba(255,255,255,.18);
      color:#fff;
      font-weight:800;
    }
    .pill{
      height:38px;
      padding:0 14px;
      border-radius:999px;
      display:flex; align-items:center; gap:8px;
      color:#fff;
      background: rgba(0,0,0,.18);
      border:1px solid rgba(255,255,255,.14);
      font-size:13px;
      white-space:nowrap;
    }

    /* ===== Sub nav (blue strip) ===== */
    .subnav{
      background: linear-gradient(180deg, rgba(18,78,178,.55), rgba(12,50,120,.35));
      border-top: 1px solid rgba(255,255,255,.06);
      border-bottom: 1px solid rgba(255,255,255,.10);
    }
    .subnav__inner{
      height:46px;
      display:flex; align-items:center; gap:22px;
      color:rgba(255,255,255,.88);
      font-weight:700;
      font-size:14px;
    }
    .subnav__inner a{
      padding:8px 10px;
      border-radius:10px;
      transition:.15s ease;
    }
    .subnav__inner a:hover{ background: rgba(255,255,255,.10); color:#fff; }

    /* ===== Main ===== */
    main{ padding:26px 0 40px; }
    .page{
      background: rgba(255,255,255,.58);
      border:1px solid rgba(255,255,255,.56);
      border-radius: var(--r24);
      padding:24px;
      box-shadow: var(--shadow);
    }

    .section-title{
      display:flex; align-items:flex-end; justify-content:space-between;
      margin: 4px 4px 14px;
    }
    .section-title h2{
      margin:0;
      font-size:30px;
      letter-spacing:-.4px;
    }
    .section-title p{
      margin:0;
      color:var(--sub);
      font-weight:600;
      font-size:13px;
    }

    /* ===== Featured games ===== */
    .games{
      display:grid;
      grid-template-columns: 1fr 1fr 1fr;
      gap:16px;
      margin-top:10px;
    }
    .game-card{
      height:160px;
      border-radius: var(--r20);
      overflow:hidden;
      position:relative;
      background: #0b1f4c;
      box-shadow: var(--shadow2);
      border:1px solid rgba(255,255,255,.32);
    }
    .game-card::before{
      content:"";
      position:absolute; inset:0;
      background:
        radial-gradient(520px 220px at 26% 30%, rgba(255,255,255,.18), transparent 55%),
        linear-gradient(180deg, rgba(0,0,0,.10), rgba(0,0,0,.55));
      pointer-events:none;
    }
    .game-card__bg{
      position:absolute; inset:0;
      background-size: cover;
      background-position: center;
      filter: saturate(1.06) contrast(1.02);
      transform: scale(1.03);
    }

    /* TODO: 아래 이미지 URL을 실제 썸네일로 교체하세요 */
    .bg-lostark{  background-image: url("assets/featured_lostark.jpg"); }
    .bg-valorant{ background-image: url("assets/featured_valorant.jpg"); }
    .bg-zelda{    background-image: url("assets/featured_zelda.jpg"); }

    .rank-badge{
      position:absolute; top:12px; left:12px;
      display:flex; align-items:center; gap:8px;
      padding:8px 10px;
      border-radius: 14px;
      background: rgba(255,255,255,.16);
      border:1px solid rgba(255,255,255,.26);
      color:#fff;
      font-weight:900;
      font-size:13px;
      backdrop-filter: blur(8px);
    }
    .rank-circle{
      width:22px; height:22px;
      border-radius:999px;
      display:grid; place-items:center;
      background: rgba(255,255,255,.18);
      border:1px solid rgba(255,255,255,.28);
      font-size:12px;
      font-weight:1000;
    }

    .game-card__content{
      position:absolute; left:16px; right:16px; bottom:14px;
      display:flex; align-items:flex-end; justify-content:space-between;
      gap:14px;
      color:#fff;
    }
    .game-name{
      font-size:26px;
      font-weight:1000;
      letter-spacing:.2px;
      text-shadow: 0 10px 26px rgba(0,0,0,.35);
      line-height:1.05;
    }
    .game-meta{
      display:flex; align-items:center; gap:8px;
      color: rgba(255,255,255,.85);
      font-weight:800;
      font-size:13px;
      background: rgba(3, 12, 38, .35);
      border:1px solid rgba(255,255,255,.14);
      padding:8px 10px;
      border-radius: 999px;
      backdrop-filter: blur(8px);
      white-space:nowrap;
    }
    .meta-dot{
      width:7px; height:7px; border-radius:999px;
      background: rgba(255,255,255,.9);
      opacity:.8;
    }

    .center-link{
      margin: 16px 0 10px;
      display:flex; align-items:center; justify-content:center;
      color: rgba(15, 23, 42, .75);
      font-weight:900;
      gap:10px;
    }
    .center-link .bar{ width:2px; height:16px; background: rgba(15, 23, 42, .25); border-radius:2px; }
    .center-link .arrow{
      width:28px; height:28px; border-radius:999px;
      display:grid; place-items:center;
      background: rgba(45,108,223,.12);
      border:1px solid rgba(45,108,223,.18);
      color: var(--accent2);
      font-weight:1000;
    }

    /* ===== Board top controls ===== */
    .board-header{
      margin-top: 10px;
      background: var(--surface);
      border:1px solid var(--line);
      border-radius: var(--r20);
      padding:14px;
      display:flex; align-items:center; gap:14px;
      box-shadow: var(--shadow2);
    }
    .board-header__title{
      font-size:18px; font-weight:1000;
      margin-right:auto;
    }
    .controls{
      display:flex; align-items:center; gap:10px;
      flex-wrap:wrap;
    }
    .select, .btn{
      height:36px;
      border-radius: 12px;
      border:1px solid var(--line);
      background: #fff;
      padding:0 12px;
      font-weight:800;
      color: #0f172a;
      font-size:13px;
      outline:none;
    }
    .btn{
      cursor:pointer;
      background: linear-gradient(180deg, #2d6cdf, #1f57d6);
      border-color: rgba(31,87,214,.25);
      color:#fff;
      padding:0 14px;
      box-shadow: 0 10px 20px rgba(45,108,223,.18);
    }
    .btn.secondary{
      background:#334155;
      border-color: rgba(51,65,85,.2);
      box-shadow: 0 10px 20px rgba(15,23,42,.14);
    }

    /* ===== 3 columns ===== */
    .grid3{
      margin-top:14px;
      display:grid;
      grid-template-columns: 1fr 1fr 1fr;
      gap:14px;
    }
    .panel{
      background:#fff;
      border:1px solid var(--line);
      border-radius: var(--r20);
      box-shadow: var(--shadow2);
      overflow:hidden;
    }
    .panel__head{
      padding:14px 16px;
      border-bottom: 1px solid var(--line);
      display:flex; align-items:center; justify-content:space-between;
      gap:10px;
    }
    .panel__title{
      margin:0;
      font-size:17px;
      font-weight:1000;
    }
    .panel__body{ padding: 10px 12px 14px; }

    /* Lists */
    .list{
      display:flex;
      flex-direction:column;
      gap:10px;
      margin:0;
      padding:0;
      list-style:none;
    }
    .item{
      display:flex;
      align-items:center;
      gap:10px;
      padding:10px 10px;
      border-radius: 14px;
      border:1px solid transparent;
      transition:.12s ease;
    }
    .item:hover{
      background: var(--muted);
      border-color: rgba(45,108,223,.10);
    }

    .hot{
      width:22px; height:22px;
      border-radius: 8px;
      display:grid; place-items:center;
      background: rgba(245, 158, 11, .16);
      border:1px solid rgba(245, 158, 11, .25);
      font-size:14px;
    }
    .avatar{
      width:28px; height:28px; border-radius:999px;
      background: linear-gradient(180deg, rgba(45,108,223,.25), rgba(15, 23, 42, .15));
      border:1px solid rgba(45,108,223,.18);
      display:grid; place-items:center;
      font-size:12px;
      font-weight:1000;
      color: var(--accent2);
      flex: 0 0 auto;
      overflow:hidden;
    }
    /* TODO: 실제 유저 프로필 이미지를 쓰려면 img를 넣고 아래 스타일로 맞추세요 */
    .avatar img{ width:100%; height:100%; object-fit:cover; display:block; }

    .item__text{
      flex:1;
      min-width:0;
    }
    .item__text .title{
      font-weight:900;
      font-size:13px;
      white-space:nowrap;
      overflow:hidden;
      text-overflow:ellipsis;
    }
    .item__text .meta{
      margin-top:3px;
      font-size:12px;
      color: var(--sub);
      font-weight:700;
      display:flex; gap:8px; flex-wrap:wrap;
    }
    .count{
      flex:0 0 auto;
      display:flex; align-items:center; gap:6px;
      color: var(--sub);
      font-weight:900;
      font-size:12px;
      background: #f6f8fd;
      border:1px solid var(--line);
      padding:6px 10px;
      border-radius: 999px;
      white-space:nowrap;
    }
    .lock{
      width:10px; height:10px;
      border-radius:3px;
      background: rgba(100,116,139,.35);
      display:inline-block;
    }

    /* Notice / Tabs */
    .tabs{
      display:flex; gap:6px;
      background: #f6f8fd;
      border:1px solid var(--line);
      padding:6px;
      border-radius: 999px;
    }
    .tab{
      border:0; cursor:pointer;
      background: transparent;
      padding:7px 10px;
      border-radius: 999px;
      font-weight:1000;
      color: #475569;
      font-size:12px;
    }
    .tab.is-active{
      background: #fff;
      color: #0f172a;
      border:1px solid rgba(15, 23, 42, .08);
      box-shadow: 0 10px 20px rgba(2, 18, 52, .08);
    }

    .notice{
      display:flex; flex-direction:column; gap:10px;
      padding: 10px 14px 14px;
    }
    .notice-row{
      display:flex; align-items:flex-start; gap:10px;
      padding:10px 10px;
      border-radius: 14px;
      transition:.12s ease;
      border:1px solid transparent;
    }
    .notice-row:hover{
      background: var(--muted);
      border-color: rgba(45,108,223,.10);
    }
    .bullet{
      width:6px; height:6px; border-radius:999px;
      background: rgba(15, 23, 42, .55);
      margin-top:7px;
      flex: 0 0 auto;
    }
    .notice-title{
      font-weight:900;
      font-size:13px;
      flex:1;
      min-width:0;
      white-space:nowrap;
      overflow:hidden;
      text-overflow:ellipsis;
    }
    .date{
      color: var(--sub);
      font-weight:800;
      font-size:12px;
      white-space:nowrap;
      margin-left:10px;
    }

    /* ===== Footer ===== */
    footer{
      margin-top: 28px;
      padding: 22px 0 30px;
      background: linear-gradient(180deg, rgba(9, 26, 63, .92), rgba(3, 12, 38, .96));
      color: rgba(255,255,255,.82);
      border-top: 1px solid rgba(255,255,255,.10);
    }
    .foot{
      display:flex; gap:18px; align-items:flex-start; justify-content:space-between;
      flex-wrap:wrap;
    }
    .foot .brand{ margin-bottom:10px; }
    .foot-links{
      display:flex; gap:14px; flex-wrap:wrap;
      font-weight:800;
      color: rgba(255,255,255,.86);
      font-size:13px;
    }
    .foot small{
      display:block;
      margin-top:8px;
      color: rgba(255,255,255,.70);
      font-size:12px;
      line-height:1.55;
      font-weight:600;
      max-width: 760px;
    }
    .foot-right{
      margin-left:auto;
      color: rgba(255,255,255,.70);
      font-size:12px;
      font-weight:700;
      text-align:right;
      min-width: 220px;
    }

    /* ===== Responsive ===== */
    @media (max-width: 980px){
      .nav{ display:none; }
      .search__box{ width: 240px; }
      .games{ grid-template-columns: 1fr; }
      .grid3{ grid-template-columns: 1fr; }
      .foot-right{ text-align:left; margin-left:0; }
    }
    
    .featured-slider{
      position:relative;
      margin-top:10px;
      padding:0 56px;
    }

    .featured-slider__viewport{
      overflow:hidden;
      border-radius:20px;
      padding:10px 0;
    }

    .featured-slider__track{
      display:flex;
      transition:transform .35s ease;
      will-change:transform;
    }

    .featured-slide{
      flex:0 0 calc(100% / 3);
      min-width:calc(100% / 3);
      padding:8px;
    }

    .featured-slider__btn{
      position:absolute;
      top:50%;
      transform:translateY(-50%);
      width:42px;
      height:42px;
      border:1px solid rgba(37, 99, 235, .18);
      border-radius:999px;
      background:#fff;
      color:#1d4ed8;
      font-size:22px;
      font-weight:1000;
      box-shadow:0 10px 24px rgba(15, 23, 42, .12);
      cursor:pointer;
      z-index:2;
    }

    .featured-slider__btn:hover{
      background:#eff6ff;
    }

    .featured-slider__btn:disabled{
      opacity:.4;
      cursor:default;
      box-shadow:none;
    }

    .featured-slider__btn--prev{ left:0; }
    .featured-slider__btn--next{ right:0; }

		.trend-card{
		  display:block;
		  min-height:180px;
		  padding:24px;
		  border-radius:20px;
		  color:#fff;
		  position:relative;
		  overflow:hidden;
		  box-shadow:0 18px 40px rgba(37, 99, 235, .25);
		
		  background-size:cover;
		  background-position:center;
		  background-repeat:no-repeat;
		}

		.trend-card::before{
		  content:"";
		  position:absolute;
		  inset:auto -30px -30px auto;
		  width:180px;
		  height:180px;
		  border-radius:999px;
		  background:rgba(255,255,255,.08);
		}

    .game-submeta{
      margin-top:8px;
      color:rgba(255,255,255,.88);
      font-size:13px;
      font-weight:800;
    }

    .featured-empty{
      background:#fff;
      border:1px solid #e5e7eb;
      border-radius:20px;
      padding:40px 20px;
      text-align:center;
      font-weight:800;
      color:#64748b;
      box-shadow:0 10px 25px rgba(15, 23, 42, .06);
    }

    .chart-popup{
      position:fixed;
      inset:0;
      z-index:999;
      display:none;
    }

    .chart-popup:target{
      display:block;
    }

    .chart-popup__dim{
      position:absolute;
      inset:0;
      background:rgba(2, 6, 23, .58);
      backdrop-filter:blur(4px);
    }

    .chart-popup__panel{
      position:relative;
      width:min(920px, calc(100% - 32px));
      margin:70px auto;
      background:#fff;
      border-radius:24px;
      box-shadow:0 24px 60px rgba(2, 18, 52, .25);
      border:1px solid #e5e7eb;
      overflow:hidden;
    }

    .chart-popup__head{
      display:flex;
      align-items:flex-start;
      justify-content:space-between;
      gap:16px;
      padding:22px 24px 18px;
      border-bottom:1px solid #e5e7eb;
      background:linear-gradient(180deg, #f8fbff, #eef4fd);
    }

    .chart-popup__head h3{
      margin:0;
      font-size:24px;
      font-weight:1000;
    }

    .chart-popup__head p{
      margin:6px 0 0;
      color:#64748b;
      font-size:13px;
      font-weight:700;
    }

    .chart-popup__close{
      width:38px;
      height:38px;
      border-radius:999px;
      display:grid;
      place-items:center;
      font-size:18px;
      font-weight:1000;
      background:#fff;
      border:1px solid #e5e7eb;
      text-decoration:none;
      color:#0f172a;
    }

    .chart-popup__body{
      padding:18px 24px 24px;
      max-height:70vh;
      overflow:auto;
    }

    .chart-table{
      width:100%;
      border-collapse:collapse;
      font-size:14px;
      background:#fff;
    }

    .chart-table thead th{
      position:sticky;
      top:0;
      background:#f8fafc;
      color:#0f172a;
      padding:14px 12px;
      text-align:center;
      border-bottom:1px solid #e5e7eb;
      font-size:13px;
      font-weight:1000;
    }

    .chart-table tbody td{
      padding:14px 12px;
      border-bottom:1px solid #eef2f7;
      text-align:center;
      font-weight:700;
      color:#334155;
    }

    .chart-table tbody tr:hover{
      background:#f8fbff;
    }

    .chart-table .rank-col{
      font-weight:1000;
      color:#2563eb;
    }

    .chart-table .name-col{
      text-align:left;
      font-weight:900;
      color:#0f172a;
    }

    .chart-table .name-col a{
      color:#0f172a;
      text-decoration:none;
    }

    .chart-table .name-col a:hover{
      text-decoration:underline;
    }

    @media (max-width: 900px){
      .featured-slider{ padding:0 48px; }
      .featured-slide{
        flex:0 0 50%;
        min-width:50%;
      }
    }

    @media (max-width: 640px){
      .featured-slider{ padding:0 42px; }
      .featured-slide{
        flex:0 0 100%;
        min-width:100%;
      }
      .featured-slider__btn{
        width:36px;
        height:36px;
        font-size:18px;
      }
    }

  </style>
</head>

<body>
 <c:set var="center" value="${requestScope.center}" />
 <c:if test="${empty requestScope.isAdmin}">
   <c:set var="isAdmin" value="${sessionScope.isAdmin or sessionScope.loginRole eq 'ADMIN'}" scope="request"/>
 </c:if>
 
 
  <c:if test="${empty center}">
    <c:set var="center" value="Center.jsp"/>
  </c:if>

  <jsp:include page="Top.jsp"/>
    <div class="main-content">
        <jsp:include page="${center}"/>
    </div>
  <jsp:include page="Bottom.jsp"/>

</body>
</html>