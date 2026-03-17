package Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

            System.out.println("[PlayStore-42matters] title = " + doc.title());
            System.out.println("[PlayStore-42matters] html length = " + doc.outerHtml().length());

            Elements candidates = selectRankingCandidates(doc);
            System.out.println("[PlayStore-42matters] candidate count = " + candidates.size());

            Set<String> seenTitles = new LinkedHashSet<String>();
            int rank = 1;

            for (Element el : candidates) {
                if (rank > limit) {
                    break;
                }

                String text = normalizeSpaces(el.text());
                if (isBlank(text)) {
                    continue;
                }

                // 예시 형식:
                // "1 Last War:Survival Game by First Fun 10M+ Downloads"
                ParsedRankItem parsed = parseRankItem(text);
                if (parsed == null) {
                    continue;
                }

                String dedupeKey = parsed.title;
                if (!seenTitles.add(dedupeKey)) {
                    continue;
                }

                PlayStoreTopGrossingVO item = new PlayStoreTopGrossingVO();
                item.setRank(rank);
                item.setTitle(parsed.title);
                item.setPackageName(null);
                item.setIconUrl(extractIconUrl(el));
                item.setStoreUrl(buildGooglePlaySearchUrl(parsed.title));
                item.setAppBrainUrl(RANKING_URL);

                result.add(item);

                System.out.println("[PlayStore-42matters] rank=" + rank + ", title=" + parsed.title);
                rank++;
            }

            System.out.println("[PlayStore-42matters] result count = " + result.size());

        } catch (Exception e) {
            System.out.println("[PlayStore-42matters] getTopGrossing failed");
            e.printStackTrace();
        }

        return result;
    }

    private Elements selectRankingCandidates(Document doc) {
        Elements result = new Elements();

        // 1차: 리스트 항목
        Elements listItems = doc.select("li");
        for (Element li : listItems) {
            String text = normalizeSpaces(li.text());
            if (looksLikeRankRow(text)) {
                result.add(li);
            }
        }

        // 2차: 혹시 li로 안 잡히는 경우 보조 탐색
        if (result.isEmpty()) {
            Elements blocks = doc.select("p, div, a");
            for (Element block : blocks) {
                String text = normalizeSpaces(block.text());
                if (looksLikeRankRow(text)) {
                    result.add(block);
                }
            }
        }

        return dedupeElements(result);
    }

    private Elements dedupeElements(Elements elements) {
        Elements deduped = new Elements();
        Set<String> seen = new LinkedHashSet<String>();

        for (Element el : elements) {
            String key = normalizeSpaces(el.text());
            if (isBlank(key)) {
                continue;
            }
            if (seen.add(key)) {
                deduped.add(el);
            }
        }
        return deduped;
    }

    private boolean looksLikeRankRow(String text) {
        if (isBlank(text)) {
            return false;
        }

        // 상위 랭킹 행 패턴 대략 판별
        // 예: "1 Last War:Survival Game by First Fun 10M+ Downloads"
        if (!text.matches("^\\d+\\s+.+")) {
            return false;
        }

        if (!text.contains(" by ")) {
            return false;
        }

        if (!text.toLowerCase().contains("download")) {
            return false;
        }

        return true;
    }

    private ParsedRankItem parseRankItem(String text) {
        if (isBlank(text)) {
            return null;
        }

        // 맨 앞 rank 제거
        String normalized = text.replaceFirst("^\\s*(\\d+)\\s+", "").trim();

        int byIndex = normalized.indexOf(" by ");
        if (byIndex <= 0) {
            return null;
        }

        String title = normalized.substring(0, byIndex).trim();
        if (isBlank(title)) {
            return null;
        }

        ParsedRankItem item = new ParsedRankItem();
        item.title = title;
        return item;
    }

    private String extractIconUrl(Element el) {
        if (el == null) {
            return null;
        }

        Element current = el;
        for (int depth = 0; depth < 4 && current != null; depth++) {
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

    private String buildGooglePlaySearchUrl(String title) {
        try {
            return "https://play.google.com/store/search?q="
                    + URLEncoder.encode(title, StandardCharsets.UTF_8.name())
                    + "&c=apps&hl=ko&gl=KR";
        } catch (Exception e) {
            return "https://play.google.com/store/search?q=" + title + "&c=apps&hl=ko&gl=KR";
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
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
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static class ParsedRankItem {
        String title;
    }
}