package ClientController.Query;

import Car.Car;
import Server.Server;
import Message.msg_CheckChargingForm;

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

        Claims claims = Jwts.parser()
                .setSigningKey("secretKey")
                .parseClaimsJws(token)
                .getBody();

        String userIdStr = claims.getSubject();
        int userId = Integer.parseInt(userIdStr);

        Gson gson = new Gson();
        Car car = new Car(userId);
        CompletableFuture<String> future = new CompletableFuture<>();
        msg_CheckChargingForm msgCheckChargingForm = new msg_CheckChargingForm(car, future);

        try {
            Server.MessageQueue.put(msgCheckChargingForm);
            String result = future.get();
            //todo: 详单还没处理

            int code = 0;
            String order_id = "20230501000001";
            String create_time = "2023-05-01T12:11:11.000Z";
            String charged_amount = "12.34";
            int charged_time = 600;
            String begin_time = "2023-05-01T11:11:11.000Z";
            String end_time = "2023-05-01T12:11:11.000Z";
            String charging_cost = "8.92";
            String service_cost = "1.23";
            String total_cost = "10.15";
            String pile_id = "C01";
            String message = "success";

            RData[] data = new RData[1];
            data[0] = new RData(order_id,create_time,charged_amount,
                    charged_time, begin_time, end_time, charging_cost, service_cost,
                    total_cost, pile_id);
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
