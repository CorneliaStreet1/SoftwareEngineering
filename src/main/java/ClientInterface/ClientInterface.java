package ClientInterface;
import com.google.gson.Gson;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;


import java.util.Scanner;

public class ClientInterface {
//    private static Scanner scanner;
//    private static LogActivity logActivity;

    public static void main(String[] args) {
        String url = "http://127.0.0.1:8000";
        Scanner scanner = new Scanner(System.in);
        LogActivity logActivity = new LogActivity(scanner,url);
        logActivity.run();
    }
}
