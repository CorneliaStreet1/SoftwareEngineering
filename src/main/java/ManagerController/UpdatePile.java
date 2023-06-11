package ManagerController;

import Message.Message;
import Message.msg_StationFault;
import Message.msg_StationRecovery;
import Message.msg_TurnOffStation;
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

@WebServlet("/update_pile")
public class UpdatePile extends HttpServlet {

    enum Status{
        RUNNING,
        SHUTDOWN,
        UNAVAILABLE;
    }

    static class ReqBody {
        String pile_id;
        Status status;
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");

        Gson gson = new Gson();
        int code = 0;
        String message = "success";

        String token = req.getHeader("Authorization");


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
            ResponseMsg responseMsg = new ResponseMsg(code, message);
            String respJsonMsg = gson.toJson(responseMsg, ResponseMsg.class);
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

        int pileId = -1;
        try {
            String pileIDStr = reqBody.pile_id.substring(1);
            String pileMode = reqBody.pile_id.substring(0,1);
            pileId = Integer.parseInt(pileIDStr);
            if(!pileMode.equals("F")) {
                pileId += 3;
            }
        }
        catch (Exception e) {
            code = -1;
            message = "pile id error";
            ResponseMsg responseMsg = new ResponseMsg(code, message);
            String respJsonMsg = gson.toJson(responseMsg, ResponseMsg.class);
            resp.getWriter().println(respJsonMsg);
            return;
        }

//        CompletableFuture<String> checkIdFuture = new CompletableFuture<>();
        CompletableFuture<String> updatePileFuture = new CompletableFuture<>();

        try {
            Message msg;
            if(reqBody.status == Status.RUNNING){
                msg = new msg_StationRecovery(pileId, updatePileFuture);
            }
            else if(reqBody.status == Status.SHUTDOWN){
                msg = new msg_TurnOffStation(pileId, updatePileFuture);
            }
            else{
                msg = new msg_StationFault(pileId, 0, updatePileFuture);
            }

            Server.MessageQueue.put(msg);
            String result = updatePileFuture.get();

            if(result.equals("false")){
                code = -1;
                message = "false";
            }

            ResponseMsg responseMsg = new ResponseMsg(code, message);

            String respJsonMsg = gson.toJson(responseMsg, ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
