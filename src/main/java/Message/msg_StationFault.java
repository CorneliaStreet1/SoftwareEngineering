package Message;

public class msg_StationFault extends Message {
    public int StationIndex;

    public msg_StationFault(int stationIndex) {
        super("Station_Fault");
        StationIndex = stationIndex;
    }
}
