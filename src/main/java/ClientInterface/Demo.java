package ClientInterface;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Demo {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/account_login", new MyHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8000");

//        CompletableFuture<String> future = new CompletableFuture<>();
//        future.complete("os");
//
//
//        Gson gson = new Gson();
//        String s = gson.toJson(future, CompletableFuture.class);
//        System.out.println(s);
//        CompletableFuture<String> temp = gson.fromJson(s,CompletableFuture.class);
//
//
//        Thread threadProcessor = new Thread(new receiver(temp));
//        Thread threadProducor = new Thread( new sender(temp));
//
//        threadProducor.start();
//        Thread.sleep(3000);
//        threadProcessor.start();
    }

    static class sender implements Runnable {
        private CompletableFuture<String> future;

        sender(CompletableFuture<String> future) {
            this.future = future;
        }

        @Override
        public void run() {
            for( int i = 0; i < 10; i++ ) {
                try {
//                    future.complete("sender "+i);
                    String s = future.get();
                    System.out.println("get: "+s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class receiver implements Runnable {
        CompletableFuture<String> future;

        receiver(CompletableFuture<String> future) {
            this.future = future;
        }


        @Override
        public void run() {
            boolean is_run = true;
            int i = 0;
            while(is_run) {
                future.complete("complete"+i);
                System.out.println("receiver complete");
                i++;
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
//            String response = "Hello, World!";
//            t.sendResponseHeaders(200, response.length());
//            OutputStream os = t.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
            // 设置允许跨域访问的域
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            // 设置允许的请求方法
            t.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            // 设置允许的请求头部字段
            t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            // 设置是否允许发送凭据（例如Cookie）
            t.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");

            String method = t.getRequestMethod();
            System.out.println("one:"+method);
            if (method.equals("GET")) {
                System.out.println("get");
                String query = t.getRequestURI().getQuery();
//                String response = "Hello, " + query + "!";
                String response = "{\n" +
                        "    \"code\": 0,\n" +
                        "    \"data\": {\n" +
                        "        \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImppbnVvIiwiaWF0IjoxNTE2MjM5MDIyfQ.WhOxJUL0ZfPW6zrLNdkbQvoE8JObEB_5kr9DkgEVDeE\",\n" +
                        "        \"is_admin\": false\n" +
                        "    },\n" +
                        "    \"message\": \"success\"\n" +
                        "}";

                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else if (method.equals("POST")) {
                System.out.println("post");
                InputStream is = t.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String requestBody = sb.toString();
//                String response = "Hello, " + requestBody + "!";

                String response = "{\n" +
                        "    \"code\": 0,\n" +
                        "    \"data\": {\n" +
                        "        \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImppbnVvIiwiaWF0IjoxNTE2MjM5MDIyfQ.WhOxJUL0ZfPW6zrLNdkbQvoE8JObEB_5kr9DkgEVDeE\",\n" +
                        "        \"is_admin\": false\n" +
                        "    },\n" +
                        "    \"message\": \"success\"\n" +
                        "}";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            else if(method.equals("OPTIONS")){



                String response = "hello world";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
