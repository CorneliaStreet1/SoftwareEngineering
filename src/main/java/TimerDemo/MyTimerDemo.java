package TimerDemo;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MyTimerDemo  {
    Timer timer;

    public MyTimerDemo() {
        timer = new Timer();;
    }

    public void Test() {
        System.out.println("定时任务第一次执行于:" + LocalDateTime.now());
        timer.schedule(new MyTimerTask(), 1000);
    }
    public void D() {
        System.out.println("Outer-----");
    }
    private class MyTimerTask extends TimerTask{
        public void D() {
            System.out.println("---Inner");
        }
        @Override
        public void run() {
            System.out.println("定时任务执行于:" + LocalDateTime.now());
            MyTimerDemo.this.D();
            D();
            timer.schedule(new MyTimerTask(), 1000);
        }
    }

    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }
}
