package ManagerController;

import Message.Message;
import Message.msg_StationFault;
import Message.msg_StationRecovery;
import Message.msg_TurnOffStation;

import Server.Server;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

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

        ResponseMsg(int code, String message, QueryPile.RData[] data) {
            this.code = code;
            this.message = message;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");

        Gson gson = new Gson();

        String token = req.getHeader("Authorization");

        Claims claims = Jwts.parser()
                .setSigningKey("secretKey")
                .parseClaimsJws(token)
                .getBody();

        String userIdStr = claims.getSubject();
        int userId = Integer.parseInt(userIdStr);

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(req.getReader());
        String line;
        while( ( line = br.readLine()) != null){
            sb.append(line);
        }
        String requestBody = sb.toString();

        ReqBody reqBody = gson.fromJson(requestBody,ReqBody.class);

//        CompletableFuture<String> checkIdFuture = new CompletableFuture<>();
        CompletableFuture<String> updatePileFuture = new CompletableFuture<>();

        try {
            Message msg;
            if(reqBody.status == Status.RUNNING){
                msg = new msg_StationRecovery();
            }
            else if(reqBody.status == Status.SHUTDOWN){
                msg = new msg_TurnOffStation();
            }
            else{
                msg = new msg_StationFault();
            }

            Server.MessageQueue.put(msg);
            String result = updatePileFuture.get();
        }


    }
}
