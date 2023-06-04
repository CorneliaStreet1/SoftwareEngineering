package ClientController.Account;

import Message.msg_UserLogin;
import Server.Server;

import UserManagement.LoginResult;
import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//完成了吧
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

        String username = reqBody.username;
        String password = reqBody.password;

        CompletableFuture<String> future = new CompletableFuture<>();

        msg_UserLogin msgUserLogin = new msg_UserLogin(username, password, future);

        try {
            Server.MessageQueue.put(msgUserLogin);
            String result = future.get();
            LoginResult loginResult = gson.fromJson(result, LoginResult.class);

            int code = 0;
            String message = "success";
            RData data = null;

            if (loginResult.Login_Success) {
                int userId = loginResult.User_ID;
                boolean is_admin = loginResult.isAdmin;

                String secretKey = "secretKey";
                String token = Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .signWith(SignatureAlgorithm.HS256, secretKey)
                        .compact();

                data = new RData(token, is_admin);
            }
            else {
                code = -1;
                message = "user name or password error";
            }

//            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImppbnVvIiwiaWF0IjoxNTE2MjM5MDIyfQ.WhOxJUL0ZfPW6zrLNdkbQvoE8JObEB_5kr9DkgEVDeE";

            ResponseMsg responseMsg = new ResponseMsg(code, data, message);
            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
