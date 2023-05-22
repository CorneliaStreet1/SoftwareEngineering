package ClientInterface;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;


import java.util.Scanner;

public class ClientInterface {
    private static Scanner scanner;

    private static int registerModel(){
        System.out.println("please input your real name");
        String realName = scanner.nextLine();
        System.out.println("please input your password");
        String password = scanner.nextLine();
        
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post("http://127.0.0.1:8000/account_register")
                    .header("User-Agent", "Apifox/1.0.0 (https://www.apifox.cn)")
                    .header("Content-Type", "application/json")
                    .body("{\r\n  \"username\": \"12345678\",\r\n  \"password\": \"87654321\",\r\n  \"re_password\": \"87654321\"\r\n}")
                    .asString();
            System.out.println(response);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        boolean isExit = false;
        while(!isExit){
            System.out.println("Client interface:");
            System.out.println("1. register");
            System.out.println("2. log");
            System.out.println("3. View charging details");
            System.out.println("4. View queue number");
            System.out.println("5. View waiting number");
            System.out.println("6. submit charging request");
            System.out.println("7. modify charging request");
            System.out.println("8. end charging");
            System.out.println("9. exit");

            int selectNum = scanner.nextInt();
            System.out.println();
            switch (selectNum){
                case 1: registerModel(); break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9: isExit = true; break;
                default: System.out.println("input error"); break;
            }
        }
    }
}
