package ChargeStation;

import Car.Car;
import Form.ChargingForm;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SlowChargeStation extends ChargeStation{
    public static final double ChargingSpeed = 7;
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
    private boolean isAtHighPoint(LocalDateTime time) {
        //高峰：(10:00~15:00,18:00 ~ 21:00)
        LocalTime Hour_Min = LocalTime.of(time.getHour(), time.getMinute());
        return (Hour_Min.isAfter(ChargeStation.High_Start1) && Hour_Min.isBefore(ChargeStation.High_End1))
                || (Hour_Min.isAfter(ChargeStation.High_Start2) && Hour_Min.isBefore(ChargeStation.High_End2));
    }
    private boolean isAtLowPoint(LocalDateTime time) {
        //低谷期:(23:00~次日7:00)
        LocalTime Hour_Min = LocalTime.of(time.getHour(), time.getMinute());
        if (Hour_Min.isAfter(ChargeStation.Low_Start1)) {//time的小时和分钟 在晚上十一点之后
            return true;
        }else if (Hour_Min.isBefore(ChargeStation.Low_End1)){ //小时和分钟在早上七点之前
            return true;
        }
        return false;
    }
    @Override
    public void Charging() {
        while (!this.getCarQueue().isEmpty()) {
            Car firstCar = this.getCarQueue().getFirst().getDeepCopy(); //getCarQueue()是线程安全的.CarQueue也是线程安全的队列
            Charge_StartTime = LocalDateTime.now();
            double requestedChargingCapacity = firstCar.getRequestedChargingCapacity();
            double ExpectedCharging_Hour = requestedChargingCapacity / ChargingSpeed;//单位:小时
            long ExpectedCharging_Min = (long) (ExpectedCharging_Hour * 60);//单位:分钟
            Expected_Charge_EndTime = Charge_StartTime.plusMinutes(ExpectedCharging_Min);
            double HighTime_Min = 0,NormalTime_Min = 0,LowTime_Min = 0;//充电过程中处于高峰、平常、低谷的总时间。单位：分钟
            double HighTime_Min1 = 0,HighTime_Min2 = 0;//高峰两个时间段分别统计的时间
            double LowTime_Min1 = 0,LowTime_Min2 = 0;//低谷两个时间段分别统计的时间
            double NormalTime_Min1 = 0, NormalTime_Min2 = 0, NormalTime_Min3 = 0;//正常的三个时间段的分别统计的时间

            while (LocalDateTime.now().isBefore(Expected_Charge_EndTime) && firstCar.equals(this.getCarQueue().getFirst())) {
                LocalDateTime Now = LocalDateTime.now();
                LocalTime Hour_Min = LocalTime.of(Now.getHour(), Now.getMinute()); //得到Now的小时和分钟部分
                if (isAtHighPoint(Now)) {//高峰时(10:00~15:00,18:00 ~ 21:00)
                    if (Hour_Min.isAfter(ChargeStation.High_Start1) && Hour_Min.isBefore(ChargeStation.High_End1)) {
                        //10:00 < Hour_Min < 15:00
                        Duration duration = Duration.between(ChargeStation.High_Start1, Hour_Min);
                        HighTime_Min1 = duration.toMinutes();
                    }
                    if (Hour_Min.isAfter(ChargeStation.High_Start2) && Hour_Min.isBefore(ChargeStation.High_End2)) {
                        //18:0 <H_M < 21:0
                        Duration duration = Duration.between(ChargeStation.High_Start2, Hour_Min);
                        HighTime_Min2 = duration.toMinutes();
                    }else {
                        throw new RuntimeException("Time is Not Right");
                    }
                }else if (isAtLowPoint(Now)) {//低谷时电价(23:00~次日7:00)
                    if (Hour_Min.isAfter(ChargeStation.Low_Start1)) { //23:00 < H_M < 23:59
                        LowTime_Min1 = Duration.between(ChargeStation.Low_Start1, Hour_Min).toMinutes();
                    }
                    if (Hour_Min.isBefore(ChargeStation.Low_End1)) { //0:0 < H_M < 7:00
                        LowTime_Min2 = Duration.between(Hour_Min, ChargeStation.Low_End1).toMinutes();
                    }
                }else {
                    //平时电价(7:00~10:00.15:00~18:00,21:00~23:00)
                    if (Hour_Min.isAfter(ChargeStation.Normal_Start1) && Hour_Min.isBefore(ChargeStation.Normal_End1)) {
                        //7:00 < H_M < 10:00
                        NormalTime_Min1 = Duration.between(ChargeStation.Normal_Start1, Hour_Min).toMinutes();
                    }
                    if (Hour_Min.isAfter(ChargeStation.Normal_Start2) && Hour_Min.isBefore(ChargeStation.Normal_End2)) {
                        // 15:0 < H_M < 18:0
                        NormalTime_Min2 = Duration.between(ChargeStation.Normal_Start2, Hour_Min).toMinutes();
                    }
                    if (Hour_Min.isAfter(ChargeStation.Normal_Start3) && Hour_Min.isBefore(ChargeStation.Normal_End3)) {
                        // 21:0 < H_M < 23:0
                        NormalTime_Min3 = Duration.between(ChargeStation.Normal_Start3, Hour_Min).toMinutes();
                    }
                }
                //TODO:更新充电详单的信息，跳出循环后，更新父类的统计信息（已做）。生成一个详单（做了一半。
                // 但是怎么把详单传递给客户端呢？用网络通信？还是直接存到用户对应的数据库表单里面？
                // 更新处于高峰、低谷、正常的时间长度（做了）
            }
            /*
             * 需要生成详单的数据：
             * 详单编号：生成时间 + 车辆ID的形式
             * 详单生成时间
             * 充电桩编号
             * 充电电量
             * 充电时长
             * 启动时间/停止时间
             * 充电费用
             * 服务费用
             * 总费用
             * */
            HighTime_Min = HighTime_Min1 + HighTime_Min2;// 计算两个高峰段的时间之和
            NormalTime_Min = NormalTime_Min1 + NormalTime_Min2 + NormalTime_Min3; //计算三个正常时间段的和
            LowTime_Min = LowTime_Min1 + LowTime_Min2;// 计算两个低谷时间段的和
            LocalDateTime RealEndTime = LocalDateTime.now();//充电结束的时间也是订单的生成时间
            String FORM_ID = RealEndTime.toString() + " CarID = " + firstCar.getPrimaryKey();
            Duration duration = Duration.between(Charge_StartTime, RealEndTime);
            double ChargeTime = (double) duration.toMinutes();
            double TotalElectricity = (HighTime_Min + LowTime_Min + NormalTime_Min) * ChargingSpeed_PerMinute;
            double chargeFee = HighTime_Min * ChargingSpeed_PerMinute * ChargeStation.HIGH_ELECTRICITY_PRICE
                    + NormalTime_Min * ChargingSpeed_PerMinute * ChargeStation.NORMAL_ELECTRICITY_PRICE
                    + LowTime_Min * ChargingSpeed_PerMinute * ChargeStation.LOW_ELECTRICITY_PRICE;
            double ServiceFee = ChargeStation.SERVICE_PRICE * TotalElectricity;
            ChargingForm usrForm = new ChargingForm(FORM_ID, RealEndTime, this.getChargeStationNumber(),
                    TotalElectricity,Charge_StartTime, RealEndTime, chargeFee, ServiceFee, ChargeTime);
            //TODO：将表单写入用户对应的数据库
            /*
             * 需要更改的充电桩的数据：
             * 累计充电次数
             * 累计充电总时长
             * 累计充电总电量
             * 累计充电费用（只计算充电的费用）
             * 累计服务费用（只计算服务的费用）
             * */
            this.setAccumulated_Charging_Times(this.getAccumulated_Charging_Times() + 1);// 累计充电次数
            this.setTotal_Charging_TimeLength(this.getTotal_Charging_TimeLength() + ChargeTime); //累计充电总时长
            this.setTotal_ElectricityAmount_Charged(this.getTotal_ElectricityAmount_Charged() + TotalElectricity); //累计充电总电量
            this.setAccumulated_Charging_Cost(this.getAccumulated_Charging_Cost() + chargeFee); //累计电费
            this.setAccumulated_Service_Cost(this.getAccumulated_Service_Cost() + ServiceFee); //累计服务费
        }
    }

    public static void main(String[] args) {
        LocalDateTime time = LocalDateTime.of(2023,5,21,23,5);
        System.out.println(time);
        LocalTime Hour_Min = LocalTime.of(time.getHour(), time.getMinute());
        System.out.println(Hour_Min);
        LocalDateTime NextDay = time.plusDays(1);//次日和次日七点
        LocalDateTime NextDaySeven = LocalDateTime.of(NextDay.getYear(),
                NextDay.getMonth(), NextDay.getDayOfMonth(), 7, 0);
        System.out.println(NextDay);
        System.out.println(NextDaySeven);
        System.out.println(Hour_Min.isAfter(ChargeStation.Low_Start1) && time.isBefore(NextDaySeven));
        LocalTime Six = LocalTime.of(0,0);//前一天的最后一分钟是23:59分，0:0分是后一天的第一分钟
        System.out.println(Six);
        System.out.println(Six.isBefore(ChargeStation.Low_End1));
        LocalTime Hour_Min1 = LocalTime.of(16,0);
        System.out.println(Hour_Min1.isAfter(ChargeStation.Normal_Start2) && Hour_Min1.isBefore(ChargeStation.Normal_End2));
    }
}
