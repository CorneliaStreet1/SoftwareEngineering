package Message;

public class msg_CheckStationInfo extends Message {
    public int StationIndex;
    public msg_CheckStationInfo(int stationIndex) {
        super("Check_Station_Info");
        StationIndex = stationIndex;
    }
}
