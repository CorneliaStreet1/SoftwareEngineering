import java.util.ArrayDeque;

public class ChargeStation {
    protected static final int MAX_SIZE = 2;
    private ArrayDeque<Car> CarQueue;

    public ChargeStation() {
        CarQueue = new ArrayDeque<>();
    }

    public boolean JoinStation(Car car) {
        if (CarQueue.size() < MAX_SIZE) {
            CarQueue.addLast(car);
            return true;
        }
        return false;
    }

    public synchronized int Size() {
        return CarQueue.size();
    }
    public boolean CancelCharging(Car car) {
        if (!CarQueue.isEmpty()) {
            if (CarQueue.getFirst().equals(car)) {
                CarQueue.removeFirst();
                //TODO:如果有第二辆车,让等待的第二辆车去充电
            }else {
                CarQueue.removeLast();
            }
            return true;
        }else {
            return false;
        }
    }
    public boolean hasEmptySlot() {
        return this.Size() < 2;
    }
}
