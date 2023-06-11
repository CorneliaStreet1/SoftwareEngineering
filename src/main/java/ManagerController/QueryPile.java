package ManagerController;

import ChargeStation.StationState;
import Message.msg_CheckAllStationState;
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

@WebServlet("/query_pile")
public class QueryPile extends HttpServlet {
    enum Status{
        RUNNING,
        SHUTDOWN,
        UNAVAILABLE;
    }

    static class RData {
        String pile_id;
        Status status;
        int total_usage_times;
        int total_charging_time;
        String total_charging_amount;

        RData(String pile_id, Status status, int total_usage_times, int total_charging_time, String total_charging_amount){
            this.pile_id = pile_id;
            this.status = status;
            this.total_usage_times = total_usage_times;
            this.total_charging_time = total_charging_time;
            this.total_charging_amount = total_charging_amount;
        }

        RData(){
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

        String token = req.getHeader("Authorization");
        Gson gson = new Gson();
        int code = 0;
        String message = "success";
        RData[] data = null;


        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(ServerThread.secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            String userIdStr = claims.getSubject();
            int userId = Integer.parseInt(userIdStr);
        }
        catch (Exception e) {
            code = -1;
            message = "token error";
            ResponseMsg responseMsg = new ResponseMsg(code, message, data);
            String respJsonMsg = gson.toJson(responseMsg, ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            System.out.println(e);
            return;
        }


        //todo: 用户是否合法检测
//        CompletableFuture<String> loginFuture = new CompletableFuture<>();
        CompletableFuture<String> queryPileFuture = new CompletableFuture<>();

        msg_CheckAllStationState msgCheckAllStationState = new msg_CheckAllStationState(queryPileFuture);

        try {
            Server.MessageQueue.put(msgCheckAllStationState);
            String result = queryPileFuture.get();

            Type listType = new TypeToken<List<StationState>>() {}.getType();

            List<StationState> stationStates =  gson.fromJson(result, listType);


            int dataSize = stationStates.size();
            data = new RData[dataSize];

            for(int i = 0; i < dataSize; i++){
                data[i] = new RData();
                RData dataI = data[i];
                StationState stationStateI = stationStates.get(i);
                int pileID = stationStateI.StationID;
                String pileIDStr = pileID > 2 ? "S" + pileID % 3 : "F" + pileID;
                dataI.pile_id = pileIDStr;
                System.out.println("isFaulty: " + stationStateI.isFaulty);
                System.out.println("isOnService: " + stationStateI.isOnService);
                if(stationStateI.isFaulty){
                    dataI.status = Status.UNAVAILABLE;
                }
                else if(stationStateI.isOnService){
                    dataI.status = Status.RUNNING;
                }
                else{
                    dataI.status = Status.SHUTDOWN;
                }
                dataI.total_usage_times = stationStateI.Accumulated_Charging_Times;
                dataI.total_charging_time = (int) stationStateI.Total_Charging_TimeLength;
                dataI.total_charging_amount = String.valueOf(stationStateI.Total_ElectricityAmount_Charged);
            }

            ResponseMsg responseMsg = new ResponseMsg(code, message, data);

            String respJsonMsg = gson.toJson(responseMsg, ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
