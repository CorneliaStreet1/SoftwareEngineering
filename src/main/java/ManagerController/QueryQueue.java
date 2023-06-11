package ManagerController;

import ChargeStation.StationInfo;
import Message.msg_CheckStationInfo;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@WebServlet("/query_queue")
public class QueryQueue extends HttpServlet {
//这个好像是管理员查看的

    static class RData {
        String pile_id;
        String username;
        String battery_size;
        String require_amount;
        int waiting_time;

        RData(){}

        RData(String pile_id, String username, String battery_size,
              String require_amount, int waiting_time){
            this.pile_id = pile_id;
            this.username = username;
            this.battery_size = battery_size;
            this.require_amount = require_amount;
            this.waiting_time = waiting_time;
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

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String token = req.getHeader("Authorization");
        int code = 0;
        String message = "success";
        RData[] data = null;
        Gson gson = new Gson();

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
        msg_CheckStationInfo msgCheckStationInfo = new msg_CheckStationInfo(future);

        try {
            Server.MessageQueue.put(msgCheckStationInfo);
            String result = future.get();

            Type type = new TypeToken<List<StationInfo>>(){}.getType();
            List<StationInfo> stationInfos = gson.fromJson(result, type);


            if(stationInfos != null) {
                int size = stationInfos.size();
                data = new RData[size];
                for(int i = 0; i < size; i++) {
                    data[i] = new RData();
                    RData d = data[i];
                    StationInfo infoI = stationInfos.get(i);
                    int pileID = infoI.StationID;
                    String pileIDStr = pileID > 2 ? "S" + pileID % 3 : "F" + pileID;
                    d.pile_id = pileIDStr;
                    d.username = infoI.UserID;
                    d.battery_size = String.valueOf(infoI.CarBatteryCapacity);
                    d.require_amount = String.valueOf(infoI.RequestedChargingCapacity);
                    d.waiting_time = (int)infoI.WaitingTime;
                }
            }

//        data[0] = new RData();
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
