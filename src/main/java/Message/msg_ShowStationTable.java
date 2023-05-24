package Message;

public class msg_ShowStationTable extends Message{
    public int StationIndex;
    public msg_ShowStationTable(int stationIndex) {
        super("Show_Station_Table");
        StationIndex = stationIndex;
    }
}
