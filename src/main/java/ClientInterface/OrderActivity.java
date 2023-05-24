package ClientInterface;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Scanner;

public class OrderActivity {
    private Scanner scanner;
    private String url;
    private String token;

    static class Submit_request {
        String charge_mode;
        String  require_amount;
        String battery_size;
    }

    static class Edit_request {
        String charge_mode;
        String  require_amount;
    }

    static class  Msg_response {
        int code;
        String message;
    }

    OrderActivity(Scanner scanner, String url) {
        this.scanner = scanner;
        this.url = url;
    }

    public void writeToken(String token){
        this.token = token;
    }


    private int submitOrderModel() {
        Submit_request submit_request = new Submit_request();
        System.out.print("charge mode: ");
        submit_request.charge_mode = scanner.nextLine();
        System.out.print("require amount: ");
        submit_request.require_amount = scanner.nextLine();
        System.out.print("battery size: ");
        submit_request.battery_size = scanner.nextLine();

        Gson gson = new Gson();
        String msg = gson.toJson(submit_request,Submit_request.class);

        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post(url+"/submit_request")
                    .header("User-Agent", "Apifox/1.0.0 (https://www.apifox.cn)")
                    .header("Content-Type", "application/json")
                    .body(msg)
                    .asString();

            System.out.println(response.getBody());
            Msg_response msg_response = gson.fromJson(response.getBody(),Msg_response.class);
            if ( msg_response.code == 0 ){
                System.out.println("Successfully submitted");
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int modifyOrderModel() {
        Edit_request edit_request = new Edit_request();
        System.out.print("charge mode: ");
        edit_request.charge_mode = scanner.nextLine();
        System.out.print("require amount: ");
        edit_request.require_amount = scanner.nextLine();

        Gson gson = new Gson();
        String msg = gson.toJson(edit_request,Edit_request.class);

        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post(url+"/edit_request")
                    .header("User-Agent", "Apifox/1.0.0 (https://www.apifox.cn)")
                    .header("Content-Type", "application/json")
                    .body(msg)
                    .asString();

            Msg_response msg_response = gson.fromJson(response.getBody(),Msg_response.class);
            if ( msg_response.code == 0 ){
                System.out.println("Successfully modified");
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int cancelOrderModel() {
        Gson gson = new Gson();

        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.get(url+"/end_request")
                    .header("User-Agent", "Apifox/1.0.0 (https://www.apifox.cn)")
                    .asString();

            Msg_response msg_response = gson.fromJson(response.getBody(),Msg_response.class);
            if ( msg_response.code == 0 ){
                System.out.println("Successfully cancelled");
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void run() {
        boolean isExit = false;
        while(!isExit){
            System.out.println("Order activity:");
            System.out.println("1. submit charging order");
            System.out.println("2. modify charging order");
            System.out.println("3. cancel charging order");
            System.out.println("9. back");

            int selectNum = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            switch (selectNum){
                case 1: submitOrderModel(); break;
                case 2: modifyOrderModel(); break;
                case 3: cancelOrderModel(); break;
                case 9: isExit = true; break;
                default: System.out.println("input error"); break;
            }
        }
    }
}
