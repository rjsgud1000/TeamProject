package Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Vo.PlayStoreTopGrossingVO;

public class PlayStoreTopGrossingService {

    private static final String RANKING_URL =
            "https://42matters.com/top-grossing-mobile-games-south-korea";

    private static final int TIMEOUT_MS = 15000;

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

    public List<PlayStoreTopGrossingVO> getTopGrossing(int limit) {
        if (limit < 1) limit = 1;
        if (limit > 20) limit = 20;

        List<PlayStoreTopGrossingVO> result = new ArrayList<PlayStoreTopGrossingVO>();

        try {
            Document doc = Jsoup.connect(RANKING_URL)
                    .userAgent(USER_AGENT)
                    .header("Accept",
                            "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                    .header("Referer", "https://42matters.com/")
                    .followRedirects(true)
                    .timeout(TIMEOUT_MS)
                    .maxBodySize(0)
                    .get();

            System.out.println("[PlayStore] ranking title = " + doc.title());
            System.out.println("[PlayStore] ranking html length = " + doc.outerHtml().length());

            List<PlayStoreTopGrossingVO> ranked = parseRankingPage(doc, limit);
            for (PlayStoreTopGrossingVO item : ranked) {
                enrichFromGooglePlay(item);
                result.add(item);
            }

            System.out.println("[PlayStore] result count = " + result.size());

        } catch (Exception e) {
            System.out.println("[PlayStore] getTopGrossing failed");
            e.printStackTrace();
        }

        return result;
    }

    private List<PlayStoreTopGrossingVO> parseRankingPage(Document doc, int limit) {
        List<PlayStoreTopGrossingVO> result = new ArrayList<PlayStoreTopGrossingVO>();
        Set<String> seenTitles = new LinkedHashSet<String>();

        Elements candidates = new Elements();
        candidates.addAll(doc.select("li"));
        candidates.addAll(doc.select("tr"));
        candidates.addAll(doc.select("div"));
        candidates.addAll(doc.select("p"));

        int rank = 1;

        for (Element el : candidates) {
            if (rank > limit) {
                break;
            }

            String text = normalizeSpaces(el.text());
            if (!looksLikeRankRow(text)) {
                continue;
            }

            ParsedRankItem parsed = parseRankItem(text);
            if (parsed == null || isBlank(parsed.title)) {
                continue;
            }

            String dedupeKey = parsed.title.toLowerCase();
            if (!seenTitles.add(dedupeKey)) {
                continue;
            }

            PlayStoreTopGrossingVO item = new PlayStoreTopGrossingVO();
            item.setRank(rank);
            item.setTitle(parsed.title);
            item.setDeveloper(parsed.developer);
            item.setSourceUrl(RANKING_URL);

            // 42matters 공개 텍스트 기준으로는 packageName 확보가 어려워서 null 유지
            item.setPackageName(null);

            // 검색 링크 우선
            item.setStoreUrl(buildGooglePlaySearchUrl(parsed.title));

            // 42matters 페이지 내 이미지가 있으면 우선 아이콘으로 사용
            String iconUrl = extractNearbyImage(el);
            item.setIconUrl(iconUrl);

            result.add(item);
            rank++;
        }

        return result;
    }

    private void enrichFromGooglePlay(PlayStoreTopGrossingVO item) {
        if (item == null || isBlank(item.getTitle())) {
            return;
        }

        try {
            // 1차: 검색 페이지에서 상세 링크 추출
            String searchUrl = buildGooglePlaySearchUrl(item.getTitle());
            Document searchDoc = requestDocument(searchUrl, "https://play.google.com/");

            String detailUrl = extractDetailUrlFromSearch(searchDoc);
            if (isBlank(detailUrl)) {
                // 검색에서 못 찾으면 fallback: 검색 링크 유지
                item.setStoreUrl(searchUrl);
                return;
            }

            item.setStoreUrl(detailUrl);

            // 2차: 상세 페이지 접근
            Document detailDoc = requestDocument(detailUrl, "https://play.google.com/");

            // 아이콘
            if (isBlank(item.getIconUrl())) {
                String icon = extractPlayIcon(detailDoc);
                if (!isBlank(icon)) {
                    item.setIconUrl(icon);
                }
            }

            // 대표 배경 이미지
            String hero = extractPlayHeroImage(detailDoc);
            if (!isBlank(hero)) {
                item.setHeroImageUrl(hero);
            }

            // 스크린샷 fallback
            String screenshot = extractFirstScreenshot(detailDoc);
            if (!isBlank(screenshot)) {
                item.setScreenshotUrl(screenshot);
            }

            // 개발사명 보강
            if (isBlank(item.getDeveloper())) {
                String developer = extractDeveloperFromPlay(detailDoc);
                if (!isBlank(developer)) {
                    item.setDeveloper(developer);
                }
            }

            // 패키지명 추출
            if (isBlank(item.getPackageName())) {
                String packageName = extractPackageNameFromDetailUrl(detailUrl);
                if (!isBlank(packageName)) {
                    item.setPackageName(packageName);
                }
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

    private boolean looksLikeRankRow(String text) {
        if (isBlank(text)) {
            return false;
        }

        if (!text.matches("^\\d+\\s+.+")) {
            return false;
        }

        return text.contains(" by ");
    }

    private ParsedRankItem parseRankItem(String text) {
        if (isBlank(text)) {
            return null;
        }

        String normalized = text.replaceFirst("^\\s*\\d+\\s+", "").trim();
        int byIndex = normalized.indexOf(" by ");
        if (byIndex <= 0) {
            return null;
        }

        String title = normalized.substring(0, byIndex).trim();
        String rest = normalized.substring(byIndex + 4).trim();

        ParsedRankItem item = new ParsedRankItem();
        item.title = title;
        item.developer = extractDeveloper(rest);
        return item;
    }

    private String extractDeveloper(String rest) {
        if (isBlank(rest)) {
            return null;
        }

        String lower = rest.toLowerCase();
        int downloadsIdx = lower.indexOf(" downloads");
        if (downloadsIdx > 0) {
            return rest.substring(0, downloadsIdx).trim();
        }

        return rest.trim();
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

    private String extractDetailUrlFromSearch(Document doc) {
        if (doc == null) {
            return null;
        }

        Elements links = doc.select("a[href*=/store/apps/details]");
        for (Element link : links) {
            String href = trimToNull(link.absUrl("href"));
            if (!isBlank(href) && href.contains("/store/apps/details")) {
                if (!href.contains("cluster")) {
                    return href;
                }
            }
        }

        // 일부 경우 상대 링크만 들어오는 경우 대비
        for (Element link : links) {
            String href = trimToNull(link.attr("href"));
            if (!isBlank(href) && href.contains("/store/apps/details")) {
                if (href.startsWith("http")) {
                    return href;
                }
                return "https://play.google.com" + href;
            }
        }

        return null;
    }

    private String extractPlayHeroImage(Document doc) {
        if (doc == null) {
            return null;
        }

        // 우선순위 1: og:image
        Element og = doc.selectFirst("meta[property=og:image], meta[name=og:image]");
        if (og != null) {
            String content = trimToNull(og.attr("content"));
            if (!isBlank(content)) {
                return content;
            }
        }

        // 우선순위 2: 큰 이미지
        Elements imgs = doc.select("img[src]");
        for (Element img : imgs) {
            String src = trimToNull(img.absUrl("src"));
            if (isBlank(src)) {
                continue;
            }

            String alt = normalizeSpaces(img.attr("alt"));
            if (alt != null && (
                    alt.contains("feature graphic") ||
                    alt.contains("대표") ||
                    alt.contains("배너"))) {
                return src;
            }

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
            if (isBlank(src)) {
                continue;
            }

            String alt = normalizeSpaces(img.attr("alt"));
            if (alt != null && (
                    alt.contains("스크린샷") ||
                    alt.toLowerCase().contains("screenshot"))) {
                return src;
            }

            if (src.contains("play-lh.googleusercontent.com")) {
                return src;
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
            if (isBlank(src)) {
                continue;
            }

            String alt = normalizeSpaces(img.attr("alt"));
            if (alt != null && (
                    alt.contains("아이콘") ||
                    alt.toLowerCase().contains("icon"))) {
                return src;
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

    private String extractDeveloperFromPlay(Document doc) {
        if (doc == null) {
            return null;
        }

        // 개발사 링크 텍스트 추출 시도
        Elements links = doc.select("a, div, span");
        for (Element el : links) {
            String text = normalizeSpaces(el.text());
            if (isBlank(text)) {
                continue;
            }

            // 너무 긴 문구 제외
            if (text.length() <= 40 && !text.contains("설치") && !text.contains("인앱")) {
                if (el.hasAttr("href") && el.attr("href").contains("/store/apps/dev")) {
                    return text;
                }
            }
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

        return trimToNull(packageName);
    }

    private String buildGooglePlaySearchUrl(String title) {
        try {
            return "https://play.google.com/store/search?q="
                    + URLEncoder.encode(title, StandardCharsets.UTF_8.name())
                    + "&c=apps&hl=ko&gl=KR";
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

    private static class ParsedRankItem {
        String title;
        String developer;
    }
}