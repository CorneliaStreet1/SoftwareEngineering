package ClientInterface;

import java.util.Scanner;

public class OrderActivity {
    private Scanner scanner;
    private String token;


    OrderActivity(Scanner scanner) {
        this.scanner = scanner;
    }

    public void writeToken(String token){
        this.token = token;
    }


    private int submitOrderModel() {

        return 0;
    }

    private int modifyOrderModel() {

        return 0;
    }

    private int cancelOrderModel() {

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
