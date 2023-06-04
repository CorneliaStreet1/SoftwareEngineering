package ManagerController;

import Message.msg_CheckStationInfo;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/query_queue")
public class QueryQueue extends HttpServlet {
//这个好像是管理员查看的

    static class RData {
        String pile_id;
        String username;
        String battery_size;
        String require_amount;
        int waiting_time;

        RData(String pile_id, String username, String battery_size,
              String require_amount, int waiting_time){
            this.pile_id = pile_id;
            this.username = username;
            this.battery_size = battery_size;
            this.require_amount = require_amount;
            this.waiting_time = waiting_time;
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

        msg_CheckStationInfo msgCheckStationInfo = new msg_CheckStationInfo();

        int code = 0;
        String message = "success";


        RData[] data = new RData[1];
        data[0] = new RData();
        ResponseMsg responseMsg = new ResponseMsg(code,message,data);

        String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);

        resp.getWriter().println(respJsonMsg);



        resp.getWriter().println("{\n" +
                "    \"code\": 0,\n" +
                "    \"message\": \"success\",\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"pile_id\": \"P1\",\n" +
                "            \"username\": \"12345678\",\n" +
                "            \"battery_size\": \"60.00\",\n" +
                "            \"require_amount\": \"12.34\",\n" +
                "            \"waiting_time\": 600\n" +
                "        }\n" +
                "    ]\n" +
                "}");
    }
}
