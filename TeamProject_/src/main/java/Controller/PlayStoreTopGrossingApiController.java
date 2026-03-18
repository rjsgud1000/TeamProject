package Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Service.PlayStoreTopGrossingService;
import Vo.PlayStoreTopGrossingVO;

@WebServlet("/api/playstore/top-grossing")
public class PlayStoreTopGrossingApiController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PlayStoreTopGrossingService service =
            new PlayStoreTopGrossingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int limit = 20;

        try {
            String limitParam = request.getParameter("limit");
            if (limitParam != null && !limitParam.trim().isEmpty()) {
                limit = Integer.parseInt(limitParam);
            }
        } catch (Exception ignore) {
            limit = 20;
        }

        List<PlayStoreTopGrossingVO> items = service.getTopGrossing(limit);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        JSONObject result = new JSONObject();
        JSONArray arr = new JSONArray();

        for (PlayStoreTopGrossingVO item : items) {
            JSONObject obj = new JSONObject();
            obj.put("rank", item.getRank());
            obj.put("title", item.getTitle());
            obj.put("packageName", item.getPackageName());
            obj.put("developer", item.getDeveloper());
            obj.put("iconUrl", item.getIconUrl());
            obj.put("heroImageUrl", item.getHeroImageUrl());
            obj.put("screenshotUrl", item.getScreenshotUrl());
            obj.put("storeUrl", item.getStoreUrl());
            obj.put("sourceUrl", item.getSourceUrl());
            arr.add(obj);
        }

        result.put("success", !arr.isEmpty());
        result.put("items", arr);

        if (arr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            result.put("message", "플레이스토어 차트 데이터를 불러오지 못했습니다.");
        }

        try (PrintWriter out = response.getWriter()) {
            out.write(result.toJSONString());
        }
    }
}