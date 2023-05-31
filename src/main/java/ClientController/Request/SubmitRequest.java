package ClientController.Request;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/submit_request")
public class SubmitRequest extends HttpServlet {

    static class ReqBody {
        String charge_mode;

        String require_amount;

        String battery_size;
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

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(req.getReader());
        String line;
        while( ( line = br.readLine()) != null){
            sb.append(line);
        }
        String requestBody = sb.toString();

        Gson gson = new Gson();

        ReqBody reqBody = gson.fromJson(requestBody,ReqBody.class);

        int code = 0;
        String message = "success";

        ResponseMsg responseMsg = new ResponseMsg(code,message);


        String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);

        resp.getWriter().println(respJsonMsg);
    }
}
