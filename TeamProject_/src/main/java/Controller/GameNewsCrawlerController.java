package Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@WebServlet("/crawl/latest")
public class GameNewsCrawlerController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36";

    private static final Pattern ISO_DATE_PATTERN =
            Pattern.compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?Z)");
    private static final Pattern DATE_DOT_PATTERN =
            Pattern.compile("(20\\d{2}\\.\\d{2}\\.\\d{2})");
    private static final Pattern DATE_DASH_PATTERN =
            Pattern.compile("(20\\d{2}-\\d{2}-\\d{2})");
    private static final Pattern RELATIVE_DATE_PATTERN =
            Pattern.compile("(\\d+\\s*(?:분|시간|일)\\s*전)");
    private static final Pattern FC_UPDATE_DATE_PREFIX_PATTERN =
            Pattern.compile("^(20\\d{2})\\s*(\\d{2}\\.\\d{2})\\s+(.+)$");
    private static final Pattern AION_JSON_ITEM_PATTERN =
            Pattern.compile("\\\"title\\\"\\s*:\\s*\\\"([^\\\"]+)\\\".*?"
                    + "\\\"(?:createDate|createdDate|registerDate|regDate|date)\\\"\\s*:\\s*\\\"([^\\\"]+)\\\".*?"
                    + "(?:\\\"linkUrl\\\"|\\\"url\\\"|\\\"path\\\")\\s*:\\s*\\\"([^\\\"]+)\\\"",
                    Pattern.DOTALL);
    private static final Pattern AION_TEXT_ITEM_PATTERN =
            Pattern.compile("(\\[안내\\]\\s*.+?)\\s*[·•]\\s*(20\\d{2}-\\d{2}-\\d{2})");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    private void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        String game = safe(req.getParameter("game"));
        String type = safe(req.getParameter("type"));

        JSONObject result = new JSONObject();
        result.put("game", game);
        result.put("type", type);

        List<NewsItem> items = new ArrayList<NewsItem>();
        String sourceUrl = "";
        boolean ok = false;

        try {
            sourceUrl = resolveSourceUrl(game, type);
            if (!isBlank(sourceUrl)) {
                items = crawlByGame(game, type, sourceUrl);
                ok = true;
            }
        } catch (Exception e) {
            result.put("error", e.toString());
            ok = false;
        }

        result.put("ok", ok);
        result.put("sourceUrl", sourceUrl);

        JSONArray arr = new JSONArray();
        for (NewsItem item : items) {
            JSONObject o = new JSONObject();
            o.put("title", item.title);
            o.put("date", item.date);
            o.put("summary", item.summary);
            o.put("url", item.url);
            arr.add(o);
        }
        result.put("items", arr);

        PrintWriter out = resp.getWriter();
        out.print(result.toJSONString());
        out.flush();
    }

    private String resolveSourceUrl(String game, String type) {
        if ("news".equals(type)) {
            String query = resolveNewsQuery(game);
            if (isBlank(query)) {
                return "";
            }

            try {
                return "https://search.naver.com/search.naver?ssc=tab.news.all&where=news&sm=tab_jum&query="
                        + URLEncoder.encode(query, "UTF-8");
            } catch (Exception e) {
                return "";
            }
        }

        if ("lol".equals(game)) {
            if ("notice".equals(type)) return "https://www.leagueoflegends.com/ko-kr/news/notices/";
            if ("patch".equals(type)) return "https://www.leagueoflegends.com/ko-kr/news/game-updates/";
        }
        if ("battleground".equals(game)) {
            if ("notice".equals(type)) return "https://www.pubg.com/ko/news?category=notice";
            if ("patch".equals(type)) return "https://www.pubg.com/ko/news?category=patch_notes";
        }
        if ("valorant".equals(game)) {
            if ("notice".equals(type)) return "https://playvalorant.com/ko-kr/news/announcements/";
            if ("patch".equals(type)) return "https://playvalorant.com/ko-kr/news/game-updates/";
        }
        if ("suddenattack".equals(game)) {
            if ("notice".equals(type)) return "https://sa.nexon.com/news/notice/list.aspx";
            if ("patch".equals(type)) return "https://sa.nexon.com/news/update/list.aspx";
        }
        if ("aion2".equals(game)) {
            if ("notice".equals(type)) return "https://aion2.plaync.com/ko-kr/board/notice/list";
            if ("patch".equals(type)) return "https://aion2.plaync.com/ko-kr/board/update/list";
        }
        if ("fconline".equals(game)) {
            if ("notice".equals(type)) return "https://fconline.nexon.com/news/notice/list";
            if ("patch".equals(type)) return "https://fconline.nexon.com/news/update/list";
        }
        if ("maplestory".equals(game)) {
            if ("notice".equals(type)) return "https://maplestory.nexon.com/News/Notice";
            if ("patch".equals(type)) return "https://maplestory.nexon.com/News/Update";
        }
        if ("dnf".equals(game)) {
            if ("notice".equals(type)) return "https://df.nexon.com/community/news/notice/list";
            if ("patch".equals(type)) return "https://df.nexon.com/community/news/update/list";
        }
        if ("lostark".equals(game)) {
            if ("notice".equals(type)) return "https://lostark.game.onstove.com/News/Notice/List";
            if ("patch".equals(type)) {
                return "https://lostark.game.onstove.com/News/Notice/List?page=1&searchtype=2&searchtext=%EC%97%85%EB%8D%B0%EC%9D%B4%ED%8A%B8&noticetype=all";
            }
        }
        return "";
    }

    private String resolveNewsQuery(String game) {
        if ("lol".equals(game)) return "리그 오브 레전드";
        if ("battleground".equals(game)) return "배틀그라운드";
        if ("valorant".equals(game)) return "발로란트";
        if ("suddenattack".equals(game)) return "서든어택";
        if ("aion2".equals(game)) return "아이온2";
        if ("fconline".equals(game)) return "FC 온라인";
        if ("maplestory".equals(game)) return "메이플스토리";
        if ("dnf".equals(game)) return "던전앤파이터";
        if ("lostark".equals(game)) return "로스트아크";
        return "";
    }

    private List<NewsItem> crawlByGame(String game, String type, String sourceUrl) throws IOException {
        if ("news".equals(type)) {
            String query = resolveNewsQuery(game);
            return crawlNaverNews(sourceUrl, query);
        }

        if ("lol".equals(game)) {
            return crawlLol(sourceUrl);
        }
        if ("battleground".equals(game)) {
            return crawlPubgTextOnly(sourceUrl, "patch".equals(type) ? "패치노트" : "공지");
        }
        if ("valorant".equals(game)) {
            return crawlValorant(sourceUrl,
                    "notice".equals(type) ? "/ko-kr/news/announcements/" : "/ko-kr/news/game-updates/");
        }
        if ("suddenattack".equals(game)) {
            return crawlSuddenAttack(sourceUrl);
        }
        if ("fconline".equals(game)) {
            return crawlFcOnline(sourceUrl, type);
        }
        if ("maplestory".equals(game)) {
            return crawlMapleStory(sourceUrl);
        }
        if ("aion2".equals(game)) {
            return crawlAion2(sourceUrl);
        }
        if ("dnf".equals(game)) {
            return crawlDnf(sourceUrl, type);
        }
        if ("lostark".equals(game)) {
            if ("patch".equals(type)) {
                return crawlLostArkPatch(sourceUrl);
            }
            return crawlLostArk(sourceUrl, type);
        }
        return new ArrayList<NewsItem>();
    }

    private List<NewsItem> crawlNaverNews(String url, String query) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);
        Set<String> seen = new LinkedHashSet<String>();

        Elements newsBlocks = doc.select(
                "div.news_wrap, " +
                "div.news_area, " +
                "div.group_news li.bx, " +
                "ul.list_news > li, " +
                "div[class*=news_wrap], " +
                "div[class*=news_area]"
        );

        for (Element block : newsBlocks) {
            Element titleLink = findNewsTitleLink(block);
            if (titleLink == null) continue;

            String href = normalizeNewsHref(titleLink.absUrl("href"));
            if (!isRealNewsResultLink(href)) continue;
            if (!seen.add(href)) continue;

            String title = cleanTitleNoise(firstNonBlank(
                    titleLink.attr("title"),
                    titleLink.text(),
                    titleLink.attr("aria-label")
            ));
            if (isBlank(title) || title.length() < 4) continue;
            if (isFixedNoiseTitle(title)) continue;

            String date = firstNonBlank(
                    extractNewsDate(block),
                    extractDateAround(titleLink),
                    extractDateAround(block)
            );

            String summary = firstNonBlank(
                    firstText(block, ".news_dsc"),
                    firstText(block, ".dsc_wrap"),
                    firstText(block, ".api_txt_lines"),
                    firstText(block, ".news_desc"),
                    firstText(block, ".news_contents .dsc_txt_wrap"),
                    extractSummaryAround(block)
            );

            addItem(items, title, date, summary, href, 10);
            if (items.size() >= 10) {
                return items;
            }
        }

        Elements links = doc.select("a[href]");
        for (Element a : links) {
            String href = normalizeNewsHref(a.absUrl("href"));
            if (!isRealNewsResultLink(href)) continue;
            if (!seen.add(href)) continue;

            String title = cleanTitleNoise(firstNonBlank(
                    a.attr("title"),
                    a.text(),
                    a.attr("aria-label")
            ));
            if (isBlank(title) || title.length() < 8) continue;
            if (isFixedNoiseTitle(title)) continue;

            Element block = findNewsContainer(a);

            String date = firstNonBlank(
                    extractNewsDate(block),
                    extractDateAround(a),
                    extractDateAround(block)
            );

            String summary = firstNonBlank(
                    firstText(block, ".news_dsc"),
                    firstText(block, ".dsc_wrap"),
                    firstText(block, ".api_txt_lines"),
                    firstText(block, ".news_desc"),
                    firstText(block, ".news_contents .dsc_txt_wrap"),
                    extractSummaryAround(block)
            );

            if (isBlank(date) && isBlank(summary) && !containsLooseKeyword(title, query)) {
                continue;
            }

            addItem(items, title, date, summary, href, 10);
            if (items.size() >= 10) {
                break;
            }
        }

        return items;
    }

    private Element findNewsTitleLink(Element block) {
        if (block == null) return null;

        Element found = firstExisting(block,
                "a.news_tit",
                "a.news_title",
                "a.api_txt_lines.total_tit",
                "a.api_txt_lines",
                "a[href*='news.naver.com']",
                "a[href*='n.news.naver.com']",
                "a[href*='oid='][href*='aid=']"
        );
        if (found != null) return found;

        Elements links = block.select("a[href]");
        for (Element a : links) {
            String href = normalizeNewsHref(a.absUrl("href"));
            String title = cleanText(firstNonBlank(a.attr("title"), a.text()));
            if (!isRealNewsResultLink(href)) continue;
            if (isBlank(title) || title.length() < 8) continue;
            if (isFixedNoiseTitle(title)) continue;
            return a;
        }
        return null;
    }

    private String normalizeNewsHref(String href) {
        if (isBlank(href)) return "";

        href = href.trim();
        try {
            int idx = href.indexOf("url=");
            if (idx >= 0) {
                String encoded = href.substring(idx + 4);
                int amp = encoded.indexOf('&');
                if (amp >= 0) {
                    encoded = encoded.substring(0, amp);
                }
                return URLDecoder.decode(encoded, "UTF-8");
            }
        } catch (Exception ignore) {
        }
        return href;
    }

    private Element findNewsContainer(Element base) {
        Element cur = base;
        int guard = 0;

        while (cur != null && guard < 6) {
            String cls = cleanText(cur.className());
            if (cur.hasClass("news_wrap")
                    || cur.hasClass("news_area")
                    || cls.contains("news_wrap")
                    || cls.contains("news_area")
                    || "li".equalsIgnoreCase(cur.tagName())
                    || "article".equalsIgnoreCase(cur.tagName())) {
                return cur;
            }
            cur = cur.parent();
            guard++;
        }

        return base != null ? base.parent() : null;
    }

    private boolean isRealNewsResultLink(String href) {
        if (isBlank(href)) return false;

        String lower = href.toLowerCase(Locale.ROOT);

        if (lower.contains("search.naver.com")) return false;
        if (lower.contains("dict.naver.com")) return false;
        if (lower.contains("terms.naver.com")) return false;
        if (lower.contains("academic.naver.com")) return false;
        if (lower.contains("keep.naver.com")) return false;
        if (lower.contains("shopping.naver.com")) return false;
        if (lower.contains("blog.naver.com")) return false;
        if (lower.contains("cafe.naver.com")) return false;
        if (lower.contains("kin.naver.com")) return false;
        if (lower.contains("/sports")) return false;
        if (lower.contains("/entertain")) return false;

        if (lower.contains("news.naver.com")) return true;
        if (lower.contains("n.news.naver.com")) return true;
        if (lower.contains("oid=") && lower.contains("aid=")) return true;

        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            if (lower.contains("naver.com")) return false;
            return true;
        }

        return false;
    }

    private boolean isFixedNoiseTitle(String title) {
        if (isBlank(title)) return true;

        String t = title.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);

        if (t.equals("naver")) return true;
        if (t.contains("어학사전")) return true;
        if (t.contains("지식백과")) return true;
        if (t.contains("학술정보")) return true;
        if (t.contains("옵션가이드")) return true;
        if (t.contains("네이버뉴스")) return true;
        if (t.contains("keep에바로가기")) return true;
        if (t.contains("언론사선정")) return true;

        return false;
    }

    private String extractNewsDate(Element block) {
        if (block == null) return "";

        Elements infos = block.select(".info_group span.info, .news_info .info, span.info");
        for (Element info : infos) {
            String text = cleanText(info.text());
            if (isBlank(text)) continue;

            if (text.matches(".*\\d{4}\\.\\d{2}\\.\\d{2}.*")) return text;
            if (text.matches(".*\\d{4}-\\d{2}-\\d{2}.*")) return text;
            if (text.matches(".*\\d+일\\s*전.*")) return text;
            if (text.matches(".*\\d+시간\\s*전.*")) return text;
            if (text.matches(".*\\d+분\\s*전.*")) return text;
        }

        return "";
    }

    private Element firstExisting(Element base, String... selectors) {
        if (base == null || selectors == null) return null;

        for (String selector : selectors) {
            if (isBlank(selector)) continue;
            Element found = base.selectFirst(selector);
            if (found != null) return found;
        }
        return null;
    }

    private String extractSummaryAround(Element base) {
        if (base == null) return "";

        String summary = firstNonBlank(
                firstText(base, ".news_dsc"),
                firstText(base, ".dsc_wrap"),
                firstText(base, ".api_txt_lines"),
                firstText(base, ".news_desc"),
                firstText(base, ".news_contents .dsc_txt_wrap")
        );

        if (!isBlank(summary)) {
            return cleanText(summary);
        }

        String text = cleanText(base.text());
        if (isBlank(text)) return "";

        if (text.length() > 140) {
            return text.substring(0, 140) + "...";
        }
        return text;
    }

    private boolean containsLooseKeyword(String title, String query) {
        if (isBlank(title) || isBlank(query)) return false;

        String normalizedTitle = title.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
        String normalizedQuery = query.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);

        if (normalizedTitle.contains(normalizedQuery)) {
            return true;
        }

        String[] parts = query.split("\\s+");
        for (String part : parts) {
            String token = part.trim().toLowerCase(Locale.ROOT);
            if (token.length() < 2) continue;
            if (normalizedTitle.contains(token)) {
                return true;
            }
        }

        if ("리그 오브 레전드".equals(query)) {
            return normalizedTitle.contains("롤")
                    || normalizedTitle.contains("lol")
                    || normalizedTitle.contains("lck")
                    || normalizedTitle.contains("리그오브레전드");
        }
        if ("로스트아크".equals(query)) {
            return normalizedTitle.contains("로스트아크")
                    || normalizedTitle.contains("로아");
        }
        if ("던전앤파이터".equals(query)) {
            return normalizedTitle.contains("던전앤파이터")
                    || normalizedTitle.contains("던파");
        }
        if ("배틀그라운드".equals(query)) {
            return normalizedTitle.contains("배틀그라운드")
                    || normalizedTitle.contains("배그")
                    || normalizedTitle.contains("pubg");
        }

        return false;
    }

    private List<NewsItem> crawlLol(String url) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);

        Elements cards = doc.select("a[href]");
        Set<String> seen = new LinkedHashSet<String>();

        for (Element a : cards) {
            String href = a.absUrl("href");
            if (isBlank(href) || !href.contains("/news/")) {
                continue;
            }
            if (!seen.add(href)) {
                continue;
            }

            String title = firstText(a,
                    "h2, h3, [data-testid='card-title'], .sc-6c7e286c-3, .sc-6c7e286c-2");
            String date = firstText(a,
                    "time, .sc-6c7e286c-6, .sc-6c7e286c-7, .sc-6c7e286c-8");
            String summary = firstText(a,
                    "p, .sc-6c7e286c-4, .sc-6c7e286c-5");

            if (isBlank(title)) {
                title = cleanText(a.text());
                if (title.length() > 80) {
                    title = title.substring(0, 80) + "...";
                }
            }
            if (isBlank(title)) {
                continue;
            }

            items.add(new NewsItem(title, date, summary, href));
            if (items.size() >= 10) {
                break;
            }
        }
        return items;
    }

    private List<NewsItem> crawlValorant(String listUrl, String expectedPathPrefix) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document listDoc = connect(listUrl);
        Elements links = listDoc.select("a[href]");
        Set<String> seen = new LinkedHashSet<String>();

        for (Element link : links) {
            String href = normalizeValorantUrl(link.absUrl("href"));
            if (isBlank(href)) {
                continue;
            }
            if (!href.startsWith("https://playvalorant.com" + expectedPathPrefix)) {
                continue;
            }
            if (href.equals(normalizeValorantUrl(listUrl))) {
                continue;
            }
            if (!seen.add(href)) {
                continue;
            }

            NewsItem item = crawlValorantDetail(href);
            if (item != null && !isBlank(item.title)) {
                items.add(item);
            }
            if (items.size() >= 10) {
                break;
            }
        }

        return items;
    }

    private NewsItem crawlValorantDetail(String url) {
        try {
            Document doc = connect(url);
            String title = firstNonBlank(
                    firstText(doc, "h1"),
                    metaContent(doc, "meta[property=og:title]"),
                    metaContent(doc, "meta[name=twitter:title]"),
                    extractTitleFromDocumentText(doc.text())
            );

            String summary = firstNonBlank(
                    metaContent(doc, "meta[name=description]"),
                    metaContent(doc, "meta[property=og:description]"),
                    extractLeadParagraph(doc)
            );

            String rawDate = firstNonBlank(
                    firstText(doc, "time"),
                    extractIsoDate(doc.text())
            );
            String date = formatIsoDate(rawDate);

            if (isBlank(title)) {
                return null;
            }
            return new NewsItem(title, date, summary, url);
        } catch (Exception e) {
            return null;
        }
    }

    private List<NewsItem> crawlPubgTextOnly(String url, String categoryLabel) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);

        String text = doc.body() != null ? doc.body().text() : doc.text();
        text = cleanText(text);

        int newsIndex = text.indexOf("# 뉴스");
        if (newsIndex >= 0) {
            text = text.substring(newsIndex);
        } else {
            newsIndex = text.indexOf("뉴스");
            if (newsIndex >= 0) {
                text = text.substring(newsIndex);
            }
        }

        int moreIndex = text.indexOf("더 보기");
        if (moreIndex > 0) {
            text = text.substring(0, moreIndex);
        }

        Pattern pattern;
        if ("패치노트".equals(categoryLabel)) {
            pattern = Pattern.compile("(패치 노트\\s*-\\s*업데이트[^0-9]*[0-9]+(?:\\.[0-9]+)?)\\s*(.*?)\\s*패치노트\\s*(\\d{4}\\.\\d{2}\\.\\d{2})");
        } else {
            pattern = Pattern.compile("((?:(?!공지\\s*\\d{4}\\.\\d{2}\\.\\d{2}).){2,120}?)\\s*공지\\s*(\\d{4}\\.\\d{2}\\.\\d{2})");
        }

        Matcher m = pattern.matcher(text);
        Set<String> seenTitles = new LinkedHashSet<String>();

        while (m.find() && items.size() < 10) {
            String title;
            String summary = "";
            String date;
            if ("패치노트".equals(categoryLabel)) {
                title = cleanText(m.group(1));
                summary = cleanText(m.group(2));
                date = cleanText(m.group(3));
            } else {
                title = cleanText(m.group(1));
                date = cleanText(m.group(2));
                title = removePubgPrefixNoise(title);
                if (title.contains("콘솔")) {
                    title = title.replaceFirst("^(PC\\s*)?(콘솔\\s*)+", "").trim();
                }
                if (title.length() > 120) {
                    title = title.substring(title.length() - 120).trim();
                }
                int idxPc = Math.max(title.lastIndexOf("PC "), title.lastIndexOf("콘솔 "));
                if (idxPc >= 0 && idxPc + 3 < title.length()) {
                    title = title.substring(idxPc + 3).trim();
                }
            }

            title = collapseCategoryNoise(title);
            if (isBlank(title) || !seenTitles.add(title)) {
                continue;
            }
            items.add(new NewsItem(title, date, summary, url));
        }

        if (items.isEmpty() && "공지".equals(categoryLabel)) {
            Pattern datePattern = Pattern.compile("(\\d{4}\\.\\d{2}\\.\\d{2})");
            Matcher dm = datePattern.matcher(text);
            List<Integer> positions = new ArrayList<Integer>();
            List<String> dates = new ArrayList<String>();
            while (dm.find()) {
                positions.add(dm.start());
                dates.add(dm.group(1));
            }
            for (int i = 0; i < positions.size() && items.size() < 10; i++) {
                int start = Math.max(0, positions.get(i) - 160);
                String segment = text.substring(start, positions.get(i));
                String title = segment.replaceAll(".*제목\\+본문", "")
                                     .replaceAll(".*?(전체|PC|콘솔|이스포츠)\\s+", "")
                                     .replaceAll("공지\\s*$", "")
                                     .trim();
                title = removePubgPrefixNoise(title);
                title = collapseCategoryNoise(title);
                if (!isBlank(title) && seenTitles.add(title)) {
                    items.add(new NewsItem(title, dates.get(i), "", url));
                }
            }
        }

        return items;
    }

    private List<NewsItem> crawlSuddenAttack(String url) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);

        for (Element a : doc.select("a[href]")) {
            String href = a.absUrl("href");
            String title = cleanBracketPrefix(cleanText(a.text()));
            if (isBlank(title) || title.length() < 4) continue;
            if (!href.contains("/news/") || href.contains("/list.aspx")) continue;
            if (title.equals("공지사항") || title.equals("업데이트")) continue;
            addItem(items, title, extractDateAround(a), "", href, 10);
        }

        if (!items.isEmpty()) {
            return items;
        }

        String text = cleanText(doc.text());
        Matcher m = Pattern.compile("\\d+\\s+(.+?)\\s+(20\\d{2}\\.\\d{2}\\.\\d{2})\\s+[0-9,]+")
                .matcher(text);
        while (m.find() && items.size() < 10) {
            addItem(items, cleanBracketPrefix(m.group(1)), m.group(2), "", url, 10);
        }
        return items;
    }

    private List<NewsItem> crawlFcOnline(String url, String type) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);

        for (Element a : doc.select("a[href]")) {
            String href = a.absUrl("href");
            String text = cleanText(a.text());
            if (isBlank(text) || text.length() < 6) continue;

            if ("notice".equals(type)) {
                if (!(href.contains("/news/notice/") || href.contains("fconline.nexon.com") || href.contains("events.fconline.nexon.com"))) {
                    continue;
                }
                if (text.startsWith("공지 ") || text.startsWith("점검 ") || text.startsWith("업데이트 ") || text.startsWith("이벤트 ")) {
                    String title = text.replaceFirst("^(공지|점검|업데이트|이벤트)\\s+", "");
                    String date = extractRelativeDate(text);
                    title = title.replaceFirst("\\s+(\\d+시간 전|\\d+일 전|\\d+분 전).*$", "").trim();
                    addItem(items, title, date, "", href, 10);
                }
            } else {
                Matcher datePrefix = FC_UPDATE_DATE_PREFIX_PATTERN.matcher(text);
                if (datePrefix.find()) {
                    String date = datePrefix.group(1) + "." + datePrefix.group(2).replace('.', '.');
                    String title = cleanText(datePrefix.group(3));
                    addItem(items, title, date, "", href, 10);
                }
            }
        }
        return items;
    }

    private List<NewsItem> crawlMapleStory(String url) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);

        java.util.LinkedHashMap<String, NewsItem> merged = new java.util.LinkedHashMap<String, NewsItem>();

        Elements links = doc.select(
                "a[href*='/News/Notice/'], " +
                "a[href*='/News/Update/']"
        );

        for (Element a : links) {
            String href = a.absUrl("href");
            if (isBlank(href) || href.equals(url)) continue;

            String text = cleanText(a.text());
            if (isBlank(text)) continue;
            if (isMapleIgnoreText(text)) continue;

            NewsItem current = merged.get(href);
            if (current == null) {
                current = new NewsItem("", "", "", href);
            }

            if (isMapleDateOnlyText(text)) {
                if (isBlank(current.date)) {
                    current.date = normalizeMapleDateText(text);
                }
            } else {
                String title = removeMapleDateText(text);
                String date = extractMapleDateText(text);

                title = cleanTitleNoise(title);

                if (!isBlank(title)) {
                    if (isBlank(current.title) || current.title.length() < title.length()) {
                        current.title = title;
                    }
                }

                if (isBlank(current.date) && !isBlank(date)) {
                    current.date = date;
                }
            }

            if (isBlank(current.date)) {
                String nearDate = extractMapleDateAround(a);
                if (!isBlank(nearDate)) {
                    current.date = nearDate;
                }
            }

            merged.put(href, current);
        }

        for (NewsItem item : merged.values()) {
            if (isBlank(item.title)) continue;
            addItem(items, item.title, item.date, "", item.url, 10);
            if (items.size() >= 10) break;
        }

        if (!items.isEmpty()) {
            return items;
        }

        String text = cleanText(doc.text());
        Matcher m = Pattern.compile("(.+?)\\s+(20\\d{2}\\.\\d{2}\\.\\d{2})").matcher(text);
        while (m.find() && items.size() < 10) {
            String title = cleanTitleNoise(removeMapleDateText(m.group(1)));
            String date = m.group(2);

            if (isBlank(title) || isMapleIgnoreText(title)) continue;
            addItem(items, title, date, "", url, 10);
        }

        return items;
    }

    private boolean isMapleIgnoreText(String text) {
        String t = cleanText(text);
        if (isBlank(t)) return true;

        return t.equals("공지사항")
                || t.equals("업데이트")
                || t.equals("이벤트")
                || t.equals("캐시샵 공지")
                || t.equals("메이플 알림판")
                || t.equals("with maple")
                || t.equals("제목")
                || t.equals("전체")
                || t.equals("공지")
                || t.equals("점검")
                || t.equals("GM소식");
    }

    private boolean isMapleDateOnlyText(String text) {
        String t = cleanText(text);

        return t.matches("^20\\d{2}\\.\\d{2}\\.\\d{2}.*$")
                || t.matches("^AM\\s*\\d{1,2}:\\d{2}$")
                || t.matches("^PM\\s*\\d{1,2}:\\d{2}$")
                || t.matches("^20\\d{2}\\.\\d{2}\\.\\d{2}\\s*\\(.+?\\)\\s*~.*$");
    }

    private String extractMapleDateText(String text) {
        String t = cleanText(text);

        Matcher range = Pattern.compile("(20\\d{2}\\.\\d{2}\\.\\d{2}\\s*\\(.+?\\)\\s*~\\s*20\\d{2}\\.\\d{2}\\.\\d{2}.*)$")
                .matcher(t);
        if (range.find()) return cleanText(range.group(1));

        Matcher date = Pattern.compile("(20\\d{2}\\.\\d{2}\\.\\d{2})").matcher(t);
        if (date.find()) return cleanText(date.group(1));

        Matcher ampm = Pattern.compile("\\b(AM|PM)\\s*\\d{1,2}:\\d{2}\\b").matcher(t);
        if (ampm.find()) return cleanText(ampm.group());

        return "";
    }

    private String removeMapleDateText(String text) {
        String t = cleanText(text);

        t = t.replaceAll("20\\d{2}\\.\\d{2}\\.\\d{2}\\s*\\(.+?\\)\\s*~\\s*20\\d{2}\\.\\d{2}\\.\\d{2}.*$", "");
        t = t.replaceAll("20\\d{2}\\.\\d{2}\\.\\d{2}$", "");
        t = t.replaceAll("\\b(AM|PM)\\s*\\d{1,2}:\\d{2}\\b$", "");
        t = t.replaceAll("\\s+", " ").trim();

        return t;
    }

    private String normalizeMapleDateText(String text) {
        return cleanText(extractMapleDateText(text));
    }

    private String extractMapleDateAround(Element element) {
        if (element == null) return "";

        String own = extractMapleDateText(element.text());
        if (!isBlank(own)) return own;

        Element cur = element;
        for (int i = 0; i < 4 && cur != null; i++) {
            Element next = cur.nextElementSibling();
            if (next != null) {
                String nextDate = extractMapleDateText(next.text());
                if (!isBlank(nextDate)) return nextDate;
            }

            Element parent = cur.parent();
            if (parent != null) {
                String parentDate = extractMapleDateText(parent.text());
                if (!isBlank(parentDate)) return parentDate;
            }

            cur = parent;
        }

        return "";
    }

    private List<NewsItem> crawlAion2(String url) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);
        String html = doc.html();
        String text = cleanText(doc.text() + " " + html);

        Matcher jsonMatcher = AION_JSON_ITEM_PATTERN.matcher(html);
        while (jsonMatcher.find() && items.size() < 10) {
            String title = unescapeJson(jsonMatcher.group(1));
            String date = normalizeDashedDate(unescapeJson(jsonMatcher.group(2)));
            String link = unescapeJson(jsonMatcher.group(3));
            if (!link.startsWith("http")) {
                link = toAbsoluteUrl("https://aion2.plaync.com", link);
            }
            addItem(items, cleanTitleNoise(title), date, "", link, 10);
        }

        if (!items.isEmpty()) {
            return items;
        }

        Matcher textMatcher = AION_TEXT_ITEM_PATTERN.matcher(text);
        while (textMatcher.find() && items.size() < 10) {
            addItem(items, cleanTitleNoise(textMatcher.group(1)), textMatcher.group(2), "", url, 10);
        }
        return items;
    }

    private List<NewsItem> crawlDnf(String url, String type) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);

        boolean noticePage = "notice".equals(type) || url.contains("/community/news/notice/list");
        boolean updatePage = "patch".equals(type) || url.contains("/community/news/update/list");

        String pageText = cleanText(doc.text());
        List<NewsItem> parsed = parseDnfListText(pageText, url, noticePage, updatePage);

        for (NewsItem item : parsed) {
            addItem(items, item.title, item.date, "", item.url, 10);
            if (items.size() >= 10) {
                return items;
            }
        }

        String scopedText = extractDnfNewsSection(pageText, noticePage, updatePage);
        parsed = parseDnfListText(scopedText, url, noticePage, updatePage);

        for (NewsItem item : parsed) {
            addItem(items, item.title, item.date, "", item.url, 10);
            if (items.size() >= 10) {
                return items;
            }
        }

        return items;
    }

    private List<NewsItem> parseDnfListText(String text, String fallbackUrl, boolean noticePage, boolean updatePage) {
        List<NewsItem> items = new ArrayList<NewsItem>();
        if (isBlank(text)) return items;

        String scoped = extractDnfNewsSection(text, noticePage, updatePage);
        if (isBlank(scoped)) scoped = text;

        scoped = trimDnfListStart(scoped, noticePage, updatePage);
        if (isBlank(scoped)) return items;

        Pattern p = Pattern.compile(
                "(일반|점검|퍼스트서버|당첨자발표|주요|대규모|던파ON)\\s+(.+?)\\s+(20\\d{2}\\.\\d{2}\\.\\d{2})\\s+[0-9,]+"
        );
        Matcher m = p.matcher(scoped);

        Set<String> seen = new LinkedHashSet<String>();

        while (m.find() && items.size() < 10) {
            String category = cleanText(m.group(1));
            String title = cleanTitleNoise(cleanText(m.group(2)));
            String date = cleanText(m.group(3));

            if (isBlank(title)) continue;
            if (isDnfIgnoreTitle(title)) continue;
            if (containsEmail(title)) continue;
            if (title.length() < 4) continue;

            String dedupKey = title + "|" + date;
            if (!seen.add(dedupKey)) continue;

            items.add(new NewsItem(title, date, "", fallbackUrl));
        }

        return items;
    }

    private String trimDnfListStart(String text, boolean noticePage, boolean updatePage) {
        if (isBlank(text)) return "";

        String scoped = cleanText(text);

        String[] startMarkers = {
                "제목+본문 제목 삭제",
                "제목 삭제",
                "삭제"
        };

        for (String marker : startMarkers) {
            int idx = scoped.indexOf(marker);
            if (idx >= 0) {
                scoped = scoped.substring(idx + marker.length()).trim();
                break;
            }
        }

        Pattern firstRow = Pattern.compile(
                "(일반|점검|퍼스트서버|당첨자발표|주요|대규모|던파ON)\\s+.+?\\s+20\\d{2}\\.\\d{2}\\.\\d{2}\\s+[0-9,]+"
        );
        Matcher m = firstRow.matcher(scoped);
        if (m.find()) {
            scoped = scoped.substring(m.start()).trim();
        }

        return scoped;
    }

    private String extractDnfNewsSection(String text, boolean noticePage, boolean updatePage) {
        if (isBlank(text)) return "";

        String startToken = noticePage ? "공지사항" : "업데이트";
        int start = text.indexOf(startToken);
        if (start < 0) {
            start = text.indexOf("새소식");
        }
        if (start < 0) {
            start = 0;
        }

        int end = text.length();

        String[] endTokens = {
                "업데이트 안정화 제보",
                "제보하기 TOP",
                "회사소개",
                "E-mail :",
                "© 2005 NEOPLE",
                "보안 경고 알림"
        };

        for (String token : endTokens) {
            int idx = text.indexOf(token, start);
            if (idx >= 0 && idx < end) {
                end = idx;
            }
        }

        return cleanText(text.substring(start, end));
    }

    private boolean containsEmail(String text) {
        if (isBlank(text)) return false;
        return text.matches(".*[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}.*");
    }

    private boolean isDnfUpdateOnlyCategory(String category) {
        if (isBlank(category)) return false;
        return "퍼스트서버".equals(category)
                || "주요".equals(category)
                || "대규모".equals(category)
                || "던파ON".equals(category);
    }

    private boolean isDnfIgnoreTitle(String title) {
        if (isBlank(title)) return true;

        String raw = cleanText(title);
        if (containsEmail(raw)) return true;

        String t = raw.replaceAll("\\s+", "").toUpperCase();

        return t.equals("공지사항")
                || t.equals("업데이트")
                || t.equals("이벤트")
                || t.equals("이달의아이템")
                || t.equals("세리아의상점")
                || t.equals("오늘의던파")
                || t.equals("던파매거진")
                || t.equals("개발자노트")
                || t.equals("게임시작")
                || t.equals("로그인")
                || t.equals("넥슨ID회원가입")
                || t.equals("던파ID회원가입")
                || t.equals("제목")
                || t.equals("제목+본문")
                || t.equals("삭제")
                || t.equals("TOP")
                || t.equals("NEXT")
                || t.equals("END")
                || t.equals("PREV")
                || t.equals("EMAIL")
                || t.equals("E-MAIL")
                || t.contains("NEXON")
                || t.contains("NEOPLE")
                || t.matches("^\\d+$");
    }

    private String extractDnfDateFromParent(Element element) {
        if (element == null) return "";

        Element parent = element.parent();
        int guard = 0;

        while (parent != null && guard < 4) {
            String date = extractDateAround(parent);
            if (!isBlank(date)) {
                return date;
            }

            String ownText = cleanText(parent.ownText());
            Matcher m = DATE_DOT_PATTERN.matcher(ownText);
            if (m.find()) {
                return m.group(1);
            }

            parent = parent.parent();
            guard++;
        }

        return "";
    }

    private String extractDnfDateFromSibling(Element element) {
        if (element == null) return "";

        Element cur = element;

        for (int i = 0; i < 4 && cur != null; i++) {
            Element next = cur.nextElementSibling();
            if (next != null) {
                String text = cleanText(next.text());
                Matcher m = DATE_DOT_PATTERN.matcher(text);
                if (m.find()) {
                    return m.group(1);
                }
            }

            Element prev = cur.previousElementSibling();
            if (prev != null) {
                String text = cleanText(prev.text());
                Matcher m = DATE_DOT_PATTERN.matcher(text);
                if (m.find()) {
                    return m.group(1);
                }
            }

            cur = cur.parent();
        }

        return "";
    }

    private List<NewsItem> crawlLostArk(String url, String type) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);
        String text = cleanText(doc.text());

        if ("patch".equals(type) && text.contains("해당 게시물이 없습니다")) {
            return items;
        }

        Set<String> seen = new LinkedHashSet<String>();

        Elements scopedLinks = doc.select(
                "section a[href*=/News/Update/View], " +
                "section a[href*=/News/Notice/View], " +
                "section a[href*=/News/Notice/Views/], " +
                "article a[href*=/News/Update/View], " +
                "article a[href*=/News/Notice/View], " +
                "article a[href*=/News/Notice/Views/], " +
                "main a[href*=/News/Update/View], " +
                "main a[href*=/News/Notice/View], " +
                "main a[href*=/News/Notice/Views/], " +
                "div a[href*=/News/Update/View], " +
                "div a[href*=/News/Notice/View], " +
                "div a[href*=/News/Notice/Views/]"
        );

        for (Element a : scopedLinks) {
            String href = a.absUrl("href");
            if (isBlank(href)) continue;
            if (!seen.add(href)) continue;

            if ("patch".equals(type) && !href.contains("/News/Update/View")) continue;
            if ("notice".equals(type)
                    && !(href.contains("/News/Notice/View") || href.contains("/News/Notice/Views/"))) continue;

            String rawTitle = cleanText(a.text());
            String title = cleanLostArkTitle(rawTitle);
            if (isBlank(title) || title.length() < 4) continue;

            if (title.contains("이벤트 모아보기")
                    || title.contains("자동재생")
                    || title.contains("정지")) {
                continue;
            }

            String date = extractLostArkDate(rawTitle);
            if (isBlank(date)) {
                date = extractDateAround(a);
            }

            if (isBlank(date)) {
                Element parent = a.parent();
                int guard = 0;
                while (parent != null && guard < 3 && isBlank(date)) {
                    date = extractDateAround(parent);
                    parent = parent.parent();
                    guard++;
                }
            }

            addItem(items, title, date, "", href, 10);

            if (items.size() >= 10) {
                break;
            }
        }

        if (!items.isEmpty()) {
            return items;
        }

        Elements fallbackLinks = doc.select("a[href*=/News/Update/View], a[href*=/News/Notice/View], a[href*=/News/Notice/Views/]");
        for (Element a : fallbackLinks) {
            String href = a.absUrl("href");
            if (isBlank(href)) continue;
            if (!seen.add(href)) continue;

            if ("patch".equals(type) && !href.contains("/News/Update/View")) continue;
            if ("notice".equals(type)
                    && !(href.contains("/News/Notice/View") || href.contains("/News/Notice/Views/"))) continue;

            String rawTitle = cleanText(a.text());
            String title = cleanLostArkTitle(rawTitle);
            if (isBlank(title) || title.length() < 4) continue;

            String date = extractLostArkDate(rawTitle);
            if (isBlank(date)) {
                date = extractDateAround(a);
            }

            addItem(items, title, date, "", href, 10);

            if (items.size() >= 10) {
                break;
            }
        }

        return items;
    }

    private String cleanLostArkTitle(String text) {
        String title = cleanTitleNoise(cleanText(text));
        if (isBlank(title)) return "";

        title = title.replaceFirst("^(공지|점검|상점|이벤트)\\s+", "");
        title = title.replaceAll("\\s*새\\s*글\\s*", " ");
        title = title.replaceAll("\\s+\\d{1,3}(?:,\\d{3})*(?:\\+)?\\s+(?:(?:20\\d{2}\\.\\d{2}\\.\\d{2})|(?:\\d+\\s*(?:분|시간|일)\\s*전))$", "");
        title = title.replaceAll("\\s+(?:20\\d{2}\\.\\d{2}\\.\\d{2}|\\d+\\s*(?:분|시간|일)\\s*전)$", "");
        title = title.replaceAll("\\s+", " ").trim();

        return title;
    }

    private String extractLostArkDate(String text) {
        String value = cleanText(text);
        if (isBlank(value)) return "";

        Matcher dotMatcher = DATE_DOT_PATTERN.matcher(value);
        String lastDate = "";
        while (dotMatcher.find()) {
            lastDate = dotMatcher.group(1);
        }
        if (!isBlank(lastDate)) {
            return lastDate;
        }

        Matcher relativeMatcher = RELATIVE_DATE_PATTERN.matcher(value);
        String lastRelative = "";
        while (relativeMatcher.find()) {
            lastRelative = cleanText(relativeMatcher.group(1));
        }
        return lastRelative;
    }

    private List<NewsItem> crawlLostArkPatch(String url) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);

        Set<String> seen = new LinkedHashSet<String>();

        Elements rows = doc.select("ul.list li, ul.news_list li, div.list li, tbody tr");

        for (Element row : rows) {
            Element linkEl = row.selectFirst("a[href]");
            if (linkEl == null) continue;

            String href = cleanText(linkEl.absUrl("href"));
            if (isBlank(href)) continue;
            if (!(href.contains("/News/Notice/View") || href.contains("/News/Notice/Views/"))) continue;
            if (!seen.add(href)) continue;

            String rawTitle = firstNonBlank(
                    firstText(row, ".tit"),
                    firstText(row, ".title"),
                    firstText(row, ".subject"),
                    cleanText(linkEl.text()),
                    cleanText(row.text())
            );

            String title = cleanLostArkPatchSearchTitle(rawTitle);
            if (isBlank(title) || title.length() < 4) continue;
            if (!looksLikeLostArkPatchTitle(title)) continue;

            String date = firstNonBlank(
                    firstText(row, ".date"),
                    firstText(row, "time"),
                    extractDateAround(row),
                    extractLostArkDate(rawTitle)
            );

            addItem(items, title, date, "", href, 10);

            if (items.size() >= 10) {
                break;
            }
        }

        if (!items.isEmpty()) {
            return items;
        }

        Elements links = doc.select("a[href*='/News/Notice/View'], a[href*='/News/Notice/Views/']");
        for (Element a : links) {
            String href = cleanText(a.absUrl("href"));
            if (isBlank(href)) continue;
            if (!seen.add(href)) continue;

            String rawTitle = cleanText(firstNonBlank(a.text(), a.attr("title"), a.attr("aria-label")));
            String title = cleanLostArkPatchSearchTitle(rawTitle);

            if (isBlank(title) || title.length() < 4) continue;
            if (!looksLikeLostArkPatchTitle(title)) continue;

            String date = firstNonBlank(
                    extractDateAround(a),
                    extractLostArkDate(rawTitle)
            );

            addItem(items, title, date, "", href, 10);

            if (items.size() >= 10) {
                break;
            }
        }

        return items;
    }

    private String cleanLostArkPatchSearchTitle(String text) {
        String title = cleanText(text);
        if (isBlank(title)) return "";

        title = title.replaceFirst("^공지\\s+", "");
        title = title.replaceAll("\\s*새\\s*글\\s*", " ");
        title = title.replaceAll("\\s+(9999\\+|[0-9,]+)$", "");
        title = title.replaceAll("\\s+(20\\d{2}\\.\\d{2}\\.\\d{2}|\\d+\\s*(?:분|시간|일)\\s*전)$", "");
        title = title.replaceAll("\\s+", " ").trim();

        return cleanTitleNoise(title);
    }

    private boolean looksLikeLostArkPatchTitle(String title) {
        String t = cleanText(title);
        if (isBlank(t)) return false;

        return t.contains("업데이트")
                || t.contains("패치")
                || t.contains("밸런스")
                || t.contains("콘텐츠")
                || t.contains("개선")
                || t.contains("전투")
                || t.contains("시스템");
    }

    private void collectLostArkPatchListItems(Document doc, List<NewsItem> items, Set<String> seenUrls, int limit) {
        if (doc == null || items.size() >= limit) {
            return;
        }

        Elements links = doc.select(
                "a[href*='/Event/Update/'], " +
                "a[href*='/News/Update/View'], " +
                "a[href*='/News/Notice/Views/']"
        );

        for (Element a : links) {
            if (items.size() >= limit) {
                break;
            }

            String href = cleanText(a.absUrl("href"));
            if (isBlank(href)) continue;
            if (!(href.contains("/Event/Update/") || href.contains("/News/Update/View") || href.contains("/News/Notice/Views/"))) continue;
            if (!seenUrls.add(href)) continue;

            String rawText = cleanText(firstNonBlank(a.text(), a.attr("title"), a.attr("aria-label")));
            if (isBlank(rawText)) continue;
            if (rawText.contains("이벤트 모아보기") || rawText.contains("자동재생") || rawText.contains("정지")) continue;

            String title = normalizeLostArkPatchListTitle(rawText);
            if (isBlank(title) || title.length() < 4) continue;

            String date = extractLostArkPatchListDate(rawText);
            if (isBlank(date)) {
                date = extractDateAround(a);
            }

            addItem(items, title, date, "", href, limit);
        }
    }

    private void collectLostArkPatchNoticeItems(List<NewsItem> items, Set<String> seenUrls, int limit) throws IOException {
        for (int page = 1; page <= 8 && items.size() < limit; page++) {
            String url = "https://lostark.game.onstove.com/News/Notice/List?noticetype=all&page="
                    + page + "&searchtext=" + encodeUtf8("업데이트") + "&searchtype=2";

            Document doc = connect(url);
            Elements links = doc.select("a[href*='/News/Notice/Views/']");
            if (links.isEmpty()) {
                continue;
            }

            for (Element a : links) {
                if (items.size() >= limit) {
                    break;
                }

                String href = cleanText(a.absUrl("href"));
                if (isBlank(href) || !href.contains("/News/Notice/Views/")) continue;
                if (!seenUrls.add(href)) continue;

                String rawText = cleanText(firstNonBlank(a.text(), a.attr("title"), a.attr("aria-label")));
                if (isBlank(rawText)) continue;

                String title = cleanLostArkNoticePatchTitle(rawText);
                if (isBlank(title) || title.length() < 4) continue;
                if (!looksLikeLostArkPatchNotice(title, rawText)) continue;

                String date = firstNonBlank(extractLostArkDate(rawText), extractDateAround(a));
                addItem(items, title, date, "", href, limit);
            }
        }
    }

    private String normalizeLostArkPatchListTitle(String text) {
        String value = cleanText(text);
        if (isBlank(value)) {
            return "";
        }

        value = value.replaceFirst("^날짜\\s*", "");
        value = value.replaceAll("\\b새\\s*글\\b", " ");
        value = value.replaceAll("\\b20\\d{2}\\s*\\.\\s*\\d{1,2}\\s*\\.\\s*\\d{1,2}\\b", " ");
        value = value.replaceAll("\\b\\d{1,2}\\s*\\.\\s*\\d{1,2}\\s*20\\d{2}\\b", " ");
        value = value.replaceAll("\\b\\d{1,2}\\s*\\.\\s*\\d{1,2}\\b", " ");
        value = value.replaceAll("\\s+", " ").trim();

        value = cleanTitleNoise(value);
        if (value.length() > 120) {
            value = value.substring(0, 120).trim();
        }
        return value;
    }

    private String extractLostArkPatchListDate(String text) {
        String value = cleanText(text);
        if (isBlank(value)) {
            return "";
        }

        Matcher m1 = Pattern.compile("(20\\d{2})\\s*\\.\\s*(\\d{1,2})\\s*\\.\\s*(\\d{1,2})").matcher(value);
        if (m1.find()) {
            return String.format(Locale.KOREA, "%s.%02d.%02d", m1.group(1), Integer.parseInt(m1.group(2)), Integer.parseInt(m1.group(3)));
        }

        Matcher m2 = Pattern.compile("(\\d{1,2})\\s*\\.\\s*(\\d{1,2})\\s*(20\\d{2})").matcher(value);
        if (m2.find()) {
            return String.format(Locale.KOREA, "%s.%02d.%02d", m2.group(3), Integer.parseInt(m2.group(1)), Integer.parseInt(m2.group(2)));
        }

        return extractLostArkDate(value);
    }

    private String cleanLostArkNoticePatchTitle(String text) {
        String title = cleanText(text);
        if (isBlank(title)) {
            return "";
        }

        title = title.replaceFirst("^공지\\s+", "");
        title = title.replaceAll("\\s*새\\s*글\\s*", " ");
        title = title.replaceAll("\\s+(9999\\+|[0-9,]+)$", "");
        title = title.replaceAll("\\s+(20\\d{2}\\.\\d{2}\\.\\d{2}|\\d+\\s*(?:분|시간|일)\\s*전)$", "");
        title = title.replaceAll("\\s+", " ").trim();

        return title;
    }

    private boolean looksLikeLostArkPatchNotice(String title, String rawText) {
        String t = cleanText(title + " " + rawText);
        return t.contains("업데이트") || t.contains("패치") || t.contains("밸런스") || t.contains("콘텐츠");
    }

    private String encodeUtf8(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    private Document connect(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .referrer("https://www.google.com/")
                .timeout(20000)
                .followRedirects(true)
                .get();
    }

    private String normalizeValorantUrl(String url) {
        if (isBlank(url)) {
            return "";
        }
        url = url.trim();
        if (url.endsWith("/")) {
            return url;
        }
        return url + "/";
    }

    private String extractTitleFromDocumentText(String text) {
        text = cleanText(text);
        Matcher m = ISO_DATE_PATTERN.matcher(text);
        if (!m.find()) {
            return "";
        }
        String afterDate = cleanText(text.substring(m.end()));
        if (afterDate.startsWith("발로란트 ")) {
            return splitTitleAndSummary(afterDate)[0];
        }
        return "";
    }

    private String extractLeadParagraph(Document doc) {
        Element h1 = doc.selectFirst("h1");
        if (h1 != null) {
            Element next = h1.nextElementSibling();
            int guard = 0;
            while (next != null && guard < 5) {
                String text = cleanText(next.text());
                if (!isBlank(text) && !looksLikeDate(text) && text.length() >= 8) {
                    return text;
                }
                next = next.nextElementSibling();
                guard++;
            }
        }
        String fullText = cleanText(doc.text());
        Matcher m = ISO_DATE_PATTERN.matcher(fullText);
        if (m.find()) {
            String afterDate = cleanText(fullText.substring(m.end()));
            String[] parts = splitTitleAndSummary(afterDate);
            if (!isBlank(parts[1])) {
                return parts[1];
            }
        }
        return "";
    }

    private String[] splitTitleAndSummary(String text) {
        String normalized = cleanText(text);
        String[] markers = {" 패치의 ", " 다양한 ", " 이번 ", " 모드 ", " 하버와 ", " 3월, ", " 2월, ", " 1월, ", " 여러분의 ", " 발로란트 ", " 지역 ", " V26 ", " 발로란트 공식 "};
        int splitIndex = -1;
        for (String marker : markers) {
            int idx = normalized.indexOf(marker);
            if (idx > 0) {
                splitIndex = idx;
                break;
            }
        }
        if (splitIndex > 0) {
            return new String[] {
                cleanText(normalized.substring(0, splitIndex)),
                cleanText(normalized.substring(splitIndex))
            };
        }
        return new String[] { normalized, "" };
    }

    private String extractIsoDate(String text) {
        Matcher m = ISO_DATE_PATTERN.matcher(text == null ? "" : text);
        return m.find() ? m.group(1) : "";
    }

    private String formatIsoDate(String raw) {
        raw = cleanText(raw);
        if (isBlank(raw)) {
            return "";
        }
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
            in.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsed = in.parse(raw);
            return new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(parsed);
        } catch (ParseException e) {
            try {
                SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US);
                in.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date parsed = in.parse(raw);
                return new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(parsed);
            } catch (ParseException ignore) {
                return raw;
            }
        }
    }

    private boolean looksLikeDate(String text) {
        String s = cleanText(text);
        return s.matches("\\d{4}-\\d{2}-\\d{2}.*") || s.matches("\\d{4}\\.\\d{2}\\.\\d{2}.*");
    }

    private String metaContent(Document doc, String selector) {
        if (doc == null) {
            return "";
        }
        Element meta = doc.selectFirst(selector);
        return meta == null ? "" : cleanText(meta.attr("content"));
    }

    private String firstText(Element parent, String selector) {
        if (parent == null || isBlank(selector)) {
            return "";
        }
        Element found = parent.selectFirst(selector);
        return found == null ? "" : cleanText(found.text());
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return cleanText(value);
            }
        }
        return "";
    }

    private String removePubgPrefixNoise(String title) {
        title = cleanText(title);
        title = title.replaceAll("^(뉴스|제목\\+본문)\\s*", "");
        title = title.replaceAll("^(전체|PC|콘솔|이스포츠|아케이드|개발일지|유니버스|패치노트|공지)\\s+", "");
        title = title.replaceAll("^(?:전체|PC|콘솔|이스포츠|아케이드|개발일지|유니버스|패치노트|공지)\\s+", "");
        return title.trim();
    }

    private String collapseCategoryNoise(String title) {
        title = cleanText(title);
        title = title.replaceAll("\\b(PC|콘솔|이스포츠|전체)\\b", " ").replaceAll("\\s+", " ").trim();
        return title;
    }

    private String extractDateAround(Element element) {
        if (element == null) return "";
        String own = cleanText(element.text());
        String d = findDate(own);
        if (!isBlank(d)) return d;

        Element cur = element;
        for (int i = 0; i < 3 && cur != null; i++) {
            Element next = cur.nextElementSibling();
            if (next != null) {
                d = findDate(next.text());
                if (!isBlank(d)) return d;
            }
            Element parent = cur.parent();
            if (parent != null) {
                d = findDate(parent.text());
                if (!isBlank(d)) return d;
            }
            cur = parent;
        }
        return "";
    }

    private String findDate(String text) {
        text = cleanText(text);
        Matcher dot = DATE_DOT_PATTERN.matcher(text);
        if (dot.find()) return dot.group(1);
        Matcher dash = DATE_DASH_PATTERN.matcher(text);
        if (dash.find()) return dash.group(1);
        Matcher rel = RELATIVE_DATE_PATTERN.matcher(text);
        if (rel.find()) return cleanText(rel.group(1));
        return "";
    }

    private String extractRelativeDate(String text) {
        Matcher rel = RELATIVE_DATE_PATTERN.matcher(cleanText(text));
        return rel.find() ? cleanText(rel.group(1)) : "";
    }

    private void addItem(List<NewsItem> items, String title, String date, String summary, String url, int limit) {
        title = cleanTitleNoise(title);
        if (isBlank(title)) return;

        for (NewsItem existing : items) {
            if (existing.title.equals(title)) return;
        }

        items.add(new NewsItem(title, cleanText(date), cleanText(summary), cleanText(url)));
        if (items.size() > limit) {
            items.remove(items.size() - 1);
        }
    }

    private String cleanTitleNoise(String title) {
        title = cleanText(title);
        title = title.replaceFirst("^(공지|점검|상점|이벤트|일반|주요|퍼스트서버|던파ON)\\s+", "");
        title = title.replaceAll("\\s+(9999\\+|[0-9,]+)$", "");
        title = title.replaceAll("\\s+(\\d+\\s*(?:분|시간|일)\\s*전)\\s*-?$", "");
        title = title.replaceAll("^[-•·]+\\s*", "");
        title = title.replaceAll("\\s+", " ").trim();
        return title;
    }

    private String cleanBracketPrefix(String title) {
        return cleanText(title).replaceFirst("^\\[[^\\]]+\\]\\s*", "");
    }

    private String normalizeDashedDate(String value) {
        String date = findDate(value);
        return isBlank(date) ? cleanText(value) : date;
    }

    private String toAbsoluteUrl(String base, String path) {
        if (isBlank(path)) return "";
        if (path.startsWith("http")) return path;
        if (path.startsWith("//")) return "https:" + path;
        if (path.startsWith("/")) return base + path;
        return base + "/" + path;
    }

    private String unescapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\/", "/")
                .replace("\\u002F", "/")
                .replace("\\u003A", ":")
                .replace("\\u0026", "&")
                .replace("\\\"", "\"");
    }

    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        return text.replace('\u00A0', ' ').replaceAll("\\s+", " ").trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    static class NewsItem {
        String title;
        String date;
        String summary;
        String url;

        NewsItem(String title, String date, String summary, String url) {
            this.title = title == null ? "" : title;
            this.date = date == null ? "" : date;
            this.summary = summary == null ? "" : summary;
            this.url = url == null ? "" : url;
        }
    }
}
