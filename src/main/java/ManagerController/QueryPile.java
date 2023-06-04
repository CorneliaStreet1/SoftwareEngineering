package ManagerController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class QueryPile extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        {
            "code": 0,
                "message": "success",
                "data": [
            {
                "pile_id": "P1",
                    "status": "RUNNING",
                    "total_usage_times": 53,
                    "total_charging_time": 287218,
                    "total_charging_amount": "2191.32"
            }
    ]
        }
    }
}
