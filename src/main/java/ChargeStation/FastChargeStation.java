package ChargeStation;

import Car.Car;

public class FastChargeStation extends ChargeStation{
    public static final int ChargingSpeed = 30;
    public FastChargeStation() {
        super();
    }

    public synchronized boolean JoinFastStation(Car car) {
        if (car.isFastCharging()) {
            return super.JoinStation(car);
        }
        return false;
    }
    public synchronized double getWaitingTime() {//得到当前充电桩的等待时间
        double time = 0;
        for (Car car : super.getCarQueue()) {
            time += car.getRequestedChargingCapacity() / ChargingSpeed;
        }
        //TODO：目前这个方法使用每辆车的充电容量除以充电速度来估计充电时间。但是对于正在充电的车，应该用其剩余充电容量（比如要冲100度，已经充了80度了，用20除以30，而不是100）
        return time;
    }
}
