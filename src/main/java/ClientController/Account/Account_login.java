package ClientController.Account;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/account_login")
public class Account_login extends HttpServlet {

    static class ReqBody {
        String username;
        String password;
    }

    static class RData {
        String token;
        boolean is_admin;

        RData(String token, boolean is_admin) {
            this.is_admin = is_admin;
            this.token = token;
        }
    }

    static class ResponseMsg {
        int code;
        RData data;
        String message;

        ResponseMsg(int code, RData data, String message){
            this.code = code;
            this.data = data;
            this.message = message;
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.getWriter().println(req.getHeader("Accept")+req.getHeader("justAJoke")+"\n{\n" +
                "    \"code\": 0,\n" +
                "    \"data\": {\n" +
                "        \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImppbnVvIiwiaWF0IjoxNTE2MjM5MDIyfQ.WhOxJUL0ZfPW6zrLNdkbQvoE8JObEB_5kr9DkgEVDeE\",\n" +
                "        \"is_admin\": false\n" +
                "    },\n" +
                "    \"message\": \"success\"\n" +
                "}");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(req.getReader());
        String line;
        while( ( line = br.readLine()) != null){
            sb.append(line);
        }
        String requestBody = sb.toString();

        Gson gson = new Gson();

        ReqBody reqBody = gson.fromJson(requestBody,ReqBody.class);

        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImppbnVvIiwiaWF0IjoxNTE2MjM5MDIyfQ.WhOxJUL0ZfPW6zrLNdkbQvoE8JObEB_5kr9DkgEVDeE";

        ResponseMsg responseMsg = new ResponseMsg(0,new RData(token,false),"success");
        String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);

//        resp.getWriter().println(req.getHeader("Accept")+"\n"
//                +req.getHeader("justAJoke")+"\n"
//                +requestBody+"\n"
//                +"response:\n"
//                +"{\n" +
//                "    \"code\": 0,\n" +
//                "    \"data\": {\n" +
//                "        \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImppbnVvIiwiaWF0IjoxNTE2MjM5MDIyfQ.WhOxJUL0ZfPW6zrLNdkbQvoE8JObEB_5kr9DkgEVDeE\",\n" +
//                "        \"is_admin\": false\n" +
//                "    },\n" +
//                "    \"message\": \"success\"\n" +
//                "}");
        resp.getWriter().println(req.getHeader("Accept")+"\n"
                +req.getHeader("justAJoke")+"\n"
                +requestBody+"\n"
                +respJsonMsg);
    }
}
