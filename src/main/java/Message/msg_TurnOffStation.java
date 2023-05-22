package Message;

public class msg_TurnOffStation extends Message{
    public int StationIndex;
    public msg_TurnOffStation(int stationIndex) {
        super("Turn_Off_Station");
        StationIndex = stationIndex;
    }
}
