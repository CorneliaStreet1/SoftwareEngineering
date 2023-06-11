package ClientController.Request;

import Car.Car;
import Message.msg_EnterWaitingZone;
import Server.Server;
import Server.ServerThread;
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
@WebServlet("/submit_request")
public class SubmitRequest extends HttpServlet {

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        resp.setContentType("application/json");
        Gson gson = new Gson();

        String token = req.getHeader("Authorization");

        String userIdStr;
        int userId;
        int code = 0;
        String message = "success";

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
            ResponseMsg responseMsg = new ResponseMsg(code,message);
            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            System.out.println(e);
            return;
        }


        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(req.getReader());
        String line;
        while( ( line = br.readLine()) != null){
            sb.append(line);
        }
        String requestBody = sb.toString();

        ReqBody reqBody = gson.fromJson(requestBody,ReqBody.class);

        boolean isFastCharge = false;
        double requestedChargingCapacity = 0;
        double carBatteryCapacity = 0;

        try {
            isFastCharge = reqBody.charge_mode.equals("F");
            requestedChargingCapacity = Double.parseDouble(reqBody.require_amount);
            carBatteryCapacity = Double.parseDouble(reqBody.battery_size);
        }
        catch (NullPointerException e) {
            code = -1;
            message = "post body error ";
            ResponseMsg responseMsg = new ResponseMsg(code,message);
            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            System.out.println(e);
            return;
        }

        Car car = new Car(isFastCharge, requestedChargingCapacity, carBatteryCapacity, userId);
        CompletableFuture<String> future = new CompletableFuture<>();
        msg_EnterWaitingZone msg2q = new msg_EnterWaitingZone(car,future);

        try {
            Server.MessageQueue.put(msg2q);
            String result = future.get();

            if (result.equals("false")){
                code = -1;
                message = "fail";
            }

            ResponseMsg responseMsg = new ResponseMsg(code,message);

            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);
        }
        catch (InterruptedException e) {
            code = -1;
            message = "service is interrupted";
            ResponseMsg responseMsg = new ResponseMsg(code,message);
            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            System.out.println(e);
            return;
        }
        catch (ExecutionException e){
            code = -1;
            message = "service execution exception";
            ResponseMsg responseMsg = new ResponseMsg(code,message);
            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            System.out.println(e);
            return;
        }
    }
}
