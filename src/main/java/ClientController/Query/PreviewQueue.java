package ClientController.Query;

import Car.Car;
import ChargeStation.QueueSituation;
import Message.msg_PreviewQueueSituation;
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

@WebServlet("/preview_queue")
public class PreviewQueue extends HttpServlet {

    public enum CurState {
        NOTCHARGING,
        WAITINGSTAGE1,
        WAITINGSTAGE2,
        CHARGING,
        CHANGEMODEREQUEUE,
        FAULTREQUEUE;
    }

    static class RData {
        String charge_id;
        int queue_len;

        CurState cur_state;

        String place;

        RData(String charge_id,int queue_len, CurState cur_state, String place) {
            this.charge_id = charge_id;
            this.queue_len = queue_len;
            this.cur_state = cur_state;
            this.place = place;
        }

    }

    static class ResponseMsg {
        int code;
        String message;

        RData data;

        ResponseMsg(int code, String message, RData data) {
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
        RData data = null;

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
        msg_PreviewQueueSituation msgPreviewQueueSituation = new msg_PreviewQueueSituation(car, future);

        try {
            Server.MessageQueue.put(msgPreviewQueueSituation);
            String result = future.get();
            QueueSituation queueSituation = gson.fromJson(result, QueueSituation.class);

            //todo: 理论上，如果用户不存在，应该检测出并返回code = -1，但这里没有处理

            String charge_id = String.valueOf(queueSituation.seq);
            int queue_len = queueSituation.queue_len;
            CurState cur_state = CurState.valueOf(queueSituation.CurrentState);
            String place = queueSituation.CurrentPlace;

            data = new RData(charge_id, queue_len, cur_state, place);

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
            message = "InterruptedException";
            ResponseMsg responseMsg = new ResponseMsg(code,message,data);
            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            throw new RuntimeException(e);
        }
    }
}
