package ClientController.Request;

import Car.Car;

import Message.msg_EnterWaitingZone;

import Server.Server;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        String authorization = req.getHeader("Authorization");


        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(req.getReader());
        String line;
        while( ( line = br.readLine()) != null){
            sb.append(line);
        }
        String requestBody = sb.toString();

        Gson gson = new Gson();

        ReqBody reqBody = gson.fromJson(requestBody,ReqBody.class);

        boolean isFastCharge = reqBody.charge_mode.equals("F");
        double requestedChargingCapacity = Double.parseDouble(reqBody.require_amount);
        double carBatteryCapacity = Double.parseDouble(reqBody.battery_size);

        Car car = new Car(isFastCharge, requestedChargingCapacity, carBatteryCapacity);
        CompletableFuture<String> future = new CompletableFuture<>();
        msg_EnterWaitingZone msg2q = new msg_EnterWaitingZone(car,future);

        try {
            Server.MessageQueue.put(msg2q);
            String s = future.get();

            int code = 0;
            String message = "success";

            if (s.equals("false")){
                code = -1;
                message = "fail";
            }

            ResponseMsg responseMsg = new ResponseMsg(code,message);

            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
        catch (ExecutionException e){
            System.out.println(e);
        }
    }
}
