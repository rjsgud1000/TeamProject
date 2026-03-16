package Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NaverTrendScheduler {

    private ScheduledExecutorService scheduler;

    public NaverTrendScheduler() {
    }

    public void start() {
        if (scheduler != null && !scheduler.isShutdown()) {
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();

        try {
            NaverTrendCacheService.getInstance().refresh(); // 서버 시작 시 1회 즉시 갱신
        } catch (Exception e) {
            e.printStackTrace();
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                NaverTrendCacheService.getInstance().refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 24, 24, TimeUnit.HOURS);

        System.out.println("[NaverTrendScheduler] 스케줄러 시작");
    }

    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("[NaverTrendScheduler] 스케줄러 종료");
        }
    }
}