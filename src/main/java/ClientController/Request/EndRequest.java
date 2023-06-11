package ClientController.Request;

import Car.Car;
import Message.msg_CancelCharging;
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
        int code = 0;
        String message = "success";
        Gson gson = new Gson();

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
            ResponseMsg responseMsg = new ResponseMsg(code,message);
            String respJsonMsg = gson.toJson(responseMsg, ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            System.out.println(e);
            return;
        }

        try {
            Car car = new Car(userId);
            CompletableFuture<String> future = new CompletableFuture<>();
            msg_CancelCharging msgCancelCharging = new msg_CancelCharging(car, future);
            Server.MessageQueue.put(msgCancelCharging);
            String result = future.get();

            if (result.equals("false")) {
                code = -1;
                message = "cancel fail";
            }

            ResponseMsg responseMsg = new ResponseMsg(code,message);

            String respJsonMsg = gson.toJson(responseMsg, ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



    }
}
