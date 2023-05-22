package Message;

public class msg_StationRecovery extends Message{
    public int StationIndex;
    public msg_StationRecovery(int stationIndex) {
        super("Station_Recovery");
        StationIndex = stationIndex;
    }
}
