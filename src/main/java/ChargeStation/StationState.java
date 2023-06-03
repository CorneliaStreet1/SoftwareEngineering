package ChargeStation;

public class StationState {
    public int StationID;
    public boolean isFaulty;//是否故障
    public boolean isOnService;//是否开启
    public int Accumulated_Charging_Times;//累计充电次数
    public double Total_Charging_TimeLength;//充电的累计时长：秒
    public double Total_ElectricityAmount_Charged;

    public StationState(int stationID, boolean isfaulty, boolean isOnService, int accumulated_Charging_Times, double total_Charging_TimeLength, double total_ElectricityAmount_Charged) {
        this.isOnService = isOnService;
        isFaulty = isfaulty;
        StationID = stationID;
        Accumulated_Charging_Times = accumulated_Charging_Times;
        Total_Charging_TimeLength = total_Charging_TimeLength;
        Total_ElectricityAmount_Charged = total_ElectricityAmount_Charged;
    }
}
