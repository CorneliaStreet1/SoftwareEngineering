package ClientInterface;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

public class GetClass extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 处理GET请求
        String message = request.getParameter("message"); // 获取请求参数message的值
        String result = "okkkkkkk"; // 调用MyService类的方法来处理请求
        // 返回响应
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().println(result);
    }
}
