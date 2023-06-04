package ChargeStation;

/*
*
* 查看各充电桩等候服务的车辆信息
* (用户ID、车辆电池总容量(度).请求充电量(度)、排队时长)
* */
public class StationInfo {
    public int StationID;
    public String UserID;
    public double CarBatteryCapacity;
    public double RequestedChargingCapacity;
    public double WaitingTime;

    public StationInfo(int stationID,String userID, double carBatteryCapacity, double requestedChargingCapacity, double speed_Min) {
        StationID = stationID;
        UserID = userID;
        CarBatteryCapacity = carBatteryCapacity;
        RequestedChargingCapacity = requestedChargingCapacity;
        WaitingTime = (RequestedChargingCapacity / speed_Min) * 60;
    }
}
