package Message;

public class msg_StationFault extends Message {
    public int StationIndex;
    public int SchedulingStrategy;
    public msg_StationFault(int stationIndex, int schedulingStrategy) {
        super("Station_Fault");
        StationIndex = stationIndex;
        SchedulingStrategy = schedulingStrategy;
    }
}
