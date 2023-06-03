package ChargeStation;

public class QueueSituation {
    public int seq;
    public int queue_len;
    public String CurrentState;
    public String CurrentPlace;

    public QueueSituation(int seq, int queue_len, String currentState, String currentPlace) {
        this.seq = seq;
        this.queue_len = queue_len;
        CurrentState = currentState;
        CurrentPlace = currentPlace;
    }
}
