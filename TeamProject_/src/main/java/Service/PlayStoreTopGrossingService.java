package Service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Vo.PlayStoreTopGrossingVO;

public class PlayStoreTopGrossingService {

    private static final String CHART_URL_TEMPLATE =
            "https://42matters.com/top-charts-explorer/android/south-korea/all-games?date=%s";

    private static final String GOOGLE_PLAY_SEARCH_TEMPLATE =
            "https://play.google.com/store/search?q=%s&c=apps&hl=ko&gl=KR";

    private static final int TIMEOUT_MS = 15000;

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

    private static final DateTimeFormatter QUERY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final ZoneId US_ZONE = ZoneId.of("America/New_York");

    public List<PlayStoreTopGrossingVO> getTopGrossing(int limit) {
        return getTopGrossing(limit, null);
    }

    /**
     * chartDate:
     * - null or blank -> 미국 시간 기준 오늘 날짜 사용
     * - 결과 없으면 미국 시간 기준 전날로 재시도
     * - yyyy-MM-dd -> dd-MM-yyyy 로 변환
     * - dd-MM-yyyy -> 그대로 사용
     */
    public List<PlayStoreTopGrossingVO> getTopGrossing(int limit, String chartDate) {
        if (limit < 1) limit = 1;
        if (limit > 10) limit = 10;

        List<PlayStoreTopGrossingVO> result = new ArrayList<PlayStoreTopGrossingVO>();

        try {
            List<LocalDate> targetDates = resolveTargetDates(chartDate);

            List<PlayStoreTopGrossingVO> ranked = new ArrayList<PlayStoreTopGrossingVO>();
            String finalSourceUrl = null;

            for (LocalDate targetDate : targetDates) {
                String dateParam = targetDate.format(QUERY_DATE_FORMAT);
                String rankingUrl = String.format(CHART_URL_TEMPLATE, dateParam);

                System.out.println("[PlayStore] try rankingUrl");

                Document doc = requestDocument(rankingUrl, "https://42matters.com/");
                ranked = parseTopGrossingAppsColumn(doc, rankingUrl, limit);

                if (!ranked.isEmpty()) {
                    finalSourceUrl = rankingUrl;
                    System.out.println("[PlayStore] ranking parsed count = " + ranked.size());
                    break;
                }

                System.out.println("[PlayStore] no items found for date = " + dateParam);
            }

            for (PlayStoreTopGrossingVO item : ranked) {
                if (isBlank(item.getSourceUrl()) && !isBlank(finalSourceUrl)) {
                    item.setSourceUrl(finalSourceUrl);
                }

                enrichFromGooglePlay(item);
                result.add(item);
            }

            System.out.println("[PlayStore] final result count = " + result.size());

        } catch (Exception e) {
            System.out.println("[PlayStore] getTopGrossing failed");
            e.printStackTrace();
        }

        return result;
    }

    /**
     * chartDate가 명시되지 않으면:
     * 1) 미국 시간 기준 오늘
     * 2) 결과 없으면 미국 시간 기준 전날
     */
    private List<LocalDate> resolveTargetDates(String chartDate) {
        List<LocalDate> dates = new ArrayList<LocalDate>();

        if (!isBlank(chartDate)) {
            LocalDate parsed = parseInputDate(chartDate);
            if (parsed != null) {
                dates.add(parsed);
                return dates;
            }
        }

        LocalDate usToday = ZonedDateTime.now(US_ZONE).toLocalDate();
        dates.add(usToday);
        dates.add(usToday.minusDays(1));

        return dates;
    }

    private LocalDate parseInputDate(String chartDate) {
        if (isBlank(chartDate)) {
            return null;
        }

        String value = chartDate.trim();

        if (value.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException ignore) {
                return null;
            }
        }

        if (value.matches("^\\d{2}-\\d{2}-\\d{4}$")) {
            try {
                return LocalDate.parse(value, QUERY_DATE_FORMAT);
            } catch (DateTimeParseException ignore) {
                return null;
            }
        }

        return null;
    }

    /**
     * Top Charts Explorer 페이지에서 Top Grossing Apps 컬럼만 파싱
     */
    private List<PlayStoreTopGrossingVO> parseTopGrossingAppsColumn(
            Document doc, String sourceUrl, int limit) {

        List<PlayStoreTopGrossingVO> result = new ArrayList<PlayStoreTopGrossingVO>();
        if (doc == null) {
            return result;
        }

        Elements appHeadings = doc.select("h3");

        List<Element> titles = new ArrayList<Element>();
        for (Element h3 : appHeadings) {
            String title = normalizeSpaces(h3.text());
            if (!isBlank(title)) {
                titles.add(h3);
            }
        }

        // Free / Paid / Grossing 순서로 반복된다고 가정하고 3번째마다 Grossing 추출
        int rank = 1;
        for (int i = 2; i < titles.size() && rank <= limit; i += 3) {
            Element h3 = titles.get(i);

            String title = normalizeSpaces(h3.text());
            if (isBlank(title)) {
                continue;
            }

            String developer = null;
            String iconUrl = null;

            Element block = findCardBlock(h3);
            if (block != null) {
                developer = extractDeveloperNearHeading(h3, block);
                iconUrl = extractNearbyImage(block);
            } else {
                Element parent = h3.parent();
                developer = extractDeveloperNearHeading(h3, parent);
                iconUrl = extractNearbyImage(h3);
            }

            PlayStoreTopGrossingVO item = new PlayStoreTopGrossingVO();
            item.setRank(rank);
            item.setTitle(title);
            item.setDeveloper(developer);
            item.setIconUrl(iconUrl);
            item.setSourceUrl(sourceUrl);
            item.setStoreUrl(buildGooglePlaySearchUrl(title));

            result.add(item);
            rank++;
        }

        return result;
    }

    private Element findCardBlock(Element heading) {
        if (heading == null) {
            return null;
        }

        Element current = heading.parent();
        for (int i = 0; i < 4 && current != null; i++) {
            String text = normalizeSpaces(current.text());
            if (!isBlank(text) && text.length() <= 300) {
                return current;
            }
            current = current.parent();
        }

        return heading.parent();
    }

    private String extractDeveloperNearHeading(Element heading, Element scope) {
        if (heading == null) {
            return null;
        }

        Element base = scope != null ? scope : heading.parent();
        if (base == null) {
            return null;
        }

        Elements candidates = base.getAllElements();
        boolean afterHeading = false;

        for (Element el : candidates) {
            if (el == heading) {
                afterHeading = true;
                continue;
            }
            if (!afterHeading) {
                continue;
            }

            String text = normalizeSpaces(el.ownText());
            if (isBlank(text)) {
                continue;
            }

            if (looksLikeDeveloper(text)) {
                return text;
            }
        }

        return null;
    }

    private boolean looksLikeDeveloper(String text) {
        if (isBlank(text)) {
            return false;
        }

        text = normalizeSpaces(text);

        if (text.length() < 2 || text.length() > 60) {
            return false;
        }

        if (text.startsWith("(") && text.endsWith(")")) {
            return false;
        }

        if ("Free".equalsIgnoreCase(text)) {
            return false;
        }

        if (text.startsWith("$")) {
            return false;
        }

        if (text.toLowerCase().startsWith("change:")) {
            return false;
        }

        if (text.matches("^\\d+$")) {
            return false;
        }

        if (text.equals("Top Free Apps")
                || text.equals("Top Paid Apps")
                || text.equals("Top Grossing Apps")) {
            return false;
        }

        return true;
    }

    private void enrichFromGooglePlay(PlayStoreTopGrossingVO item) {
        if (item == null || isBlank(item.getTitle())) {
            return;
        }

        try {
            String searchUrl = buildGooglePlaySearchUrl(item.getTitle());
            Document searchDoc = requestDocument(searchUrl, "https://play.google.com/");

            String detailUrl = extractDetailUrlFromSearch(searchDoc);
            if (isBlank(detailUrl)) {
                item.setStoreUrl(searchUrl);
                return;
            }

            item.setStoreUrl(detailUrl);

            Document detailDoc = requestDocument(detailUrl, "https://play.google.com/");

            String realTitle = extractTitleFromDetail(detailDoc);
            if (!isBlank(realTitle)) {
                item.setTitle(realTitle);
            }

            if (isBlank(item.getDeveloper())) {
                String developer = extractDeveloperFromPlay(detailDoc);
                if (!isBlank(developer)) {
                    item.setDeveloper(developer);
                }
            }

            if (isBlank(item.getIconUrl())) {
                String icon = extractPlayIcon(detailDoc);
                if (!isBlank(icon)) {
                    item.setIconUrl(icon);
                }
            }

            String hero = extractPlayHeroImage(detailDoc);
            if (!isBlank(hero)) {
                item.setHeroImageUrl(hero);
            }

            String screenshot = extractFirstScreenshot(detailDoc);
            if (!isBlank(screenshot)) {
                item.setScreenshotUrl(screenshot);
            }

            String packageName = extractPackageNameFromDetailUrl(detailUrl);
            if (!isBlank(packageName)) {
                item.setPackageName(packageName);
            }

        } catch (Exception e) {
            System.out.println("[PlayStore] enrich failed: " + item.getTitle());
        }
    }

    private Document requestDocument(String url, String referer) throws Exception {
        Connection connection = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .header("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Referer", referer)
                .followRedirects(true)
                .timeout(TIMEOUT_MS)
                .maxBodySize(0);

        return connection.get();
    }

    private String extractDetailUrlFromSearch(Document doc) {
        if (doc == null) {
            return null;
        }

        Elements links = doc.select("a[href*=/store/apps/details]");
        for (Element link : links) {
            String href = trimToNull(link.absUrl("href"));
            if (!isBlank(href) && href.contains("/store/apps/details") && !href.contains("cluster")) {
                return href;
            }
        }

        for (Element link : links) {
            String href = trimToNull(link.attr("href"));
            if (!isBlank(href) && href.contains("/store/apps/details")) {
                if (href.startsWith("http://") || href.startsWith("https://")) {
                    return href;
                }
                return "https://play.google.com" + href;
            }
        }

        return null;
    }

    private String extractTitleFromDetail(Document doc) {
        if (doc == null) return null;

        Element meta = doc.selectFirst("meta[property=og:title], meta[name=og:title]");
        if (meta != null) {
            String content = trimToNull(meta.attr("content"));
            if (!isBlank(content)) {
                return content.replace(" - Google Play 앱", "")
                              .replace(" - Google Play", "")
                              .trim();
            }
        }

        Element h1 = doc.selectFirst("h1");
        if (h1 != null) {
            String text = normalizeSpaces(h1.text());
            if (!isBlank(text)) {
                return text;
            }
        }

        return null;
    }

    private String extractDeveloperFromPlay(Document doc) {
        if (doc == null) {
            return null;
        }

        Element devLink = doc.selectFirst("a[href*=/store/apps/dev?id=], a[href*=/developer?id=]");
        if (devLink != null) {
            String text = normalizeSpaces(devLink.text());
            if (!isBlank(text)) {
                return text;
            }
        }

        Element author = doc.selectFirst("meta[name=author]");
        if (author != null) {
            String content = trimToNull(author.attr("content"));
            if (!isBlank(content)) {
                return content;
            }
        }

        return null;
    }

    private String extractPlayHeroImage(Document doc) {
        if (doc == null) {
            return null;
        }

        Element og = doc.selectFirst("meta[property=og:image], meta[name=og:image]");
        if (og != null) {
            String content = trimToNull(og.attr("content"));
            if (!isBlank(content)) {
                return content;
            }
        }

        Elements imgs = doc.select("img[src]");
        for (Element img : imgs) {
            String src = trimToNull(img.absUrl("src"));
            if (isBlank(src)) continue;

            if (src.contains("play-lh.googleusercontent.com")) {
                String width = img.attr("width");
                String height = img.attr("height");
                int w = parseInt(width);
                int h = parseInt(height);
                if (w >= 500 || h >= 250) {
                    return src;
                }
            }
        }

        return null;
    }

    private String extractFirstScreenshot(Document doc) {
        if (doc == null) {
            return null;
        }

        Elements imgs = doc.select("img[src]");
        for (Element img : imgs) {
            String src = trimToNull(img.absUrl("src"));
            if (isBlank(src)) continue;

            String alt = normalizeSpaces(img.attr("alt"));
            if (!isBlank(alt)) {
                String lower = alt.toLowerCase();
                if (lower.contains("screenshot") || alt.contains("스크린샷")) {
                    return src;
                }
            }
        }

        return null;
    }

    private String extractPlayIcon(Document doc) {
        if (doc == null) {
            return null;
        }

        Elements imgs = doc.select("img[src]");
        for (Element img : imgs) {
            String src = trimToNull(img.absUrl("src"));
            if (isBlank(src)) continue;

            String alt = normalizeSpaces(img.attr("alt"));
            if (!isBlank(alt)) {
                String lower = alt.toLowerCase();
                if (lower.contains("icon") || alt.contains("아이콘")) {
                    return src;
                }
            }
        }

        Element og = doc.selectFirst("meta[property=og:image], meta[name=og:image]");
        if (og != null) {
            String content = trimToNull(og.attr("content"));
            if (!isBlank(content)) {
                return content;
            }
        }

        return null;
    }

    private String extractNearbyImage(Element el) {
        if (el == null) {
            return null;
        }

        Element current = el;
        for (int i = 0; i < 4 && current != null; i++) {
            Element img = current.selectFirst("img[src], img[data-src]");
            if (img != null) {
                String src = firstNonBlank(
                        trimToNull(img.absUrl("src")),
                        trimToNull(img.absUrl("data-src")),
                        trimToNull(img.attr("src")),
                        trimToNull(img.attr("data-src"))
                );
                if (!isBlank(src)) {
                    return src;
                }
            }
            current = current.parent();
        }

        return null;
    }

    private String extractPackageNameFromDetailUrl(String url) {
        if (isBlank(url)) {
            return null;
        }

        int idx = url.indexOf("id=");
        if (idx < 0) {
            return null;
        }

        String packageName = url.substring(idx + 3);
        int amp = packageName.indexOf('&');
        if (amp >= 0) {
            packageName = packageName.substring(0, amp);
        }

        try {
            packageName = URLDecoder.decode(packageName, "UTF-8");
        } catch (Exception ignore) {
        }

        return trimToNull(packageName);
    }

    private String buildGooglePlaySearchUrl(String title) {
        try {
            return String.format(
                    GOOGLE_PLAY_SEARCH_TEMPLATE,
                    URLEncoder.encode(title, StandardCharsets.UTF_8.name())
            );
        } catch (Exception e) {
            return "https://play.google.com/store/search?q=" + title + "&c=apps&hl=ko&gl=KR";
        }
    }

    private int parseInt(String value) {
        try {
            if (isBlank(value)) {
                return 0;
            }
            return Integer.parseInt(value.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private String normalizeSpaces(String value) {
        if (value == null) {
            return null;
        }
        return value.replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}