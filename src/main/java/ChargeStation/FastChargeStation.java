package ChargeStation;

import Car.Car;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class FastChargeStation extends ChargeStation{
    private static final Logger logger = LogManager.getLogger(FastChargeStation.class);
    public static final double ChargingSpeed = 30.0;
    public static final double ChargingSpeed_PerMinute = 0.5; //0.5度每分钟
    private LocalDateTime Charge_StartTime;//当前正在充电的车，充电的开始时间
    private LocalDateTime Expected_Charge_EndTime;//当前正在充电的车，预期的充电的结束时间
    public FastChargeStation() {
        super();
    }
    public FastChargeStation(int num) {
        super(num);
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

    public synchronized boolean JoinFastStation(Car car) {
        if (car.isFastCharging()) {
            return super.JoinStation(car);
        }
        return false;
    }
    public synchronized double getWaitingTime() {//得到当前充电桩的等待时间
        double time = 0;
        //logger.info("Fast Station Size == " + this.Size());
        for (Car car : this.getCarQueue()) {
            time += car.getRequestedChargingCapacity() / ChargingSpeed;
        }
        //TODO：目前这个方法使用每辆车的充电容量除以充电速度来估计充电时间。但是对于正在充电的车，应该用其剩余充电容量（比如要冲100度，已经充了80度了，用20除以30，而不是100）
        return time;
    }
}
