package Message;

public class msg_TurnOnStation extends Message{
    public int StationIndex;

    public msg_TurnOnStation(int stationIndex) {
        super("Turn_On_Station");
        StationIndex = stationIndex;
    }
}
