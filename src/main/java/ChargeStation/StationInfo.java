package ChargeStation;

public class StationInfo {
    public String UserID;
    public double CarBatteryCapacity;
    public double RequestedChargingCapacity;

    public StationInfo(String userID, double carBatteryCapacity, double requestedChargingCapacity) {
        UserID = userID;
        CarBatteryCapacity = carBatteryCapacity;
        RequestedChargingCapacity = requestedChargingCapacity;
    }
//TODO 排队时长还不知道怎么整，先丢这吧。

}
