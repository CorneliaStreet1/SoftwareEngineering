package ChargeStation;

import Car.Car;

import java.time.LocalDateTime;

public class SlowChargeStation extends ChargeStation{
    public static final int ChargingSpeed = 7;
    private LocalDateTime Charge_StartTime;//当前正在充电的车，充电的开始时间
    private LocalDateTime Charge_EndTime;//当前正在充电的车，充电的结束时间
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
        for (Car car : super.getCarQueue()) {
            time += car.getRequestedChargingCapacity() / ChargingSpeed;
        }
        //TODO：目前这个方法使用每辆车的充电容量除以充电速度来估计充电时间。但是对于正在充电的车，应该用其剩余充电容量（比如要冲100度，已经充了80度了，应该用20除以30，而不是1000/30）
        return time;
    }

    @Override
    public void Charging() {
        /*
        * 需要更改的充电桩数据：
        * 累计充电次数
        * 累计充电总时长
        * 累计充电总电量
        * 累计充电费用（只计算充电的费用）
        * 累计服务费用（只计算服务的费用）
        * */
        while (!super.getCarQueue().isEmpty()) {
            Car firstCar = super.getCarQueue().getFirst();
            Charge_StartTime = LocalDateTime.now();
            double requestedChargingCapacity = firstCar.getRequestedChargingCapacity();
            double ExpectedCharging_Hour = requestedChargingCapacity / ChargingSpeed;//单位:小时
            long ExpectedCharging_Min = (long) (ExpectedCharging_Hour * 60);
            Charge_EndTime = Charge_StartTime.plusMinutes(ExpectedCharging_Min);
            while (LocalDateTime.now().isBefore(Charge_EndTime)) {
                //TODO:更新充电详单的信息，跳出循环后，更新父类的统计信息。生成一个详单。
                // 但是怎么把详单传递给客户端呢？用网络通信？
            }
        }
    }
}
