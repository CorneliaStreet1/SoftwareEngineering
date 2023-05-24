package ChargeStation;

import java.time.LocalDateTime;

/*
* 管理员客户端的报表展示:
* 时间
* 充电桩编号
* 累计充电次数
* 累计充电时长
* 累计充电量
* 累计充电费用
* 累计服务费用
* 累计总费用
* */
public class StationForm {
    public LocalDateTime time;
    public int StationIndex;
    public int Accumulated_Charging_Times; // 累计充电次数
    public double Total_Charging_TimeLength; // 累计充电总时长
    public double Total_ElectricityAmount_Charged; // 累计充电总电量
    /*
     * 累计总费用，个人认为只需要将下面两个相加就可以了，没必要单独维护一个字段
     * */
    public double Accumulated_Charging_Cost; //累计充电费用（只计算充电的费用）
    public double Accumulated_Service_Cost; //累计服务费用（只计算服务的费用）
    public double Total_Cost;
    public StationForm(LocalDateTime time, int stationIndex, int accumulated_Charging_Times, double total_Charging_TimeLength,
                       double total_ElectricityAmount_Charged, double accumulated_Charging_Cost, double accumulated_Service_Cost) {
        this.time = time;
        StationIndex = stationIndex;
        Accumulated_Charging_Times = accumulated_Charging_Times;
        Total_Charging_TimeLength = total_Charging_TimeLength;
        Total_ElectricityAmount_Charged = total_ElectricityAmount_Charged;
        Accumulated_Charging_Cost = accumulated_Charging_Cost;
        Accumulated_Service_Cost = accumulated_Service_Cost;
        Total_Cost = Accumulated_Charging_Cost + Accumulated_Service_Cost;
    }
}
