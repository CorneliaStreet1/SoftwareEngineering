package ChargeStation;

public class StationState {
    public boolean isOnService;
    public int Accumulated_Charging_Times;
    public double Total_Charging_TimeLength;
    public double Total_ElectricityAmount_Charged;

    public StationState(boolean isOnService, int accumulated_Charging_Times, double total_Charging_TimeLength, double total_ElectricityAmount_Charged) {
        this.isOnService = isOnService;
        Accumulated_Charging_Times = accumulated_Charging_Times;
        Total_Charging_TimeLength = total_Charging_TimeLength;
        Total_ElectricityAmount_Charged = total_ElectricityAmount_Charged;
    }
}
