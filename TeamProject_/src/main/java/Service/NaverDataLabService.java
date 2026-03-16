package Service;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Vo.TrendGameVO;

public class NaverDataLabService {

    private static final String API_URL = "https://openapi.naver.com/v1/datalab/search";

    /**
     * 네이버 DataLab 검색어 트렌드는 한 요청에서 비교 가능한 그룹 수 제한을 고려해
     * anchor 1개 + 비교군 여러 개로 잘라 호출한다.
     * 현재 안전하게 1 + 4 방식으로 운용한다.
     */
    private static final int MAX_COMPARE_GAMES_PER_BATCH = 4;

    /**
     * 배치 간 보정 기준이 되는 게임
     * 검색량이 너무 낮지 않고 비교적 안정적인 게임을 선택하는 것이 좋다.
     */
    private static final String ANCHOR_GAME_NAME = "리그 오브 레전드";

    /**
     * 최종적으로 캐시에 보관할 최대 랭킹 수
     * Center.jsp 에서는 1~10위 슬라이드, 11~20위 차트에 사용
     */
    private static final int FINAL_RANK_LIMIT = 20;

    private final String clientId;
    private final String clientSecret;
    private final HttpClient httpClient;

    public NaverDataLabService() {
        String tmpClientId = null;
        String tmpClientSecret = null;

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);

                tmpClientId = trimToNull(props.getProperty("naver.client.id"));
                tmpClientSecret = trimToNull(props.getProperty("naver.client.secret"));
            } else {
                System.out.println("[NaverDataLabService] application.properties 파일을 찾지 못했습니다.");
            }
        } catch (Exception e) {
            System.out.println("[NaverDataLabService] 설정 파일 로딩 중 오류");
            e.printStackTrace();
        }

        this.clientId = tmpClientId;
        this.clientSecret = tmpClientSecret;
        this.httpClient = HttpClient.newHttpClient();

        System.out.println("[NaverDataLabService] clientId 존재 여부: " + (this.clientId != null));
        System.out.println("[NaverDataLabService] clientSecret 존재 여부: " + (this.clientSecret != null));
    }

    /**
     * 전체 게임 풀을 수집해 1~20위 랭킹을 반환한다.
     */
    public List<TrendGameVO> fetchTopGames() {
        List<TrendGameVO> finalRankList = new ArrayList<>();

        try {
            if (clientId == null || clientSecret == null) {
                System.out.println("[NaverDataLabService] 네이버 API 인증 정보가 비어 있습니다.");
                return finalRankList;
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6); // 최근 7일
            String period = startDate + " ~ " + endDate;

            List<GameKeywordGroup> gamePool = buildGamePool();
            if (gamePool.isEmpty()) {
                System.out.println("[NaverDataLabService] 게임 풀 데이터가 비어 있습니다.");
                return finalRankList;
            }

            GameKeywordGroup anchor = findGameGroup(gamePool, ANCHOR_GAME_NAME);
            if (anchor == null) {
                System.out.println("[NaverDataLabService] anchor 게임을 찾지 못했습니다: " + ANCHOR_GAME_NAME);
                return finalRankList;
            }

            List<GameKeywordGroup> others = new ArrayList<>();
            for (GameKeywordGroup group : gamePool) {
                if (!ANCHOR_GAME_NAME.equals(group.getGroupName())) {
                    others.add(group);
                }
            }

            Map<String, TrendGameVO> mergedMap = new LinkedHashMap<>();

            // 첫 배치
            List<GameKeywordGroup> firstBatch = new ArrayList<>();
            firstBatch.add(anchor);
            for (int i = 0; i < Math.min(MAX_COMPARE_GAMES_PER_BATCH, others.size()); i++) {
                firstBatch.add(others.get(i));
            }

            BatchResult firstResult = callBatch(startDate.toString(), endDate.toString(), period, firstBatch);
            if (firstResult == null || firstResult.items.isEmpty()) {
                System.out.println("[NaverDataLabService] 첫 배치 응답이 비어 있습니다.");
                return finalRankList;
            }

            if (firstResult.anchorAverage <= 0.0) {
                System.out.println("[NaverDataLabService] anchor 평균값이 0이어서 배치 보정을 진행할 수 없습니다.");
                return finalRankList;
            }

            double globalAnchorAverage = firstResult.anchorAverage;

            for (TrendGameVO vo : firstResult.items) {
                mergedMap.put(vo.getTitle(), vo);
            }

            // 나머지 배치
            for (int start = MAX_COMPARE_GAMES_PER_BATCH; start < others.size(); start += MAX_COMPARE_GAMES_PER_BATCH) {
                List<GameKeywordGroup> batch = new ArrayList<>();
                batch.add(anchor);

                for (int i = start; i < start + MAX_COMPARE_GAMES_PER_BATCH && i < others.size(); i++) {
                    batch.add(others.get(i));
                }

                BatchResult batchResult = callBatch(startDate.toString(), endDate.toString(), period, batch);
                if (batchResult == null || batchResult.items.isEmpty()) {
                    continue;
                }

                double batchAnchorAverage = batchResult.anchorAverage;
                double scale = 1.0;

                if (batchAnchorAverage > 0.0) {
                    scale = globalAnchorAverage / batchAnchorAverage;
                }

                for (TrendGameVO vo : batchResult.items) {
                    if (ANCHOR_GAME_NAME.equals(vo.getTitle())) {
                        continue;
                    }

                    TrendGameVO adjusted = cloneVO(vo);
                    adjusted.setScore(round2(adjusted.getScore() * scale));
                    mergedMap.put(adjusted.getTitle(), adjusted);
                }
            }

            finalRankList.addAll(mergedMap.values());
            finalRankList.sort(Comparator.comparingDouble(TrendGameVO::getScore).reversed());

            for (int i = 0; i < finalRankList.size(); i++) {
                finalRankList.get(i).setRank(i + 1);
            }

            if (finalRankList.size() > FINAL_RANK_LIMIT) {
                finalRankList = new ArrayList<>(finalRankList.subList(0, FINAL_RANK_LIMIT));
            }

            System.out.println("[NaverDataLabService] 최종 랭킹 수집 건수: " + finalRankList.size());

        } catch (Exception e) {
            System.out.println("[NaverDataLabService] fetchTopGames 처리 중 오류");
            e.printStackTrace();
        }

        return finalRankList;
    }

    private BatchResult callBatch(String startDate, String endDate, String period, List<GameKeywordGroup> groups) {
        List<TrendGameVO> resultList = new ArrayList<>();
        double anchorAverage = 0.0;

        try {
            String requestBody = buildRequestBody(startDate, endDate, groups);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            System.out.println("[NaverDataLabService] 응답 코드: " + response.statusCode());

            if (response.statusCode() != 200) {
                System.out.println("[NaverDataLabService] 응답 본문: " + response.body());
                return new BatchResult(resultList, anchorAverage);
            }

            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(response.body());
            JSONArray results = (JSONArray) root.get("results");

            if (results == null || results.isEmpty()) {
                return new BatchResult(resultList, anchorAverage);
            }

            for (Object obj : results) {
                JSONObject item = (JSONObject) obj;

                String title = safeString(item.get("title"));
                if (title == null || title.isEmpty()) {
                    title = safeString(item.get("groupName"));
                }

                JSONArray data = (JSONArray) item.get("data");
                if (data == null || data.isEmpty()) {
                    continue;
                }

                double avgRatio = calculateAverageRatio(data);

                TrendGameVO vo = new TrendGameVO();
                vo.setTitle(title);
                vo.setScore(round2(avgRatio));
                vo.setPeriod(period);
                vo.setSource("Naver DataLab");

                resultList.add(vo);

                if (ANCHOR_GAME_NAME.equals(title)) {
                    anchorAverage = avgRatio;
                }
            }

        } catch (Exception e) {
            System.out.println("[NaverDataLabService] 배치 호출 중 오류");
            e.printStackTrace();
        }

        return new BatchResult(resultList, anchorAverage);
    }

    private double calculateAverageRatio(JSONArray dataArray) {
        double sum = 0.0;
        int count = 0;

        for (Object obj : dataArray) {
            JSONObject row = (JSONObject) obj;
            Object ratioObj = row.get("ratio");

            double ratio = 0.0;
            if (ratioObj instanceof Number) {
                ratio = ((Number) ratioObj).doubleValue();
            } else if (ratioObj != null) {
                try {
                    ratio = Double.parseDouble(String.valueOf(ratioObj));
                } catch (NumberFormatException ignore) {
                    ratio = 0.0;
                }
            }

            sum += ratio;
            count++;
        }

        return count == 0 ? 0.0 : (sum / count);
    }

    @SuppressWarnings("unchecked")
    private String buildRequestBody(String startDate, String endDate, List<GameKeywordGroup> groups) {
        JSONObject body = new JSONObject();
        body.put("startDate", startDate);
        body.put("endDate", endDate);
        body.put("timeUnit", "date");
        body.put("device", "pc");

        JSONArray keywordGroups = new JSONArray();

        for (GameKeywordGroup group : groups) {
            JSONObject jsonGroup = new JSONObject();
            jsonGroup.put("groupName", group.getGroupName());

            JSONArray keywordArray = new JSONArray();
            for (String keyword : group.getKeywords()) {
                keywordArray.add(keyword);
            }

            jsonGroup.put("keywords", keywordArray);
            keywordGroups.add(jsonGroup);
        }

        body.put("keywordGroups", keywordGroups);
        return body.toJSONString();
    }

    private List<GameKeywordGroup> buildGamePool() {
        List<GameKeywordGroup> list = new ArrayList<>();

        // anchor
        list.add(new GameKeywordGroup("리그 오브 레전드",
                "리그 오브 레전드", "롤", "league of legends"));

        // 기존 주요 게임
        list.add(new GameKeywordGroup("발로란트",
                "발로란트", "valorant"));
        list.add(new GameKeywordGroup("FC 온라인",
                "FC 온라인", "FC온라인", "피파온라인"));
        list.add(new GameKeywordGroup("배틀그라운드",
                "배틀그라운드", "PUBG", "배그"));
        list.add(new GameKeywordGroup("메이플스토리",
                "메이플스토리", "메이플"));
        list.add(new GameKeywordGroup("로스트아크",
                "로스트아크"));
        list.add(new GameKeywordGroup("던전앤파이터",
                "던전앤파이터", "던파"));
        list.add(new GameKeywordGroup("오버워치",
                "오버워치", "오버워치2", "오버워치 2", "overwatch", "overwatch 2"));
        list.add(new GameKeywordGroup("서든어택",
                "서든어택", "서든"));
        list.add(new GameKeywordGroup("스타크래프트",
                "스타크래프트", "starcraft", "스타"));

        // 추가 게임
        list.add(new GameKeywordGroup("리니지",
                "리니지", "lineage"));
        list.add(new GameKeywordGroup("아이온2",
                "아이온2", "aion2", "아이온", "aion"));
        list.add(new GameKeywordGroup("로블록스",
                "로블록스", "roblox"));
        list.add(new GameKeywordGroup("디아블로2",
                "디아블로2", "디아2", "diablo 2"));
        list.add(new GameKeywordGroup("카운터스트라이크2",
                "카운터스트라이크2", "카스2", "counter strike 2", "cs2"));
        list.add(new GameKeywordGroup("월드 오브 워크래프트",
                "월드오브워크래프트", "와우", "world of warcraft"));
        list.add(new GameKeywordGroup("이터널 리턴",
                "이터널리턴", "이터널 리턴", "eternal return"));
        list.add(new GameKeywordGroup("워크래프트",
                "워크래프트", "warcraft"));
        list.add(new GameKeywordGroup("사이퍼즈",
                "사이퍼즈"));
        list.add(new GameKeywordGroup("테일즈런너",
                "테일즈런너"));
        list.add(new GameKeywordGroup("마비노기",
                "마비노기"));
        list.add(new GameKeywordGroup("로우바둑이",
                "로우바둑이"));
        list.add(new GameKeywordGroup("패스 오브 엑자일",
                "패스오브엑자일", "패오엑", "path of exile"));
        list.add(new GameKeywordGroup("스타크래프트2",
                "스타크래프트2", "starcraft 2"));
        list.add(new GameKeywordGroup("원신",
                "원신", "genshin"));

        return list;
    }

    private GameKeywordGroup findGameGroup(List<GameKeywordGroup> groups, String groupName) {
        for (GameKeywordGroup group : groups) {
            if (groupName.equals(group.getGroupName())) {
                return group;
            }
        }
        return null;
    }

    private TrendGameVO cloneVO(TrendGameVO source) {
        TrendGameVO copy = new TrendGameVO();
        copy.setRank(source.getRank());
        copy.setTitle(source.getTitle());
        copy.setScore(source.getScore());
        copy.setPeriod(source.getPeriod());
        copy.setSource(source.getSource());
        return copy;
    }

    private String safeString(Object value) {
        return value == null ? null : String.valueOf(value).trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class BatchResult {
        private final List<TrendGameVO> items;
        private final double anchorAverage;

        private BatchResult(List<TrendGameVO> items, double anchorAverage) {
            this.items = items;
            this.anchorAverage = anchorAverage;
        }
    }

    private static class GameKeywordGroup {
        private final String groupName;
        private final List<String> keywords;

        private GameKeywordGroup(String groupName, String... keywords) {
            this.groupName = groupName;
            this.keywords = Arrays.asList(keywords);
        }

        public String getGroupName() {
            return groupName;
        }

        public List<String> getKeywords() {
            return keywords;
        }
    }
}