package ClientController.Request;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        Gson gson = new Gson();

        int code = 0;
        String message = "success";

        SubmitRequest.ResponseMsg responseMsg = new SubmitRequest.ResponseMsg(code,message);


        String respJsonMsg = gson.toJson(responseMsg, SubmitRequest.ResponseMsg.class);

        resp.getWriter().println(respJsonMsg);
    }
}
