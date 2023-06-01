package TimerDemo;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
        ScheduledFuture<?> schedule = scheduledExecutorService.schedule(() -> {
            System.out.println("Hello " + LocalDateTime.now());
        }, 5, TimeUnit.SECONDS);
        ScheduledFuture<?> schedule1 = scheduledExecutorService.schedule(() -> {
            System.out.println(LocalDateTime.now());
        }, 8, TimeUnit.SECONDS);
        schedule.cancel(false);
        System.out.println(LocalDateTime.now());
        scheduledExecutorService.shutdown();
    }
}
