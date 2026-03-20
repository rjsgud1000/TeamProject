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

import Service.SteamTopSellerService;
import Vo.SteamTopSellerVO;

@WebServlet("/api/steam/top-sellers")
public class SteamTopSellerApiController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final SteamTopSellerService steamTopSellerService = new SteamTopSellerService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int limit = 20;
        try {
            String limitParam = request.getParameter("limit");
            if (limitParam != null && !limitParam.isBlank()) {
                limit = Integer.parseInt(limitParam);
            }
        } catch (Exception e) {
            limit = 20;
        }

        if (limit < 1) limit = 1;
        if (limit > 20) limit = 20;

        List<SteamTopSellerVO> items = steamTopSellerService.getTopSellers(limit);

        JSONArray arr = new JSONArray();
        for (SteamTopSellerVO item : items) {
            JSONObject obj = new JSONObject();
            obj.put("rank", item.getRank());
            obj.put("appId", item.getAppId());
            obj.put("title", item.getTitle()); // 한글명
            obj.put("englishTitle", item.getEnglishTitle()); // 영문명
            obj.put("originalTitle", item.getEnglishTitle()); // 프론트 호환용 alias
            obj.put("headerImage", item.getHeaderImage());
            obj.put("storeUrl", item.getStoreUrl());
            arr.add(obj);
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        if (arr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }

        try (PrintWriter out = response.getWriter()) {
            out.write(arr.toJSONString());
        }
    }
}