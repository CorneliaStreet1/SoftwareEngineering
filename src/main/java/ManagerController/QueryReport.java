package ManagerController;

import ChargeStation.StationForm;
import Message.msg_ShowStationTable;
import Server.Server;
import Server.ServerThread;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@WebServlet("/query_report")
public class QueryReport extends HttpServlet {
    //这个好像是管理员查看的

    static class RData {
        int day;
        int week;
        int month;
        String pile_id;
        int total_usage_times;
        int total_charging_time;
        String total_charging_amount;
        String total_charging_earning;
        String total_service_earning;
        String total_earning;

        RData(){}

        RData(int day, int week, int month, String pile_id, int total_usage_times, int total_charging_time,
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

        String token = req.getHeader("Authorization");

        Gson gson = new Gson();
        int code = 0;
        String message = "success";
        RData[] data = null;

        String userIdStr;
        int userId;

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(ServerThread.secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            userIdStr = claims.getSubject();
            userId = Integer.parseInt(userIdStr);
        }
        catch (Exception e){
            code = -1;
            message = "token error";
            ResponseMsg responseMsg = new ResponseMsg(code, message, data);
            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            System.out.println(e);
            return;
        }

        CompletableFuture<String> future = new CompletableFuture<>();
        msg_ShowStationTable msgShowStationTable = new msg_ShowStationTable(future);

        try {
            Server.MessageQueue.put(msgShowStationTable);
            String result = future.get();

            Type type = new TypeToken<List<StationForm>>(){}.getType();
            List<StationForm> stationForms = gson.fromJson(result, type);

            if(stationForms != null) {
                int size = stationForms.size();
                data = new RData[size];
                for(int i = 0; i < size; i++) {
                    data[i] = new RData();
                    RData d = data[i];
                    StationForm formI = stationForms.get(i);
                    d.day = formI.time.getDayOfMonth();
                    d.month = formI.time.getMonthValue();
                    d.week = -1;
                    int pileID = formI.StationIndex;
                    String pileIDStr = pileID > 2 ? "S" + pileID % 3 : "F" + pileID;
                    d.pile_id = pileIDStr;
                    d.total_usage_times = formI.Accumulated_Charging_Times;
                    d.total_charging_time = (int)(formI.Total_Charging_TimeLength);
                    d.total_charging_amount = String.valueOf(formI.Total_ElectricityAmount_Charged);
                    d.total_charging_earning = String.valueOf(formI.Accumulated_Charging_Cost);
                    d.total_service_earning = String.valueOf(formI.Accumulated_Service_Cost);
                    d.total_earning = String.valueOf(formI.Total_Cost);
                }
            }

            ResponseMsg responseMsg = new ResponseMsg(code,message,data);

            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
