import java.util.ArrayList;
import java.util.List;

public class Server {
    private WaitingZone waitingZone;
    private List<FastChargeStation> FastStations;
    private List<SlowChargeStation> SlowStations;

    public Server(int FastStationCount, int SlowStationCount) {
        FastStations = new ArrayList<>(FastStationCount);
        SlowStations = new ArrayList<>(SlowStationCount);
        for (int i = 0; i < FastStationCount; i++) {
            FastStations.add(new FastChargeStation());
        }
        for (int i = 0; i < SlowStationCount; i++) {
            SlowStations.add(new SlowChargeStation());
        }
    }
    public int getFastStationCount() {
        return FastStations.size();
    }
    public int getSlowStationCount() {
        return SlowStations.size();
    }
    public int getStationCount() {
        return getSlowStationCount() + getFastStationCount();
    }
    public boolean HandleStationError(ChargeStation ErrorStation, int SchedulingStrategy) {
        if (SchedulingStrategy == WaitingZone.Priority_Scheduling) {
            waitingZone.StopService();

        }else if (SchedulingStrategy == WaitingZone.Time_Sequence_Scheduling) {

        }
        return false;
    }
    public boolean changeChargeMode(Car car) {
        return waitingZone.changeChargeMode_Waiting(car);
    }
    public boolean changeChargeCapacity(Car car, double NewVal) {
        return waitingZone.changeChargeCapacity_Waiting(car, NewVal);
    }
}
