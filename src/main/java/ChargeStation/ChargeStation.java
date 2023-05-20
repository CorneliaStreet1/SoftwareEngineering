package ChargeStation;

import Car.Car;

import java.util.ArrayDeque;

public class ChargeStation {
    protected static final int MAX_SIZE = 2;
    protected static final double SERVICE_PRICE = 0.8; //服务费单价0.8元/度
    /*
    电价单位都是 元/度
    */
    protected static final double HIGH_ELECTRICITY_PRICE = 1.0; //高峰时(10:00~15:00,18:00 ~ 21:00)电价
    protected static final double NORMAL_ELECTRICITY_PRICE = 0.7; //平时电价(7:00~10:00.15:00~18:00,21:00~23:00)
    protected static final double LOW_ELECTRICITY_PRICE = 0.4; //低谷时电价(23:00~次日7:00)

    private ArrayDeque<Car> CarQueue;
    private boolean isOnService; //充电桩是否开启。默认是开启。New出一个新充电桩默认是开启状态
    private boolean isFaulty;//充电桩是否故障。默认不故障。
    private int Accumulated_Charging_Times; // 累计充电次数
    private double Total_Charging_TimeLength; // 累计充电总时长
    private double Total_ElectricityAmount_Charged; // 累计充电总电量
    /*
    * 累计总费用，个人认为只需要将下面两个相加就可以了，没必要单独维护一个字段
    * */
    private double Accumulated_Charging_Cost; //累计充电费用（只计算充电的费用）
    private double Accumulated_Service_Cost; //累计服务费用（只计算服务的费用）


    public ChargeStation() {
        CarQueue = new ArrayDeque<>();
        isOnService = true;
        isFaulty = false;
        Accumulated_Charging_Cost = 0;
        Accumulated_Charging_Times = 0;
        Accumulated_Service_Cost = 0;
        Total_Charging_TimeLength = 0;
        Total_ElectricityAmount_Charged = 0;
    }

    public int getAccumulated_Charging_Times() {
        return Accumulated_Charging_Times;
    }

    public void setAccumulated_Charging_Times(int accumulated_Charging_Times) {
        Accumulated_Charging_Times = accumulated_Charging_Times;
    }

    public double getTotal_Charging_TimeLength() {
        return Total_Charging_TimeLength;
    }

    public void setTotal_Charging_TimeLength(double total_Charging_TimeLength) {
        Total_Charging_TimeLength = total_Charging_TimeLength;
    }

    public double getTotal_ElectricityAmount_Charged() {
        return Total_ElectricityAmount_Charged;
    }

    public void setTotal_ElectricityAmount_Charged(double total_ElectricityAmount_Charged) {
        Total_ElectricityAmount_Charged = total_ElectricityAmount_Charged;
    }

    public double getAccumulated_Charging_Cost() {
        return Accumulated_Charging_Cost;
    }

    public void setAccumulated_Charging_Cost(double accumulated_Charging_Cost) {
        Accumulated_Charging_Cost = accumulated_Charging_Cost;
    }

    public double getAccumulated_Service_Cost() {
        return Accumulated_Service_Cost;
    }

    public void setAccumulated_Service_Cost(double accumulated_Service_Cost) {
        Accumulated_Service_Cost = accumulated_Service_Cost;
    }

    public boolean isOnService() {
        return isOnService;
    }
    public void TurnOnStation() {
        isOnService = true;
    }
    public void TurnOffStation() {
        isOnService = false;
    }
    public boolean isFaulty() {
        return isFaulty;
    }
    public void FixStation() {
        isFaulty = false;
    }
    public synchronized boolean JoinStation(Car car) {
        if (CarQueue.size() < MAX_SIZE) {
            CarQueue.addLast(car);
            return true;
        }
        return false;
    }

    public synchronized int Size() {
        return CarQueue.size();
    }
    public boolean CancelCharging(Car car) {
        if (!CarQueue.isEmpty()) {
            if (CarQueue.getFirst().equals(car)) {
                CarQueue.removeFirst();
                //TODO:如果有第二辆车,让等待的第二辆车去充电
            }else {
                CarQueue.removeLast();
            }
            return true;
        }else {
            return false;
        }
    }
    public synchronized boolean hasEmptySlot() {
        return this.Size() < 2;
    }

    public synchronized ArrayDeque<Car> getCarQueue() {
        return CarQueue;
    }
    public void Charging() {
        return;
    }
}
