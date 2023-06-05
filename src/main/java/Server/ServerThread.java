package Server;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.security.SecureRandom;

public class ServerThread implements ServletContextListener {
    public static SecretKey secretKey;

    private Thread myThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 在Web应用启动时调用
        System.out.println("Web应用启动");

        // 生成256位的随机密钥
        byte[] keyBytes = generateRandomKey(32);

        // 将字节数组转换为SecretKey对象
        secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

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

    private static byte[] generateRandomKey(int keyLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[keyLength];
        secureRandom.nextBytes(keyBytes);
        return keyBytes;
    }
}
