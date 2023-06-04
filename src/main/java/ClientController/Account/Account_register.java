package ClientController.Account;

import Message.msg_UserRegistration;
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


//完成了吧
@WebServlet("/account_register")
public class Account_register extends HttpServlet {

    static class ReqBody {
        String username;
        String password;
        String rePassword;
    }

    static class ResponseMsg {
        int code;
        String message;

        ResponseMsg(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("application/json");
//        resp.getWriter().println("{\n" +
//                "    \"code\": 0,\n" +
//                "    \"message\": \"success\"\n" +
//                "}");
//    }

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

        ReqBody reqBody = gson.fromJson(requestBody, ReqBody.class);

        String username = reqBody.username;
        String password = reqBody.password;
        String rePassword = reqBody.rePassword;

        CompletableFuture<String> future = new CompletableFuture<>();

        msg_UserRegistration msgUserRegistration = new msg_UserRegistration(username, password, future, false);

        try {
            Server.MessageQueue.put(msgUserRegistration);
            String result = future.get();

            int code = 0;
            String message = "success";

            if ( result.equals("false") ) {
                code = -1;
                message = "fail";
            }

            ResponseMsg responseMsg = new ResponseMsg(code,message);
            String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);

            resp.getWriter().println(respJsonMsg);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
