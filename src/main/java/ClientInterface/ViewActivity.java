package ClientInterface;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Scanner;

public class ViewActivity {
    private Scanner scanner;
    private String url;
    private String token;


    ViewActivity(Scanner scanner, String url) {
        this.scanner = scanner;
        this.url = url;
    }

    public void writeToken(String token){
        this.token = token;
    }


    private int view_details() {
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.get(url+"/query_order")
                    .header("User-Agent", "Apifox/1.0.0 (https://www.apifox.cn)")
                    .asString();
            System.out.println(response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int view_queue() {
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.get(url+"/preview_queue")
                    .header("User-Agent", "Apifox/1.0.0 (https://www.apifox.cn)")
                    .asString();
            System.out.println(response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void run() {
        boolean isExit = false;
        while(!isExit){
            System.out.println("View activity:");
            System.out.println("1. View charging details of order");
            System.out.println("2. View queue");
//            System.out.println("5. View waiting number");
            System.out.println("9. back");

            int selectNum = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            switch (selectNum){
                case 1: view_details(); break;
                case 2: view_queue(); break;
                case 9: isExit = true; break;
                default: System.out.println("input error"); break;
            }
        }
    }
}
