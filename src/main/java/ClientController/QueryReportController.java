package ClientController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/query_report")
public class QueryReportController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.getWriter().println("{\n" +
                "    \"code\": 0,\n" +
                "    \"message\": \"success\",\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"day\": 65,\n" +
                "            \"week\": 9,\n" +
                "            \"month\": 2,\n" +
                "            \"pile_id\": \"P1\",\n" +
                "            \"total_usage_times\": 173,\n" +
                "            \"total_charging_time\": 1200000,\n" +
                "            \"total_charging_amount\": \"1873.25\",\n" +
                "            \"total_charging_earning\": \"2312.12\",\n" +
                "            \"total_service_earning\": \"121.08\",\n" +
                "            \"total_earning\": \"2433.20\"\n" +
                "        }\n" +
                "    ]\n" +
                "}");
    }
}
