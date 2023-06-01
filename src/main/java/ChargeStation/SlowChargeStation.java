package ChargeStation;

import Car.Car;
import java.time.LocalDateTime;


public class SlowChargeStation extends ChargeStation{

    public static final double ChargingSpeed = 7.0;
    public static final double ChargingSpeed_PerMinute = 0.1166667; // 7.0 / 60.0
    private LocalDateTime Charge_StartTime;//当前正在充电的车，充电的开始时间
    private LocalDateTime Expected_Charge_EndTime;//当前正在充电的车，预期的充电的结束时间
    public SlowChargeStation() {
        super();
    }

    public synchronized boolean JoinSlowStation(Car car) {
        if (!car.isFastCharging()) {
            return super.JoinStation(car);
        }
        return false;
    }


    public synchronized double getWaitingTime() {
        double time = 0;
        for (Car car : this.getCarQueue()) {
            time += car.getRequestedChargingCapacity() / ChargingSpeed;
        }
        //TODO：目前这个方法使用每辆车的充电容量除以充电速度来估计充电时间。但是对于正在充电的车，应该用其剩余充电容量（比如要冲100度，已经充了80度了，应该用20除以30，而不是1000/30）
        return time;
    }

    public LocalDateTime getCharge_StartTime() {
        return Charge_StartTime;
    }

    public void setCharge_StartTime(LocalDateTime charge_StartTime) {
        Charge_StartTime = charge_StartTime;
    }

    public LocalDateTime getExpected_Charge_EndTime() {
        return Expected_Charge_EndTime;
    }

    public void setExpected_Charge_EndTime(LocalDateTime expected_Charge_EndTime) {
        Expected_Charge_EndTime = expected_Charge_EndTime;
    }
}
