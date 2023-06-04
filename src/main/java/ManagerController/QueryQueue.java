package ManagerController;

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

        RData(){

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
