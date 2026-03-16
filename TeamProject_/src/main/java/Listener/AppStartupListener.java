package Listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import Service.NaverTrendScheduler;

@WebListener
public class AppStartupListener implements ServletContextListener {

    private NaverTrendScheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = new NaverTrendScheduler();
        scheduler.start();
        System.out.println("[AppStartupListener] Naver Trend 스케줄러 초기화 완료");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.stop();
        }
        System.out.println("[AppStartupListener] 종료");
    }
}