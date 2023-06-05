package ManagerController;

import Message.msg_ShowStationTable;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

//@WebServlet("/query_report")
public class QueryReport extends HttpServlet {
    //这个好像是管理员查看的

    static class RData {
        int day;
        int week;
        int month;
        int pile_id;
        int total_usage_times;
        int total_charging_time;
        String total_charging_amount;
        String total_charging_earning;
        String total_service_earning;
        String total_earning;

        RData(int day, int week, int month, int pile_id, int total_usage_times, int total_charging_time,
              String total_charging_amount, String total_charging_earning, String total_service_earning,
              String total_earning){
            this.day = day;
            this.week = week;
            this.month = month;
            this.pile_id = pile_id;
            this.total_usage_times = total_usage_times;
            this.total_charging_time = total_charging_time;
            this.total_charging_amount = total_charging_amount;
            this.total_charging_earning = total_charging_earning;
            this.total_service_earning = total_service_earning;
            this.total_earning = total_earning;
        }
    }

    static class ResponseMsg {
        int code;
        String message;
        RData[] data;

        ResponseMsg(int code, String message, RData[] data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            String token = req.getHeader("Authorization");

            Claims claims = Jwts.parser()
                    .setSigningKey("secretKey")
                    .parseClaimsJws(token)
                    .getBody();

            String userIdStr = claims.getSubject();
            int userId = Integer.parseInt(userIdStr);

            Gson gson = new Gson();

            CompletableFuture<String> future = new CompletableFuture<>();
//        msg_ShowStationTable msgShowStationTable = new msg_ShowStationTable();

            int code = 0;
            String message = "success";


            RData[] data = new RData[1];
//        data[0] = new RData();
            ResponseMsg responseMsg = new ResponseMsg(code,message,data);

            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);

            resp.getWriter().println("{\n" +
                    "    \"code\": 0,\n" +
                    "    \"message\": \"success\",\n" +
                    "    \"data\": [\n" +
                    "        {\n" +
                    "            \"day\": 65,\n" +
                    "            \"week\": 9,\n" +
                    "            \"month\": 2,\n" +
                    "            \"pile_id\": \"P1\",\n" +
                    "            \"total_usage_times\": 173,\n" +
                    "            \"total_charging_time\": 1200000,\n" +
                    "            \"total_charging_amount\": \"1873.25\",\n" +
                    "            \"total_charging_earning\": \"2312.12\",\n" +
                    "            \"total_service_earning\": \"121.08\",\n" +
                    "            \"total_earning\": \"2433.20\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");
        }
        catch (Exception e) {
            return;
        }
    }
}
