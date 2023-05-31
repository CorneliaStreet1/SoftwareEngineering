package ClientController.Query;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        int code = 0;
        String message = "success";
        String charge_id = "F7";
        int queue_len = 4;
        CurState cur_state = CurState.NOTCHARGING;
        String place = "WAITINGPLACE";

        RData data = new RData(charge_id, queue_len, cur_state, place);

        ResponseMsg responseMsg = new ResponseMsg(code,message,data);

        Gson gson = new Gson();

        String respJsonMsg = gson.toJson(responseMsg,ResponseMsg.class);

        resp.getWriter().println(respJsonMsg);
    }
}
