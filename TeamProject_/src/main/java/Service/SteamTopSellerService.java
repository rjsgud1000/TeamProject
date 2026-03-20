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

    private static final String SEARCH_RESULTS_API =
            "https://store.steampowered.com/search/results/";

    private static final String APPDETAILS_API_KO =
            "https://store.steampowered.com/api/appdetails?cc=KR&l=koreana&appids=";

    private static final String APPDETAILS_API_EN =
            "https://store.steampowered.com/api/appdetails?cc=KR&l=english&appids=";

    private static final int TIMEOUT_MS = 12000;

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

    public List<SteamTopSellerVO> getTopSellers(int limit) {
        if (limit < 1) limit = 1;
        if (limit > 20) limit = 20;

        List<SteamTopSellerVO> items = fetchTopSellersFromSearch(limit);

        // 제목/영문명/이미지 보정
        enrichAppDetails(items);

        return items;
    }

    private List<SteamTopSellerVO> fetchTopSellersFromSearch(int limit) {
        List<SteamTopSellerVO> result = new ArrayList<>();

        try {
            String url = buildSearchResultsUrl(limit);
            String body = httpGet(
                    url,
                    "https://store.steampowered.com/search/?filter=topsellers&cc=KR&l=koreana"
            );

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

                // search/results + supportedlang=koreana 기준이라
                // 여기서 잡히는 값은 우선 한글명으로 간주
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
            // JSON 아니면 fallback
        }

        if (responseBody.contains("search_result_row")) {
            return responseBody;
        }

        return null;
    }

    private Integer extractAppId(Element row) {
        try {
            String dataDsAppid = attrOrNull(row, "data-ds-appid");
            if (dataDsAppid != null && !dataDsAppid.isBlank()) {
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

    private void enrichAppDetails(List<SteamTopSellerVO> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        for (SteamTopSellerVO item : items) {
            if (item == null || item.getAppId() == null) {
                continue;
            }

            try {
                AppDetail koDetail = fetchAppDetail(item.getAppId(), true);
                AppDetail enDetail = fetchAppDetail(item.getAppId(), false);

                // 한글명 보정
                if (koDetail != null && !isBlank(koDetail.name)) {
                    if (isBlank(item.getTitle()) || !isLikelyKorean(item.getTitle())) {
                        item.setTitle(koDetail.name);
                    }
                }

                // 영문명 세팅
                if (enDetail != null && !isBlank(enDetail.name)) {
                    item.setEnglishTitle(enDetail.name);
                }

                // 만약 영문명 못 가져오면 fallback
                if (isBlank(item.getEnglishTitle())) {
                    item.setEnglishTitle(item.getTitle());
                }

                // 이미지 보정
                if (isBlank(item.getHeaderImage())) {
                    if (koDetail != null && !isBlank(koDetail.headerImage)) {
                        item.setHeaderImage(koDetail.headerImage);
                    } else if (enDetail != null && !isBlank(enDetail.headerImage)) {
                        item.setHeaderImage(enDetail.headerImage);
                    }
                }

                if (isBlank(item.getStoreUrl())) {
                    item.setStoreUrl("https://store.steampowered.com/app/" + item.getAppId() + "/");
                }

            } catch (Exception e) {
                e.printStackTrace();

                // 에러나도 최소 fallback 유지
                if (isBlank(item.getEnglishTitle())) {
                    item.setEnglishTitle(item.getTitle());
                }
            }
        }
    }

    private AppDetail fetchAppDetail(Integer appId, boolean korean) {
        HttpURLConnection conn = null;

        try {
            String apiUrl = (korean ? APPDETAILS_API_KO : APPDETAILS_API_EN) + appId;
            URL url = new URL(apiUrl);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);

            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept-Language",
                    korean ? "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7"
                           : "en-US,en;q=0.9,ko-KR;q=0.8,ko;q=0.7");
            conn.setRequestProperty("Accept", "application/json, text/html, */*;q=0.9");
            conn.setRequestProperty("Accept-Encoding", "gzip");
            conn.setRequestProperty("Referer", "https://store.steampowered.com/app/" + appId + "/");
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

            String json = sb.toString();
            if (json == null || json.isBlank()) {
                return null;
            }

            JSONObject root = (JSONObject) new JSONParser().parse(json);
            JSONObject appObj = (JSONObject) root.get(String.valueOf(appId));
            if (appObj == null) {
                return null;
            }

            Object successObj = appObj.get("success");
            if (!(successObj instanceof Boolean) || !((Boolean) successObj)) {
                return null;
            }

            JSONObject dataObj = (JSONObject) appObj.get("data");
            if (dataObj == null) {
                return null;
            }

            AppDetail detail = new AppDetail();
            Object nameObj = dataObj.get("name");
            Object headerImageObj = dataObj.get("header_image");

            if (nameObj != null) {
                detail.name = String.valueOf(nameObj).trim();
            }
            if (headerImageObj != null) {
                detail.headerImage = String.valueOf(headerImageObj).trim();
            }

            return detail;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
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

    private static class AppDetail {
        private String name;
        private String headerImage;
    }
}