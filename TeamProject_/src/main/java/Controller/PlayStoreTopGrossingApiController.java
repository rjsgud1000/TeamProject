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

    private final PlayStoreTopGrossingService playStoreTopGrossingService =
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
        } catch (Exception e) {
            limit = 20;
        }

        if (limit < 1) limit = 1;
        if (limit > 20) limit = 20;

        List<PlayStoreTopGrossingVO> items = playStoreTopGrossingService.getTopGrossing(limit);

        JSONArray arr = new JSONArray();

        for (PlayStoreTopGrossingVO item : items) {
            JSONObject obj = new JSONObject();
            obj.put("rank", item.getRank());
            obj.put("title", item.getTitle());
            obj.put("packageName", item.getPackageName());
            obj.put("iconUrl", item.getIconUrl());
            obj.put("storeUrl", item.getStoreUrl());
            obj.put("appBrainUrl", item.getAppBrainUrl());
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