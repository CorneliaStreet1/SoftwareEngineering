package Server;

import java.time.LocalDateTime;
import java.util.Scanner;

public class TimeSystem {
    private LocalDateTime currentTime;
    private Integer timeFactor;
    private boolean isRun;
    private Thread thread;
    private Object lock;

    TimeSystem() {
        timeFactor = 1;
        currentTime = LocalDateTime.now();
        isRun = false;
        thread = null;
    }

    public void start() {
        if(thread != null) {
            return;
        }
        isRun = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                int outTime = 0;
                while(isRun) {
                    synchronized (timeFactor) {
                        for(int i = 0; i < timeFactor; i++){
                            plusSecond();
                        }
                    }
//                    outTime++;
//                    if(outTime % 5 == 0) {
//                        System.out.println(currentTime);
//                        outTime = 0;
//                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                thread = null;
            }
        });
        thread.start();
    }

    public void end() {
        isRun = false;
    }

    private void plusSecond() {
        synchronized (currentTime) {
            currentTime = currentTime.plusSeconds(1);
//            System.out.println("part time: " + currentTime);
        }
    }

    public void writeTimeFactor(int newTimeFactor) {
        synchronized (timeFactor) {
            timeFactor = newTimeFactor;
        }
    }

    public LocalDateTime getCurrentTime() {
        synchronized (currentTime){
            return currentTime;
        }
    }

    public void writeCurrentTime(LocalDateTime newTime) {
        synchronized (currentTime) {
            currentTime = newTime;
        }
    }

    public static void main(String[] args) {
        TimeSystem timeSystem = new TimeSystem();

        timeSystem.start();
        timeSystem.writeTimeFactor(5);
        Scanner scanner = new Scanner(System.in);
        int i;
        while(true) {
            i = scanner.nextInt();
            System.out.println("now time: "+ timeSystem.getCurrentTime());
            LocalDateTime setTime = LocalDateTime.now();
            setTime = setTime.withYear(1999);
            setTime = setTime.withMonth(9);
            setTime = setTime.withDayOfMonth(20);
            setTime = setTime.withHour(13);
            setTime = setTime.withMinute(49);
            timeSystem.writeCurrentTime(setTime);
            timeSystem.writeTimeFactor(i);
        }
//        timeSystem.end();
//        System.out.println("finish");
    }


}
