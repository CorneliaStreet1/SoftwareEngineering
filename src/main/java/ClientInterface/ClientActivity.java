package ClientInterface;

import java.util.Scanner;

public class ClientActivity {
    private ViewActivity viewActivity;
    private OrderActivity orderActivity;
    private Scanner scanner;
    private String token;

    ClientActivity(Scanner scanner){
        this.scanner = scanner;
    }

    public void writeToken(String token){
        this.token = token;
    }

    public void run() {
        viewActivity = new ViewActivity(scanner);
        orderActivity = new OrderActivity(scanner);
        viewActivity.writeToken(token);
        orderActivity.writeToken(token);

        boolean isExit = false;
        while(!isExit){
            System.out.println("Client activity:");
            System.out.println("1. view detail");
            System.out.println("2. submit or edit order");
            System.out.println("9. exit");

            int selectNum = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            switch (selectNum){
                case 1: viewActivity.run(); break;
                case 2: orderActivity.run(); break;
                case 9: isExit = true; break;
                default: System.out.println("input error"); break;
            }
        }
    }
}
