package ChargeStation;

import Car.Car;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChargeStation {
    private static Logger logger = LogManager.getLogger(ChargeStation.class);
    protected static final int MAX_SIZE = 2;
    protected static int NextStationNumber; //下一个充电桩的编号
    public static final double SERVICE_PRICE = 0.8; //服务费单价0.8元/度
    private ConcurrentLinkedDeque<Car> CarQueue;
    private final int ChargeStationNumber;//充电桩的编号
    private boolean isOnService; //充电桩是否开启。默认是开启。New出一个新充电桩默认是开启状态
    private boolean isFaulty;//充电桩是否故障。默认不故障。
    private int Accumulated_Charging_Times; // 累计充电次数
    private double Total_Charging_TimeLength; // 累计充电总时长。单位：秒
    private double Total_ElectricityAmount_Charged; // 累计充电总电量
    /*
    * 累计总费用，个人认为只需要将下面两个相加就可以了，没必要单独维护一个字段
    * */
    private double Accumulated_Charging_Cost; //累计充电费用（只计算充电的费用）
    private double Accumulated_Service_Cost; //累计服务费用（只计算服务的费用）


    public ChargeStation() {
        CarQueue = new ConcurrentLinkedDeque<>();
        isOnService = true;
        isFaulty = false;
        ChargeStationNumber = NextStationNumber;
        NextStationNumber ++;
        Accumulated_Charging_Cost = 0;
        Accumulated_Charging_Times = 0;
        Accumulated_Service_Cost = 0;
        Total_Charging_TimeLength = 0;
        Total_ElectricityAmount_Charged = 0;
    }
    public ChargeStation(int chargeStationNumber) {
        CarQueue = new ConcurrentLinkedDeque<>();
        isOnService = true;
        isFaulty = false;
        ChargeStationNumber = chargeStationNumber;
        Accumulated_Charging_Cost = 0;
        Accumulated_Charging_Times = 0;
        Accumulated_Service_Cost = 0;
        Total_Charging_TimeLength = 0;
        Total_ElectricityAmount_Charged = 0;
    }
/************************************从这里开始，到下面的分割线结束*****************************************/
    /*夹在这两个分割线之间的方法以及对应的资源，都只有充电桩本身那个线程可能会调用和访问（资源独享），所以不需要synchronized */
    public int getChargeStationNumber() {
        return ChargeStationNumber;
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
    public boolean contains(Car car) {
        return CarQueue.contains(car);
    }
/***************************************分割线结束***********************************************************/
    public synchronized boolean isOnService() {
        return isOnService && !isFaulty;
    }
    public synchronized void TurnOnStation() {
        isOnService = true;
    }
    public void TurnOffStation() {
        isOnService = false;
    }
    public boolean isFaulty() {
        return isFaulty && !isOnService;
    }
    public void SetFaulty(boolean b) {
        isFaulty = b;
    }
    public void FixStation() {
        isOnService = true;
        isFaulty = false;
    }
    public void DestroyStation() {
        isOnService = false;
        isFaulty = true;
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
    public Car getCar(Car car) {
        if (car == null) {
            return null;
        }
        if (!CarQueue.isEmpty()) {
            if (CarQueue.getFirst().equals(car)) {
                return CarQueue.getFirst();
            }else if (CarQueue.getLast().equals(car)) {
                return CarQueue.getLast();
            }
        }
        return null;
    }
    public synchronized boolean CancelCharging(Car car) {
        if (car == null) {
            return false;
        }
        if (!CarQueue.isEmpty()) {
            if (CarQueue.getFirst().equals(car)) {
                CarQueue.removeFirst();
                logger.info("CancelCharging(): Remove Charging Car");
                return true;
                //TODO 结算各种。虽然是不收费，但是还是要生成一张详单才行（决定了：不结算了）
            }else if (CarQueue.getLast().equals(car)){
                CarQueue.removeLast();
                logger.info("CancelCharging(): Remove Waiting Car At Station");
                return true;
            }
        }
        return false;
    }
    public synchronized boolean hasEmptySlot() {
        return this.Size() < 2;
    }

    public synchronized ConcurrentLinkedDeque<Car> getCarQueue() {
        /*
        * 同一个充电桩的这个方法，Server线程也会调用，充电桩本身的线程也会调用。所以需要是同步的
        * */
        return CarQueue;
    }
    @Deprecated
    public void Charging() {
        throw new UnsupportedOperationException("Should NOT Call Parent's Method");
    }
    public void CompleteCharge() {
        throw new UnsupportedOperationException("Should NOT Call Parent's Method");
    }
    public void UpdateStationState(double ChargeTime, double TotalElectricity, double chargeFee, double ServiceFee) {
        /*
         * 需要更改的充电桩的数据：
         * 累计充电次数
         * 累计充电总时长
         * 累计充电总电量
         * 累计充电费用（只计算充电的费用）
         * 累计服务费用（只计算服务的费用）
         * */
        logger.info("START========UpdateStationState");
        logger.info("ChargeTime " + ChargeTime + "TotalElectricity " + TotalElectricity + "chargeFee \n" + chargeFee + "ServiceFee " +  ServiceFee);
        this.setAccumulated_Charging_Times(this.getAccumulated_Charging_Times() + 1);// 累计充电次数
        this.setTotal_Charging_TimeLength(this.getTotal_Charging_TimeLength() + ChargeTime); //累计充电总时长
        this.setTotal_ElectricityAmount_Charged(this.getTotal_ElectricityAmount_Charged() + TotalElectricity); //累计充电总电量
        this.setAccumulated_Charging_Cost(this.getAccumulated_Charging_Cost() + chargeFee); //累计电费
        this.setAccumulated_Service_Cost(this.getAccumulated_Service_Cost() + ServiceFee); //累计服务费
        logger.info("END========UpdateStationState");
    }
}
