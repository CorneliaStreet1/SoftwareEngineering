package ClientController.Request;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import Car.Car;
import Message.msg_CancelCharging;
import Server.Server;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@WebServlet("/end_request")
public class EndRequest extends HttpServlet {

    static class ResponseMsg {
        int code;
        String message;

        ResponseMsg(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String token = req.getHeader("Authorization");

        Gson gson = new Gson();

        Claims claims = Jwts.parser()
                .setSigningKey("secretKey")
                .parseClaimsJws(token)
                .getBody();

        //todo: 这里加密的数据是username还是userId有待商榷
        String username = claims.getSubject();
        int userId = getUserId();

        int code = 0;
        String message = "success";

        try {
            if(userId < 0){
                code = -1;
                message = "token error";
            }
            else{
                Car car = new Car(userId);
                CompletableFuture<String> future = new CompletableFuture<>();
                msg_CancelCharging msgCancelCharging = new msg_CancelCharging(car, future);
                Server.MessageQueue.put(msgCancelCharging);
                String s = future.get();

                if (s.equals("false")) {
                    code = -1;
                    message = "cancel fail";
                }
            }

            SubmitRequest.ResponseMsg responseMsg = new SubmitRequest.ResponseMsg(code,message);

            String respJsonMsg = gson.toJson(responseMsg, SubmitRequest.ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
