package ClientInterface;

import java.util.Scanner;

public class ViewActivity {
    private Scanner scanner;
    private String token;


    ViewActivity(Scanner scanner) {
        this.scanner = scanner;
    }

    public void writeToken(String token){
        this.token = token;
    }


    public void run() {
        boolean isExit = false;
        while(!isExit){
            System.out.println("View activity:");
            System.out.println("3. View charging details");
            System.out.println("4. View queue number");
            System.out.println("5. View waiting number");
            System.out.println("9. back");

            int selectNum = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            switch (selectNum){
                case 1:  break;
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
