package ManagerController;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        {
            "code": 0,
                "message": "success"
        }

        resp.setContentType("application/json");

        String token = req.getHeader("Authorization");

        Claims claims = Jwts.parser()
                .setSigningKey("secretKey")
                .parseClaimsJws(token)
                .getBody();

        String userIdStr = claims.getSubject();
        int userId = Integer.parseInt(userIdStr);

        Gson gson = new Gson();
    }
}
