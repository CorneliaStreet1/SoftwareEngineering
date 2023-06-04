package ClientController.Request;

import Message.msg_ChangeChargingMode;
import Message.msg_ChangeChargeCapacity;
import Server.Server;

import Car.Car;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//完成？
@WebServlet("/edit_request")
public class EditRequest extends HttpServlet {

    static class ReqBody {
        String charge_mode;

        String require_amount;

        String battery_size;
    }

    static class ResponseMsg {
        int code;
        String message;

        ResponseMsg(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String token = req.getHeader("Authorization");

        Claims claims = Jwts.parser()
                .setSigningKey("secretKey")
                .parseClaimsJws(token)
                .getBody();

        String userIdStr = claims.getSubject();
        int userId = Integer.parseInt(userIdStr);

        Gson gson = new Gson();

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(req.getReader());
        String line;
        while( ( line = br.readLine()) != null){
            sb.append(line);
        }
        String requestBody = sb.toString();

        ReqBody reqBody = gson.fromJson(requestBody, ReqBody.class);

        boolean isFastCharge = reqBody.charge_mode.equals("F");
        double requestedChargingCapacity = Double.parseDouble(reqBody.require_amount);
        double carBatteryCapacity = Double.parseDouble(reqBody.battery_size);

        Car car = new Car(isFastCharge, requestedChargingCapacity, carBatteryCapacity, userId);

        CompletableFuture<String> modeFuture = new CompletableFuture<>();
        CompletableFuture<String> capacityFuture = new CompletableFuture<>();

        msg_ChangeChargingMode msgChangeChargingMode = new msg_ChangeChargingMode(car,modeFuture);
        msg_ChangeChargeCapacity msgChangeChargeCapacity = new msg_ChangeChargeCapacity(requestedChargingCapacity, car, capacityFuture);

        try {
            Server.MessageQueue.put(msgChangeChargingMode);
            Server.MessageQueue.put(msgChangeChargeCapacity);

            String ms = modeFuture.get();
            String cs = capacityFuture.get();

            int code = 0;
            String message = "success";

            if (ms.equals("false") || cs.equals("false")){
                code = -1;
                message = "fail";
            }

            ResponseMsg responseMsg = new ResponseMsg(code,message);

            String respJsonMsg = gson.toJson(responseMsg, ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
        catch (ExecutionException e) {
            System.out.println(e);
        }
    }
}
