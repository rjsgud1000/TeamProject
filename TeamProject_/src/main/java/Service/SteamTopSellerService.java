package Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Vo.SteamTopSellerVO;

public class SteamTopSellerService {

    // 기존 charts/topselling/KR HTML 직접 파싱 방식은 현재 구조상 잘 깨짐
    // 그래서 Steam 검색 결과 AJAX 엔드포인트를 사용
    private static final String SEARCH_RESULTS_API =
            "https://store.steampowered.com/search/results/";

    private static final String APPDETAILS_API =
            "https://store.steampowered.com/api/appdetails?cc=KR&l=koreana&appids=";

    private static final int TIMEOUT_MS = 12000;

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

    public List<SteamTopSellerVO> getTopSellers(int limit) {
        if (limit < 1) limit = 1;
        if (limit > 20) limit = 20;

        List<SteamTopSellerVO> items = fetchTopSellersFromSearch(limit);

        // 검색 결과에서 제목/이미지가 빠지는 경우를 대비한 보정
        enrichMissingAppDetails(items);

        return items;
    }

    private List<SteamTopSellerVO> fetchTopSellersFromSearch(int limit) {
        List<SteamTopSellerVO> result = new ArrayList<>();

        try {
            String url = buildSearchResultsUrl(limit);
            String body = httpGet(url, "https://store.steampowered.com/search/?filter=topsellers&cc=KR&l=koreana");

            if (body == null || body.isBlank()) {
                return result;
            }

            String resultsHtml = extractResultsHtml(body);
            if (resultsHtml == null || resultsHtml.isBlank()) {
                return result;
            }

            Document doc = Jsoup.parse(resultsHtml);
            Elements rows = doc.select("a.search_result_row");

            int rank = 1;
            for (Element row : rows) {
                if (rank > limit) {
                    break;
                }

                Integer appId = extractAppId(row);
                if (appId == null) {
                    continue;
                }

                SteamTopSellerVO item = new SteamTopSellerVO();
                item.setRank(rank);
                item.setAppId(appId);

                String title = textOrNull(row.selectFirst("span.title"));
                if (title == null) {
                    title = textOrNull(row.selectFirst(".title"));
                }
                item.setTitle(title);

                String imageUrl = null;
                Element img = row.selectFirst("div.col.search_capsule img");
                if (img == null) {
                    img = row.selectFirst("img");
                }
                if (img != null) {
                    imageUrl = attrOrNull(img, "src");
                }
                item.setHeaderImage(imageUrl);

                String href = attrOrNull(row, "href");
                if (href == null || href.isBlank()) {
                    href = "https://store.steampowered.com/app/" + appId + "/";
                }
                item.setStoreUrl(href);

                result.add(item);
                rank++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String buildSearchResultsUrl(int limit) throws Exception {
        StringBuilder sb = new StringBuilder(SEARCH_RESULTS_API);
        sb.append("?query=");
        sb.append("&start=0");
        sb.append("&count=").append(limit);
        sb.append("&dynamic_data=");
        sb.append("&sort_by=_ASC");
        sb.append("&supportedlang=").append(URLEncoder.encode("koreana", "UTF-8"));
        sb.append("&snr=").append(URLEncoder.encode("1_7_7_230_7", "UTF-8"));
        sb.append("&filter=").append(URLEncoder.encode("topsellers", "UTF-8"));
        sb.append("&os=").append(URLEncoder.encode("win", "UTF-8"));
        sb.append("&cc=").append(URLEncoder.encode("KR", "UTF-8"));
        sb.append("&ignore_preferences=1");
        sb.append("&hide_filtered_results_warning=1");
        sb.append("&ndl=1");
        sb.append("&infinite=1");
        return sb.toString();
    }

    private String extractResultsHtml(String responseBody) {
        try {
            Object parsed = new JSONParser().parse(responseBody);
            if (parsed instanceof JSONObject) {
                JSONObject obj = (JSONObject) parsed;

                Object htmlObj = obj.get("results_html");
                if (htmlObj != null) {
                    return String.valueOf(htmlObj);
                }
            }
        } catch (Exception ignore) {
            // JSON이 아니면 HTML일 수도 있으니 아래 fallback
        }

        // fallback: 혹시 전체 HTML이 통으로 내려오면 그대로 사용
        if (responseBody.contains("search_result_row")) {
            return responseBody;
        }

        return null;
    }

    private Integer extractAppId(Element row) {
        try {
            String dataDsAppid = attrOrNull(row, "data-ds-appid");
            if (dataDsAppid != null && !dataDsAppid.isBlank()) {
                // 종종 "12345" 또는 "12345,67890" 형태일 수 있음
                String[] parts = dataDsAppid.split(",");
                for (String part : parts) {
                    Integer parsed = safeParseInt(part.trim());
                    if (parsed != null && parsed > 0) {
                        return parsed;
                    }
                }
            }

            String href = attrOrNull(row, "href");
            if (href != null) {
                java.util.regex.Matcher m =
                        java.util.regex.Pattern.compile("/app/(\\d+)/").matcher(href);
                if (m.find()) {
                    return safeParseInt(m.group(1));
                }
            }
        } catch (Exception ignore) {
        }

        return null;
    }

    private void enrichMissingAppDetails(List<SteamTopSellerVO> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        for (SteamTopSellerVO item : items) {
            if (item == null || item.getAppId() == null) {
                continue;
            }

            boolean needsTitle = isBlank(item.getTitle());
            boolean needsImage = isBlank(item.getHeaderImage());

            try {
                String json = httpGet(
                        APPDETAILS_API + item.getAppId(),
                        "https://store.steampowered.com/app/" + item.getAppId() + "/"
                );

                if (json == null || json.isBlank()) {
                    continue;
                }

                JSONObject root = (JSONObject) new JSONParser().parse(json);
                JSONObject appObj = (JSONObject) root.get(String.valueOf(item.getAppId()));
                if (appObj == null) {
                    continue;
                }

                Object successObj = appObj.get("success");
                if (!(successObj instanceof Boolean) || !((Boolean) successObj)) {
                    continue;
                }

                JSONObject dataObj = (JSONObject) appObj.get("data");
                if (dataObj == null) {
                    continue;
                }

                Object nameObj = dataObj.get("name");
                Object headerImageObj = dataObj.get("header_image");

                if (nameObj != null) {
                    String apiTitle = String.valueOf(nameObj).trim();

                    // 제목이 비어 있거나, 검색 결과보다 앱 상세 제목이 더 적합할 때 덮어쓰기
                    if (needsTitle || !isLikelyKorean(item.getTitle())) {
                        item.setTitle(apiTitle);
                    }
                }

                if (needsImage && headerImageObj != null) {
                    item.setHeaderImage(String.valueOf(headerImageObj));
                }

                if (isBlank(item.getStoreUrl())) {
                    item.setStoreUrl("https://store.steampowered.com/app/" + item.getAppId() + "/");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isLikelyKorean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if ((ch >= 0xAC00 && ch <= 0xD7A3) || (ch >= 0x1100 && ch <= 0x11FF)) {
                return true;
            }
        }
        return false;
    }

    private String textOrNull(Element el) {
        if (el == null) return null;
        String text = el.text();
        return text == null || text.isBlank() ? null : text.trim();
    }

    private String attrOrNull(Element el, String attr) {
        if (el == null) return null;
        String value = el.attr(attr);
        return value == null || value.isBlank() ? null : value.trim();
    }

    private Integer safeParseInt(String value) {
        try {
            return Integer.valueOf(Integer.parseInt(value));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String httpGet(String urlStr, String referer) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);

            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
            conn.setRequestProperty("Accept", "application/json, text/html, */*;q=0.9");
            conn.setRequestProperty("Accept-Encoding", "gzip");
            conn.setRequestProperty("Referer", referer);
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            int status = conn.getResponseCode();
            if (status < 200 || status >= 300) {
                return null;
            }

            InputStream in = conn.getInputStream();
            String encoding = conn.getContentEncoding();
            if (encoding != null && encoding.toLowerCase().contains("gzip")) {
                in = new GZIPInputStream(in);
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}