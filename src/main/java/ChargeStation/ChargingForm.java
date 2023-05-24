package ChargeStation;

import java.time.LocalDateTime;

/*
* 用户客户端所查看的充电详单
* 至少包括以下信息：
* 详单编号
* 详单生成时间
* 充电桩编号
* 充电电量
* 充电时长
* 启动时间/停止时间
* 充电费用
* 服务费用
* 总费用
* */
public class ChargingForm {
    private String FormNumber; //详单编号。目前准备做成生成时间 + 车辆ID的形式
    private LocalDateTime Order_Generation_Time; //详单生成的时间
    private int ChargeStationNumber;//充电桩编号
    private double Total_Electricity_Amount_Charged; //充电电量
    private LocalDateTime StartTime;
    private LocalDateTime EndTime;//充电时长直接用起始时间相减
    private double ElectricityCost; //充电费用
    private double ServiceFee; //服务费
    private double TotalCost;
    private double ChargeTimeDuration;//单位：分钟

    public ChargingForm(String formNumber, LocalDateTime order_Generation_Time, int chargeStationNumber,
                        double total_Electricity_Amount_Charged, LocalDateTime startTime, LocalDateTime endTime,
                        double electricityCost, double serviceFee, double chargeTimeDuration) {
        FormNumber = formNumber;
        Order_Generation_Time = order_Generation_Time;
        ChargeStationNumber = chargeStationNumber;
        Total_Electricity_Amount_Charged = total_Electricity_Amount_Charged;
        StartTime = startTime;
        EndTime = endTime;
        ElectricityCost = electricityCost;
        ServiceFee = serviceFee;
        TotalCost = ServiceFee + ElectricityCost;
        ChargeTimeDuration = chargeTimeDuration;
    }
    public double getChargeTimeDuration() {
        return ChargeTimeDuration;
    }
    public double getTotalCost() {
        return TotalCost;
    }
    public String getFormNumber() {
        return FormNumber;
    }

    public LocalDateTime getOrder_Generation_Time() {
        return Order_Generation_Time;
    }

    public int getChargeStationNumber() {
        return ChargeStationNumber;
    }

    public double getTotal_Electricity_Amount_Charged() {
        return Total_Electricity_Amount_Charged;
    }

    public LocalDateTime getStartTime() {
        return StartTime;
    }

    public LocalDateTime getEndTime() {
        return EndTime;
    }

    public double getElectricityCost() {
        return ElectricityCost;
    }

    public double getServiceFee() {
        return ServiceFee;
    }
}
