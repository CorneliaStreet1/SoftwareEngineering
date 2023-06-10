package ClientController.Query;

import Car.Car;
import ChargeStation.ChargingRecord;
import Message.msg_CheckChargingForm;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@WebServlet("/query_order")
public class QueryOrder extends HttpServlet {

    static class RData {
        String order_id;
        String create_time;
        String charged_amount;
        int charged_time;
        String begin_time;
        String end_time;
        String charging_cost;
        String service_cost;
        String total_cost;
        String pile_id;

        RData(){}

        RData(String order_id, String create_time, String charged_amount, int charged_time, String begin_time,
              String end_time, String charging_cost, String service_cost, String total_cost, String pile_id){
            this.order_id = order_id;
            this.create_time = create_time;
            this.charged_amount = charged_amount;
            this.charged_time = charged_time;
            this.begin_time = begin_time;
            this.end_time = end_time;
            this.charging_cost =charging_cost;
            this.service_cost = service_cost;
            this.total_cost = total_cost;
            this.pile_id = pile_id;
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

        Gson gson = new Gson();
        int code = 0;
        String message = "success";
        RData[] data = null;


        String userIdStr = null;
        int userId = -1;
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(ServerThread.secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            userIdStr = claims.getSubject();
            userId = Integer.parseInt(userIdStr);
        }
        catch (Exception e) {
            code = -1;
            message = "token error";
            ResponseMsg responseMsg = new ResponseMsg(code,message,data);
            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            return;
        }

        Car car = new Car(userId);
        CompletableFuture<String> future = new CompletableFuture<>();
        msg_CheckChargingForm msgCheckChargingForm = new msg_CheckChargingForm(car, future);

        try {
            Server.MessageQueue.put(msgCheckChargingForm);
            String result = future.get();
            //todo: 详单还没处理

            Type type = new TypeToken<List< ChargingRecord >>() {}.getType();
            List<ChargingRecord> chargingRecords = gson.fromJson(result, type);

            if(chargingRecords != null) {
                int size = chargingRecords.size();
                data = new RData[size];
                for(int i = 0; i < size; i++) {
                    data[i] = new RData();
                    RData d = data[i];
                    ChargingRecord recordI = chargingRecords.get(i);
                    d.order_id = recordI.getOrderId();
                    d.create_time = String.valueOf(LocalDateTime.parse(recordI.getOrderGenerationTime()));
                    d.charged_amount = String.valueOf(recordI.getTotalElectricityAmountCharged());
                    d.charged_time = (int) recordI.getChargeTimeDuration();
                    d.begin_time = String.valueOf(LocalDateTime.parse(recordI.getStartTime()));
                    d.end_time = String.valueOf(LocalDateTime.parse(recordI.getEndTime()));
                    d.charging_cost = String.valueOf(recordI.getElectricityCost());
                    d.total_cost = String.valueOf(recordI.getTotalCost());
                    int pileID = recordI.getChargeStationId();
                    String pileIDStr = pileID > 2 ? "S" + pileID % 3 : "F" + pileID;
                    d.pile_id = pileIDStr;
                }
            }

            ResponseMsg responseMsg = new ResponseMsg(code,message,data);

            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);
        } catch (ExecutionException e) {
            code = -1;
            message = "ExecutionException";
            ResponseMsg responseMsg = new ResponseMsg(code,message,data);
            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            code = -1;
            message = "ExecutionException";
            ResponseMsg responseMsg = new ResponseMsg(code,message,data);
            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            throw new RuntimeException(e);
        }

    }
}
