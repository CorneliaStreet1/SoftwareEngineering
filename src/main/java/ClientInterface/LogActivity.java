package ClientInterface;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Scanner;

public class LogActivity {
    private static class RegisterMsg{
        String username;
        String password;
        String re_password;
    }

    private static class RegisterResponse{
        int code;
        String message;
    }

    private static class LogMsg{
        String username;
        String password;
    }

    private static class LogDataResponse{
        String token;
        boolean is_admin;
    }

    private static class LogResponse{
        int code;
        LogDataResponse data;
        String message;
    }

    private Scanner scanner;
    private ClientActivity clientActivity;

    LogActivity(Scanner scanner){
        this.scanner=scanner;
    }

    private int registerModel(){
        System.out.println("please input your username");
        String username = scanner.nextLine();
        System.out.println("please input your password");
        String password = scanner.nextLine();

        RegisterMsg registerMsg = new RegisterMsg();
        registerMsg.username = username;
        registerMsg.password = password;
        registerMsg.re_password = password;

        Gson gson = new Gson();
        String s = gson.toJson(registerMsg, RegisterMsg.class);

        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post("http://127.0.0.1:8000/account_register")
                    .header("User-Agent", "Apifox/1.0.0 (https://www.apifox.cn)")
                    .header("Content-Type", "application/json")
                    .body(s)
                    .asString();
            System.out.println(response);
            System.out.println(response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int logModel(){
        System.out.println("please input your username");
        String username = scanner.nextLine();
        System.out.println("please input your password");
        String password = scanner.nextLine();

        LogMsg logMsg = new LogMsg();
        logMsg.username = username;
        logMsg.password = password;

        Gson gson = new Gson();
        String s = gson.toJson(logMsg, LogMsg.class);

        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post("http://127.0.0.1:8000/account_register")
                    .header("User-Agent", "Apifox/1.0.0 (https://www.apifox.cn)")
                    .header("Content-Type", "application/json")
                    .body(s)
                    .asString();
            System.out.println(response);
            System.out.println(response.getBody());

            LogResponse logResponse = gson.fromJson(response.getBody(),LogResponse.class);
            int code = logResponse.code;
            if( code != 0 ){
                System.out.println("username or password error");
                return -1;
            }
            String token = logResponse.data.token;
            clientActivity.writeToken(token);
            clientActivity.run();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void run() {
        clientActivity = new ClientActivity(scanner);

        boolean isExit = false;
        while(!isExit){
            System.out.println("LogActivity:");
            System.out.println("1. register");
            System.out.println("2. log");
            System.out.println("9. exit");

            int selectNum = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            switch (selectNum){
                case 1: registerModel(); break;
                case 2: logModel(); break;
                case 9: isExit = true; break;
                default: System.out.println("input error"); break;
            }
        }
    }
}
