package Server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServerThread implements ServletContextListener {

    private Thread myThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 在Web应用启动时调用
        System.out.println("Web应用启动");

        // 创建并启动线程
        myThread = new Thread(new MyRunnable());
        myThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 在Web应用关闭时调用
        System.out.println("Web应用关闭");

        // 停止线程
        myThread.interrupt();
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            // 在线程中执行的逻辑
            Server server = new Server(3, 2);
            server.run();
        }
    }
}
