package Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Vo.TrendGameVO;

public class NaverTrendCacheService {

    private static final NaverTrendCacheService instance = new NaverTrendCacheService();

    private final NaverDataLabService dataLabService;
    private List<TrendGameVO> cachedList;
    private LocalDateTime lastUpdated;

    private NaverTrendCacheService() {
        this.dataLabService = new NaverDataLabService();
        this.cachedList = new ArrayList<>();
        this.lastUpdated = null;
    }

    public static NaverTrendCacheService getInstance() {
        return instance;
    }

    /**
     * 네이버 DataLab에서 최신 랭킹 데이터를 다시 조회해 캐시에 반영한다.
     * 조회 실패 시에는 기존 캐시 데이터를 유지한다.
     */
    public synchronized void refresh() {
        try {
            List<TrendGameVO> latest = dataLabService.fetchTopGames();

            if (latest != null && !latest.isEmpty()) {
                this.cachedList = new ArrayList<>(latest);
                this.lastUpdated = LocalDateTime.now();

                System.out.println("[NaverTrendCacheService] 캐시 갱신 완료");
                System.out.println("[NaverTrendCacheService] 갱신 시각: " + this.lastUpdated);
                System.out.println("[NaverTrendCacheService] 캐시 건수: " + this.cachedList.size());
            } else {
                System.out.println("[NaverTrendCacheService] 새 데이터가 비어 있어 기존 캐시를 유지합니다.");
            }
        } catch (Exception e) {
            System.out.println("[NaverTrendCacheService] 캐시 갱신 중 오류 발생");
            e.printStackTrace();
        }
    }

    /**
     * 전체 캐시 목록 반환
     */
    public synchronized List<TrendGameVO> getCachedList() {
        return new ArrayList<>(cachedList);
    }

    /**
     * 1~10위 목록 반환
     * Center.jsp 슬라이드 영역에서 사용
     */
    public synchronized List<TrendGameVO> getTop10List() {
        if (cachedList == null || cachedList.isEmpty()) {
            return new ArrayList<>();
        }

        int end = Math.min(10, cachedList.size());
        return new ArrayList<>(cachedList.subList(0, end));
    }

    /**
     * 11~20위 목록 반환
     * Center.jsp 팝업/차트 영역에서 사용
     */
    public synchronized List<TrendGameVO> getRank11To20List() {
        if (cachedList == null || cachedList.size() <= 10) {
            return new ArrayList<>();
        }

        int from = 10;
        int to = Math.min(20, cachedList.size());
        return new ArrayList<>(cachedList.subList(from, to));
    }

    /**
     * 마지막 갱신 시각 반환
     */
    public synchronized LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * 캐시가 비어있는지 확인
     */
    public synchronized boolean isEmpty() {
        return cachedList == null || cachedList.isEmpty();
    }

    /**
     * 캐시 건수 반환
     */
    public synchronized int size() {
        return cachedList == null ? 0 : cachedList.size();
    }
}