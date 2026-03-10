package Controller;

import java.io.IOException;
import java.io.PrintWriter;
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
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36";

    private static final Pattern ISO_DATE_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?Z)");
    private static final Pattern DATE_DOT_PATTERN = Pattern.compile("(20\\d{2}\\.\\d{2}\\.\\d{2})");
    private static final Pattern DATE_DASH_PATTERN = Pattern.compile("(20\\d{2}-\\d{2}-\\d{2})");
    private static final Pattern FC_UPDATE_DATE_PREFIX_PATTERN = Pattern.compile("^(20\\d{2})\\s*(\\d{2}\\.\\d{2})\\s+(.+)$");
    private static final Pattern AION_JSON_ITEM_PATTERN = Pattern.compile("\\\"title\\\"\\s*:\\s*\\\"([^\\\"]+)\\\".*?\\\"(?:createDate|createdDate|registerDate|regDate|date)\\\"\\s*:\\s*\\\"([^\\\"]+)\\\".*?(?:\\\"linkUrl\\\"|\\\"url\\\"|\\\"path\\\")\\s*:\\s*\\\"([^\\\"]+)\\\"", Pattern.DOTALL);
    private static final Pattern AION_TEXT_ITEM_PATTERN = Pattern.compile("(\\[안내\\]\\s*.+?)\\s*[·•]\\s*(20\\d{2}-\\d{2}-\\d{2})");

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
        if ("fconline".equals(game)) {
            if ("notice".equals(type)) return "https://fconline.nexon.com/news/notice/list";
            if ("patch".equals(type)) return "https://fconline.nexon.com/news/update/list";
        }
        if ("maplestory".equals(game)) {
            if ("notice".equals(type)) return "https://maplestory.nexon.com/News/Notice";
            if ("patch".equals(type)) return "https://maplestory.nexon.com/News/Update";
        }
        if ("aion2".equals(game)) {
            if ("notice".equals(type)) return "https://aion2.plaync.com/ko-kr/board/notice/list";
            if ("patch".equals(type)) return "https://aion2.plaync.com/ko-kr/board/update/list";
        }
        if ("dnf".equals(game)) {
            if ("notice".equals(type)) return "https://df.nexon.com/community/news/notice/list";
            if ("patch".equals(type)) return "https://df.nexon.com/community/news/update/list";
        }
        if ("lostark".equals(game)) {
            if ("notice".equals(type)) return "https://lostark.game.onstove.com/News/Notice/List";
            if ("patch".equals(type)) return "https://lostark.game.onstove.com/News/Update/List";
        }
        return "";
    }

    private List<NewsItem> crawlByGame(String game, String type, String sourceUrl) throws IOException {
        if ("lol".equals(game)) {
            return crawlLol(sourceUrl);
        }
        if ("battleground".equals(game)) {
            return crawlPubgTextOnly(sourceUrl, "patch".equals(type) ? "패치노트" : "공지");
        }
        if ("valorant".equals(game)) {
            return crawlValorant(sourceUrl, "notice".equals(type) ? "/ko-kr/news/announcements/" : "/ko-kr/news/game-updates/");
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
            return crawlDnf(sourceUrl);
        }
        if ("lostark".equals(game)) {
            return crawlLostArk(sourceUrl, type);
        }
        return new ArrayList<NewsItem>();
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

        Elements links = doc.select("a[href]");
        for (Element a : links) {
            String href = a.absUrl("href");
            if (!href.contains("/News/") || href.equals(url)) continue;
            String title = cleanText(a.text());
            if (isBlank(title) || title.length() < 4) continue;
            if (title.equals("공지사항") || title.equals("업데이트") || title.equals("이벤트")) continue;
            String date = extractDateAround(a);
            if (isBlank(date)) continue;
            addItem(items, title, date, "", href, 10);
        }

        if (!items.isEmpty()) return items;

        String text = cleanText(doc.text());
        Matcher m = Pattern.compile("(.+?)\\s+(20\\d{2}\\.\\d{2}\\.\\d{2})").matcher(text);
        while (m.find() && items.size() < 10) {
            String title = m.group(1);
            if (title.contains("공지사항") || title.contains("업데이트") || title.contains("이벤트")) continue;
            if (title.length() > 120) title = title.substring(Math.max(0, title.length() - 120)).trim();
            addItem(items, cleanTitleNoise(title), m.group(2), "", url, 10);
        }
        return items;
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

    private List<NewsItem> crawlDnf(String url) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);
        for (Element a : doc.select("a[href]")) {
            String href = a.absUrl("href");
            String title = cleanText(a.text());
            if (isBlank(title) || title.length() < 4) continue;
            if (!(href.contains("/community/news/") || href.contains("/df/news/") || href.contains("/news/") || href.contains("mode=view"))) continue;
            if (title.equals("공지사항") || title.equals("업데이트")) continue;
            String date = extractDateAround(a);
            if (isBlank(date)) continue;
            addItem(items, title, date, "", href, 10);
        }

        if (!items.isEmpty()) return items;

        String text = cleanText(doc.text());
        Matcher m = Pattern.compile("(?:일반|점검|주요|퍼스트서버|던파ON|대규모)\\s+(.+?)\\s+(20\\d{2}\\.\\d{2}\\.\\d{2})\\s+[0-9,]+")
                .matcher(text);
        while (m.find() && items.size() < 10) {
            addItem(items, cleanTitleNoise(m.group(1)), m.group(2), "", url, 10);
        }
        return items;
    }

    private List<NewsItem> crawlLostArk(String url, String type) throws IOException {
        List<NewsItem> items = new ArrayList<NewsItem>();
        Document doc = connect(url);
        String text = cleanText(doc.text());

        if ("patch".equals(type) && text.contains("해당 게시물이 없습니다")) {
            return items;
        }

        for (Element a : doc.select("a[href]")) {
            String href = a.absUrl("href");
            String title = cleanText(a.text());
            if (isBlank(title) || title.length() < 4) continue;
            if (!(href.contains("/News/Notice/View") || href.contains("/News/Update/View"))) continue;
            String date = extractDateAround(a);
            addItem(items, title.replaceFirst("^(공지|점검|상점|이벤트)\\s+", ""), date, "", href, 10);
        }

        if (!items.isEmpty()) return items;

        Matcher m = Pattern.compile("(?:공지|점검|상점|이벤트)?\\s*(.+?)\\s+(20\\d{2}\\.\\d{2}\\.\\d{2})").matcher(text);
        while (m.find() && items.size() < 10) {
            String title = cleanTitleNoise(m.group(1));
            if (title.contains("새소식") || title.contains("공지사항") || title.contains("업데이트")) continue;
            addItem(items, title, m.group(2), "", url, 10);
        }
        return items;
    }

    private Document connect(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
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
        Matcher rel = Pattern.compile("(\\d+\\s*(?:분|시간|일)\\s*전)").matcher(text);
        if (rel.find()) return cleanText(rel.group(1));
        return "";
    }

    private String extractRelativeDate(String text) {
        Matcher rel = Pattern.compile("(\\d+\\s*(?:분|시간|일)\\s*전)").matcher(cleanText(text));
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
        return value.replace("\\/", "/").replace("\\u002F", "/").replace("\\u003A", ":").replace("\\u0026", "&").replace("\\\"", "\"");
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
