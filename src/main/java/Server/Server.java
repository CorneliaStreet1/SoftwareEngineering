package Server;

import Car.Car;
import ChargeStation.*;
import ChargeStation.FastChargeStation;
import ChargeStation.SlowChargeStation;
import Message.*;
import UserManagement.LoginResult;
import UserManagement.User;
import UserManagement.UserManager;
import WaitingZone.WaitingZone;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    /*
    电价单位都是 元/度
    */
    public static BlockingQueue<Message> MessageQueue = new LinkedBlockingQueue<>(); //消息队列。
    private boolean StopServer;
    private static final Logger logger = LogManager.getLogger(Server.class);
    private WaitingZone waitingZone;
    private List<FastChargeStation> FastStations;
    private List<SlowChargeStation> SlowStations;
    private ConcurrentHashMap<Car, ScheduledFuture<?>> CarToTimer;// 正在充电的车 和 他们的定时器任务 之间的映射
    private List<ScheduledExecutorService> FastTimers;//每个快充站的计时器构成的List
    private List<ScheduledExecutorService> SlowTimers; //每个慢充站的计时器构成的的List
    public Server(int FastStationCount, int SlowStationCount) {
        StopServer = false;
        FastStations = new ArrayList<>(FastStationCount);
        SlowStations = new ArrayList<>(SlowStationCount);
        waitingZone = new WaitingZone();
        FastTimers = new ArrayList<>();
        SlowTimers = new ArrayList<>();
        for (int i = 0; i < FastStationCount; i++) {
            FastStations.add(new FastChargeStation());
            FastTimers.add(Executors.newScheduledThreadPool(1));
        }
        for (int i = 0; i < SlowStationCount; i++) {
            SlowStations.add(new SlowChargeStation());
            SlowTimers.add(Executors.newScheduledThreadPool(1));
        }
        CarToTimer = new ConcurrentHashMap<>();
    }
    public boolean CancelCharging_Server(Car car) {//car只需要主键对得上就行
        if (car == null) {
            return false;
        }
            if (waitingZone.contains(car)) {
                return waitingZone.CancelCharging_Waiting(car);
            }else {//如果不在等候区，直接分别遍历快慢队列。
                for (FastChargeStation fastStation : FastStations) {
                    if (fastStation.contains(car)) {//如果快充站包含
                        if (CarToTimer.containsKey(car)) {//如果这辆车正在充电的话，取消其定时任务。凡是取消的车，其数据都不计入充电桩
                            CarToTimer.get(car).cancel(false);
                            CarToTimer.remove(car);
                        }
                        return fastStation.CancelCharging(car);// 将车从队列移除
                    }
                }
                for (SlowChargeStation slowStation : SlowStations) {
                    if (slowStation.contains(car)) {
                        if (CarToTimer.containsKey(car)) {//如果这辆车正在充电的话，取消其定时任务。凡是取消的车，其数据都不计入充电桩
                            CarToTimer.get(car).cancel(false);
                            CarToTimer.remove(car);
                        }
                        return slowStation.CancelCharging(car);
                    }
                }
            }
        return false;
    }
    public boolean ChangeChargeMode_Server(Car car) {
        if (waitingZone.contains(car)) {
            return waitingZone.changeChargeMode_Waiting(car);
        }
        return false;
    }
    public boolean ChangeChargeCapacity_Server(Car car, double val) {
        if (waitingZone.contains(car)) {
            return waitingZone.changeChargeCapacity_Waiting(car, val);
        }
        return false;
    }
    public int CheckSequenceNum_Server(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("NULL Car at CheckSequenceNum_Server");
        }
        if (waitingZone.contains(car)) {
            int i = waitingZone.size();
            for (Car car1 : waitingZone.getFastQueue()) {
                if (car1.equals(car)) {
                    return i;
                }
                i ++;
            }
        }else {
            int F_index = 0;
            for (FastChargeStation fastStation : FastStations) {
                for (Car car1 : fastStation.getCarQueue()) {
                    if (car1.equals(car)) {
                        return F_index;
                    }
                    F_index ++;
                }
            }
            int S_Index = 0;
            for (SlowChargeStation slowStation : SlowStations) {
                for (Car car1 : slowStation.getCarQueue()) {
                    if (car1.equals(car)) {
                        return S_Index;
                    }
                    S_Index ++;
                }
            }
        }
        throw new IllegalArgumentException("Car does NOT EXIST at CheckSequenceNum_Server");
    }
    public void run() {
        this.MessageProcessing();
    }
    public void MessageProcessing() {
        Gson gson = new Gson();
        while (!StopServer) {
            Message message = null;
            try {
                message = MessageQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (message == null) {
                throw new NullPointerException("NULL Message");
            }else {
                switch (message.Type) {
                    case "Authentication":
                        msg_Authentication msgAuthentication = (msg_Authentication) message;
                        User user = UserManager.FindUserInfoByUsrUID(msgAuthentication.UserID);
                        if (user != null) {
                            LoginResult loginResult = new LoginResult(true, user.isAdmin(), user.getUID());
                            msgAuthentication.Result_Json.complete(gson.toJson(loginResult, loginResult.getClass()));
                        }
                    case "Enter_Waiting_Zone":
                        msg_EnterWaitingZone m = (msg_EnterWaitingZone) message;
                        boolean success = waitingZone.JoinWaitingZone(m.UserCar);
                        if (success) {//如果加入成功，尝试一次调度
                            Schedule();
                            m.Result_Json.complete(gson.toJson(true, boolean.class));
                            //TODO 给客户端返回一个加入等候区成功的消息。这个True代表加入等候区成功，不代表其他事情。
                        }else {
                            m.Result_Json.complete(gson.toJson(false, boolean.class));
                        }
                        break;
                    case "Charging_Complete":
                        //TODO 结算费用、将车从对应充电桩的队头移除。从Map移除对应的映射。各种结算和移除就在这里做吧（除了持久化，都做完了）
                        msg_ChargeComplete msgChargeComplete = (msg_ChargeComplete) message;
                        if (msgChargeComplete.isFast) {//将车从充电桩的车队列头移除
                            FastChargeStation fastChargeStation = FastStations.get(msgChargeComplete.StationIndex);
                            Car headCar_F = fastChargeStation.getCarQueue().removeFirst();
                            CarToTimer.remove(headCar_F);
                            LocalDateTime StartTime = fastChargeStation.getCharge_StartTime();
                            LocalDateTime EndTime = fastChargeStation.getExpected_Charge_EndTime();
                            double duration = Duration.between(StartTime, EndTime).toMinutes() * 60;
                            double TotalElectricity = duration * FastChargeStation.ChargingSpeed_PerMinute;
                            double chargeFee = getChargeFee(StartTime, EndTime, 0.5);
                            double ServiceFee = TotalElectricity * ChargeStation.SERVICE_PRICE;
                            fastChargeStation.UpdateStationState(duration, TotalElectricity, chargeFee, ServiceFee);
                            //TODO 将充电详单写入数据库
                            LocalDateTime now = LocalDateTime.now();
                            //生成的订单如下，还没写入数据库
                            ChargingOrderForm form = new ChargingOrderForm(now.toString() + " Car" + headCar_F.getPrimaryKey(), now,
                                    msgChargeComplete.StationIndex, TotalElectricity, StartTime, EndTime, chargeFee, ServiceFee);
                        } else {
                            SlowChargeStation slowChargeStation = SlowStations.get(msgChargeComplete.StationIndex);
                            Car headCar_S = slowChargeStation.getCarQueue().removeFirst();
                            CarToTimer.remove(headCar_S);
                            //TODO 生成充电详单、结算。更新充电桩的统计数据
                            LocalDateTime StartTime = slowChargeStation.getCharge_StartTime();
                            LocalDateTime EndTime = slowChargeStation.getExpected_Charge_EndTime();
                            double duration = Duration.between(StartTime, EndTime).toMinutes() * 60;
                            double TotalElectricity = duration * FastChargeStation.ChargingSpeed_PerMinute;
                            double chargeFee = getChargeFee(StartTime, EndTime, 0.166667);
                            double ServiceFee = TotalElectricity * ChargeStation.SERVICE_PRICE;
                            slowChargeStation.UpdateStationState(duration, TotalElectricity, chargeFee, ServiceFee);
                            //TODO 将充电详单写入数据库
                            LocalDateTime now = LocalDateTime.now();
                            ChargingOrderForm form = new ChargingOrderForm(now.toString() + " Car" + headCar_S.getPrimaryKey(), now,
                                    msgChargeComplete.StationIndex, TotalElectricity, StartTime, EndTime, chargeFee, ServiceFee);
                        }
                        Schedule();
                        break;
                    case "Cancel_Charging":
                        msg_CancelCharging cancelCharging = (msg_CancelCharging) message;
                        boolean cancelChargingServer = CancelCharging_Server(cancelCharging.UserCar);
                        Schedule();
                        //TODO 取消充电，并进行一次充电的调度。将取消的结果返回客户端。最终决定不计算报表(已完成)。
                        cancelCharging.Result_Json.complete(gson.toJson(cancelChargingServer, boolean.class));
                        break;
                    case "Check_Charging_Form":
                        //这个部分应该是从数据库里读取
                        //TODO 读取全部的充电详单并返回
                        break;
                    case "User_Registration":
                        msg_UserRegistration msgUserRegistration = (msg_UserRegistration) message;
                        //TODO 用户注册。并把结果返回客户端
                        boolean Result = UserManager.UserRegistration(msgUserRegistration.UserName, msgUserRegistration.UserPassword, msgUserRegistration.isAdmin);
                        msgUserRegistration.Result_Json.complete(gson.toJson(Result, boolean.class));
                        break;
                    case "User_Login":
                        //TODO 用户登录、并把结果返回客户端
                        msg_UserLogin msgUserLogin = (msg_UserLogin) message;
                        LoginResult loginResult = UserManager.UserLogIn(msgUserLogin.UserName, msgUserLogin.UserPassword);
                        msgUserLogin.Result_Json.complete(gson.toJson(loginResult, loginResult.getClass()));
                        break;
                    case "Change_Charging_Mode":
                        msg_ChangeChargingMode msgChangeChargingMode = (msg_ChangeChargingMode) message;
                        //TODO 改变充电模式。将改变的结果布尔值传递给客户端
                        boolean modeResult = ChangeChargeMode_Server(msgChangeChargingMode.car);
                        break;
                    case "Change_Charge_Capacity":
                        msg_ChangeChargeCapacity msgChangeChargeCapacity = (msg_ChangeChargeCapacity) message;
                        boolean changeCapacityServer = ChangeChargeCapacity_Server(msgChangeChargeCapacity.car, msgChangeChargeCapacity.NewValue);
                        //TODO 改变充电电量.将结果返回给客户端(已做)
                        msgChangeChargeCapacity.Result_Json.complete(gson.toJson(changeCapacityServer, boolean.class));
                        break;
                    case "Turn_On_Station":
                        msg_TurnOnStation msgTurnOnStation = (msg_TurnOnStation) message;
                        if (msgTurnOnStation.StationIndex > 0 && msgTurnOnStation.StationIndex < FastStations.size()) {
                            FastStations.get(msgTurnOnStation.StationIndex).TurnOnStation();
                        } else {
                            SlowStations.get(msgTurnOnStation.StationIndex - FastStations.size()).TurnOnStation();
                        }
                        //TODO 打开充电桩（是否需要传递什么东西给控制器？）
                        break;
                    case "Turn_Off_Station":
                        msg_TurnOffStation msgTurnOffStation = (msg_TurnOffStation) message;
                        if (msgTurnOffStation.StationIndex > 0 && msgTurnOffStation.StationIndex < FastStations.size()) {
                            FastStations.get(msgTurnOffStation.StationIndex).TurnOffStation();
                        } else {
                            SlowStations.get(msgTurnOffStation.StationIndex - FastStations.size()).TurnOffStation();
                        }
                        //TODO 关闭充电桩（是否需要传递什么东西给控制器？）
                        break;
                    case "Check_All_Station_State":
                        List<StationState> stationStates = CheckAllStationState_Server();
                        //TODO 检查全部充电桩的状态。将充电桩的状态返回给客户端(已做)
                        msg_CheckAllStationState msgCheckAllStationState = (msg_CheckAllStationState) message;
                        msgCheckAllStationState.Result_Json.complete(gson.toJson(stationStates, stationStates.getClass()));
                        break;
                    case "Check_Station_Info":
                        msg_CheckStationInfo msgCheckStationInfo = (msg_CheckStationInfo) message;
                        List<StationInfo> stationInfos = CheckStationInfo_Server();
                        //TODO 检查某个充电桩等候服务的车辆信息。将其返回给客户端(已做)
                        msgCheckStationInfo.Result_Json.complete(gson.toJson(stationInfos, stationInfos.getClass()));
                        break;
                    case "Show_Station_Table":
                        msg_ShowStationTable msgShowStationTable = (msg_ShowStationTable) message;
                        //TODO 某个充电桩的报表展示。（做完了）
                        List<StationForm> stationForm = ShowStationTable();
                        msgShowStationTable.Result_Json.complete(gson.toJson(stationForm, stationForm.getClass()));
                        break;
                    case "Station_Recovery":
                        //TODO 充电桩故障恢复
                        break;
                    case "Station_Fault":
                        msg_StationFault msgStationFault = (msg_StationFault) message;
                        ChargeStation station = null;
                        HandleStationError(msgStationFault.StationIndex, msgStationFault.SchedulingStrategy);
                        //TODO 充电桩故障
                        break;
                    case "Preview_queue _situation":
                        msg_PreviewQueueSituation msgPreviewQueueSituation = (msg_PreviewQueueSituation) message;
                        QueueSituation queueSituation = PreviewQueueSituation_Server(msgPreviewQueueSituation.car);
                        msgPreviewQueueSituation.Result_Json.complete(gson.toJson(queueSituation, queueSituation.getClass()));
                    default:
                        throw new IllegalArgumentException("Message Type does NOT Exist");
                }
            }
        }
    }
    public QueueSituation PreviewQueueSituation_Server(Car car) {
        if (car == null) {
            throw new NullPointerException("Null Car At PreviewQueueSituation_Server");
        }
        int charge_id = CheckSequenceNum_Server(car);
        boolean Reached = false;
        int queue_len = 0;
        for (Car car1 : waitingZone.getFastQueue()) {
            queue_len++;
            if (car1.equals(car)) {
                return new QueueSituation(charge_id, queue_len, "WAITINGSTAGE1", "WAITINGPLACE");
            }
        }
        queue_len = 0;
        for (Car car1 : waitingZone.getSlowQueue()) {
            queue_len ++;
            if (car1.equals(car)) {
                return new QueueSituation(charge_id, queue_len, "WAITINGSTAGE1", "WAITINGPLACE");
            }
        }
        int F_index = 0;
        for (FastChargeStation fastStation : FastStations) {
            if (fastStation.isFaulty()) {
                return new QueueSituation(charge_id, queue_len, "FAULTREQUEUE ", "Fast" + F_index);
            }
            if (fastStation.getCarQueue().getFirst().equals(car)) {
                return new QueueSituation(charge_id, 0, "CHARGING", "Fast" + F_index);
            }else if (fastStation.getCarQueue().getLast().equals(car)) {
                return new QueueSituation(charge_id, 1, "WAITINGSTAGE2", "Fast" + F_index);
            }
            F_index ++;
        }
        int S_index = 0;
        for (SlowChargeStation slowStation : SlowStations) {
            if (slowStation.isFaulty()) {
                return new QueueSituation(charge_id, queue_len, "FAULTREQUEUE ", "Slow" + F_index);
            }            if (slowStation.getCarQueue().getFirst().equals(car)) {
                return new QueueSituation(charge_id, 0, "CHARGING", "Slow" + F_index);
            }else if (slowStation.getCarQueue().getLast().equals(car)) {
                return new QueueSituation(charge_id, 1, "WAITINGSTAGE2", "Slow" + F_index);
            }
            S_index ++;
        }
        return new QueueSituation(-1, -1, "NOTCHARGING", null);
    }
    public List<StationForm> ShowStationTable() {
        List<StationForm> re = new ArrayList<>();
        for (int index = 0; index < FastStations.size(); index ++) {
            FastChargeStation station = FastStations.get(index);
            re.add(new StationForm(LocalDateTime.now(), index,station.getAccumulated_Charging_Times(),
                    station.getTotal_Charging_TimeLength(), station.getTotal_ElectricityAmount_Charged(),
                    station.getAccumulated_Charging_Cost(), station.getAccumulated_Service_Cost()));
        }
        for (int index = 0; index < SlowStations.size(); index ++){
            SlowChargeStation station = SlowStations.get(index);
            re.add(new StationForm(LocalDateTime.now(), index + FastStations.size(),station.getAccumulated_Charging_Times(),
                    station.getTotal_Charging_TimeLength(), station.getTotal_ElectricityAmount_Charged(),
                    station.getAccumulated_Charging_Cost(), station.getAccumulated_Service_Cost()));
        }
        return re;
    }
    public List<StationInfo> CheckStationInfo_Server() {
        ArrayList<StationInfo> re = new ArrayList<>();
        int id = 0;
        for (FastChargeStation station : FastStations) {
            for (Car car : station.getCarQueue()) {
                StationInfo stationInfo = new StationInfo(id, String.valueOf(car.getPrimaryKey()), car.getCarBatteryCapacity(), car.getRequestedChargingCapacity(), FastChargeStation.ChargingSpeed_PerMinute);
                re.add(stationInfo);
            }
            id ++;
        }
        for (SlowChargeStation slowStation : SlowStations) {
            for (Car car : slowStation.getCarQueue()) {
                StationInfo stationInfo = new StationInfo(id,String.valueOf(car.getPrimaryKey()), car.getCarBatteryCapacity(), car.getRequestedChargingCapacity(), SlowChargeStation.ChargingSpeed_PerMinute);
                re.add(stationInfo);
            }
            id ++;
        }
        return re;
    }
    public List<StationState>  CheckAllStationState_Server() {
        ArrayList<StationState> states = new ArrayList<>();
        int s_index = 0;
        for (SlowChargeStation slowStation : SlowStations) {
            StationState S_stationState = new StationState(s_index, slowStation.isFaulty(), slowStation.isOnService(), slowStation.getAccumulated_Charging_Times(),
                    slowStation.getTotal_Charging_TimeLength(), slowStation.getTotal_ElectricityAmount_Charged());
            s_index ++;
            states.add(S_stationState);
        }
        int f_index = s_index;
        for (FastChargeStation fastStation : FastStations) {
            StationState F_stationState = new StationState(f_index,fastStation.isFaulty(),  fastStation.isOnService(), fastStation.getAccumulated_Charging_Times(),
                    fastStation.getTotal_Charging_TimeLength(), fastStation.getTotal_ElectricityAmount_Charged());
            states.add(F_stationState);
        }
        return states;
    }
    public void StopServer() {
        StopServer = true;
    }
    public void Schedule() {
        if (!StopServer) {
            //从等候区向各个充电桩的调度
            if (waitingZone.isOnService() && !waitingZone.isEmpty()) {
                /*
                  每次只调度最多一辆慢充车和一辆快充车。
                  我个人觉得这是合理的，因为每调度一辆车，充电桩们的状态就发生了变化，需要重新调度
                  举个例子，一开始两个快充站AB都是空的，我们有两辆快充车。
                  我们选择最短等待时间的充电桩，这种情况下任选一个
                  如果批量调度的话，两辆车可能会被放到同一个快充站。
                  但是如果一个循环调度一辆的话，这一轮第一辆被放到A。第二路，B的等待时间小于A，第二辆车会被丢给B。
                 */
                List<FastChargeStation> fast = new ArrayList<>();
                List<SlowChargeStation> slow = new ArrayList<>();
                for (FastChargeStation fastStation : FastStations) {
                    if (fastStation.hasEmptySlot() && fastStation.isOnService() && (!fastStation.isFaulty())) {
                        fast.add(fastStation);
                    }
                }
                for (SlowChargeStation slowStation : SlowStations) {
                    if (slowStation.hasEmptySlot() && slowStation.isOnService() && (!slowStation.isFaulty())) {
                        slow.add(slowStation);
                    }
                }
                if (!fast.isEmpty()) {//调度一辆快车
                    Deque<Car> fastQueue = waitingZone.getFastQueue();
                    if (!fastQueue.isEmpty()) {
                        int ShortestIndex = 0;
                        for (int i = 0; i < fast.size(); i++) {
                            if (fast.get(i).getWaitingTime() < fast.get(ShortestIndex).getWaitingTime()) {
                                ShortestIndex = i;
                            }
                        }
                        fast.get(ShortestIndex).JoinFastStation(fastQueue.removeFirst());
                    }
                }
                if (!slow.isEmpty()) {//调度一辆慢车
                    Deque<Car> slowQueue = waitingZone.getSlowQueue();
                    if (!slowQueue.isEmpty()) {
                        int ShortestIndex = 0;
                        for (int i = 0; i < slow.size(); i++) {
                            if (slow.get(i).getWaitingTime() < slow.get(ShortestIndex).getWaitingTime()) {
                                ShortestIndex = i;
                            }
                        }
                        slow.get(ShortestIndex).JoinSlowStation(slowQueue.removeFirst());
                    }
                }
            }
            //从等候区到各个充电桩的调度完成
                //接下来遍历每个充电桩，如果桩非空且在运行，就启动对应的定时器
            //快充桩
            for (int i = 0; i < FastStations.size(); i++) {
                    FastChargeStation fastChargeStation = FastStations.get(i);
                    if (fastChargeStation.isOnService() && (!fastChargeStation.isFaulty())) {
                        if (fastChargeStation.Size() > 0) {
                            final int F_Index = i;
                            Car F_headCar = fastChargeStation.getCarQueue().getFirst();
                            if (!CarToTimer.containsKey(F_headCar)) {// 如果headCar还没有被定时，那么启动定时，加入映射关系
                                double F_Time_Hour = F_headCar.getRequestedChargingCapacity() / FastChargeStation.ChargingSpeed;
                                double F_Time_Second = F_Time_Hour * 60 * 60;// 单位:秒
                                fastChargeStation.setCharge_StartTime(LocalDateTime.now());// 充电开始时间
                                fastChargeStation.setExpected_Charge_EndTime(LocalDateTime.now().plusSeconds((long) F_Time_Second));// 充电结束预期时间点
                                ScheduledExecutorService F_scheduledExecutorService = FastTimers.get(i);
                                ScheduledFuture<?> F_schedule = F_scheduledExecutorService.schedule(() -> {
                                    Gson gson = new Gson();//给消息队列发一条消息，说充电完成，是什么类型的桩，是几号桩
                                    msg_ChargeComplete msgChargeComplete = new msg_ChargeComplete(null, F_Index, true);
                                    try {
                                        MessageQueue.put(msgChargeComplete);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }, (long) F_Time_Second, TimeUnit.SECONDS);
                                CarToTimer.put(F_headCar, F_schedule); //将车和其定时器映射关系加入Map
                            }
                        }
                    }
            }
            //慢充桩
                for (int i = 0; i < SlowStations.size(); i++) {
                    SlowChargeStation slowChargeStation = SlowStations.get(i);
                    if (slowChargeStation.isOnService() && (!slowChargeStation.isFaulty())) {
                        if (slowChargeStation.Size() > 0) {
                            final int Index = i;
                            Car headCar = slowChargeStation.getCarQueue().getFirst();
                            if (!CarToTimer.containsKey(headCar)) {// 如果headCar还没有被定时，那么启动定时
                                double Time_Hour = headCar.getRequestedChargingCapacity() / SlowChargeStation.ChargingSpeed;
                                double Time_Second = Time_Hour * 60 * 60;// 单位:秒
                                slowChargeStation.setCharge_StartTime(LocalDateTime.now());// 充电开始时间
                                slowChargeStation.setExpected_Charge_EndTime(LocalDateTime.now().plusSeconds((long) Time_Second));// 充电结束预期时间点
                                ScheduledExecutorService scheduledExecutorService = SlowTimers.get(i);
                                ScheduledFuture<?> schedule = scheduledExecutorService.schedule(() -> {
                                    Gson gson = new Gson();//给消息队列发一条消息，说充电完成，是什么类型的桩，是几号桩
                                    msg_ChargeComplete msgChargeComplete = new msg_ChargeComplete(null, Index, false);
                                    try {
                                        MessageQueue.put(msgChargeComplete);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }, (long) Time_Second, TimeUnit.SECONDS);
                                CarToTimer.put(headCar, schedule); //将车和其定时器映射关系加入Map
                            }
                        }
                    }
                }
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
    public boolean HandleStationError(int StationID, int SchedulingStrategy) {
        ChargeStation ErrorStation = null;
        if (StationID < FastStations.size()) {
            ErrorStation = FastStations.get(StationID);
        }else {
            ErrorStation = SlowStations.get(StationID - FastStations.size());
        }
        ErrorStation.DestroyStation();
        //只考虑单一充电桩故障，并且恰好该充电桩有车排队的情况
        if (SchedulingStrategy == WaitingZone.Priority_Scheduling) {
            waitingZone.StopService();
            Deque<Car> stationError_Cars = ErrorStation.getCarQueue();
            //如果是慢充
            if (ErrorStation.getClass() == SlowChargeStation.class) {
                while (!stationError_Cars.isEmpty()) {
                    Schedule();
                    for (int i = 0; i < SlowStations.size(); i++) {
                        SlowChargeStation s = SlowStations.get(i);
                        if (s.hasEmptySlot() && s.isOnService() && (!s.isFaulty())) {
                            SlowChargeStation slowChargeStation = SlowStations.get(i);
                            slowChargeStation.JoinSlowStation(stationError_Cars.removeFirst());
                        }
                    }
                }
                //快充站
            }else if (ErrorStation.getClass() == FastChargeStation.class){
                while (!stationError_Cars.isEmpty()) {
                    Schedule();
                    for (int i = 0; i < FastStations.size(); i++) {
                        FastChargeStation c = FastStations.get(i);
                        if (c.hasEmptySlot() && c.isOnService() && (!c.isFaulty())) {
                            FastChargeStation fastChargeStation = FastStations.get(i);
                            fastChargeStation.JoinFastStation(stationError_Cars.removeFirst());
                        }
                    }
                }
            }else {
                logger.debug("Error Station Type.Neither Fast Nor Slow");
                throw new IllegalArgumentException("Error Station Type");
            }
            waitingZone.StartService();
        }else if (SchedulingStrategy == WaitingZone.Time_Sequence_Scheduling) {//时间顺序调度
            waitingZone.StopService();
            //慢充站
            if (ErrorStation.getClass() == SlowChargeStation.class) {
                ArrayList<Car> stationError_Cars = new ArrayList<>();
                for (int i = 0; i < SlowStations.size(); i ++) {
                    if (i != StationID - FastStations.size()) {
                        SlowChargeStation slowStation = SlowStations.get(i);
                        Deque<Car> SlowCars = slowStation.getCarQueue();
                        while (SlowCars.size() > 1) {
                            stationError_Cars.add(SlowCars.removeLast());
                        }
                    }
                }
                stationError_Cars.addAll(ErrorStation.getCarQueue());
                stationError_Cars.sort(Car::compareTo);
                while (!stationError_Cars.isEmpty()) {
                    Schedule();
                    for (int i = 0; i < SlowStations.size(); i++) {
                        SlowChargeStation s = SlowStations.get(i);
                        if (s.hasEmptySlot() && s.isOnService() && (!s.isFaulty())) {
                            SlowChargeStation slowChargeStation = SlowStations.get(i);
                            slowChargeStation.JoinSlowStation(stationError_Cars.remove(0));
                        }
                    }
                }
                //快充站
            }else if (ErrorStation.getClass() == FastChargeStation.class) {
                ArrayList<Car> stationError_Cars = new ArrayList<>();
                for (int j = 0; j < FastStations.size(); j ++) {
                    if (j != StationID) {
                        FastChargeStation fastStation = FastStations.get(j);
                        Deque<Car> FastCars = fastStation.getCarQueue();
                        while (FastCars.size() > 1) {
                            stationError_Cars.add(FastCars.removeLast());
                        }
                    }
                }
                stationError_Cars.addAll(ErrorStation.getCarQueue());
                stationError_Cars.sort(Car::compareTo);
                while (!stationError_Cars.isEmpty()) {
                    Schedule();
                    for (int i = 0; i < FastStations.size(); i++) {
                        FastChargeStation c = FastStations.get(i);
                        if (c.hasEmptySlot() && c.isOnService() && (!c.isFaulty())) {
                            FastChargeStation fastChargeStation = FastStations.get(i);
                            fastChargeStation.JoinFastStation(stationError_Cars.remove(0));
                        }
                    }
                }
            }else {
                logger.debug("Error Station Type.Neither Fast Nor Slow");
                throw new IllegalArgumentException("Error Station Type");
            }
            waitingZone.StartService();
        }
        waitingZone.StartService();
        return true;
    }
    public boolean changeChargeCapacity(Car car, double NewVal) {
        return waitingZone.changeChargeCapacity_Waiting(car, NewVal);
    }
    public void HandleStationRecovery(int StationID) {
        waitingZone.StopService();
        if (StationID < FastStations.size()) {
            FastChargeStation fastChargeStation = FastStations.get(StationID);
            if (fastChargeStation != null && fastChargeStation.isOnService() ) {
                return;
            }
            if (fastChargeStation != null) {
                fastChargeStation.FixStation();
                fastChargeStation.getCarQueue().clear();
                Deque<Car> f_car = new ArrayDeque<>();
                for (int i = 0; i < FastStations.size(); i++) {
                    if (i != StationID) {
                        ConcurrentLinkedDeque<Car> carQueue = FastStations.get(i).getCarQueue();
                        while (carQueue.size() > 1) {
                            f_car.addLast(carQueue.removeLast());
                        }
                    }
                }
                while (!f_car.isEmpty()) {
                    Schedule();
                    for (FastChargeStation fastStation : FastStations) {
                        if (fastStation.hasEmptySlot() && fastStation.isOnService() && (!fastStation.isFaulty())) {
                            fastStation.JoinFastStation(f_car.removeFirst());
                        }
                    }
                }
                waitingZone.StartService();
            }
        }else {
            SlowChargeStation slowChargeStation = SlowStations.get(StationID - FastStations.size());
            if (slowChargeStation != null && slowChargeStation.isOnService()) {
                return;
            }
            if (slowChargeStation != null) {
                slowChargeStation.FixStation();
                slowChargeStation.getCarQueue().clear();
                Deque<Car> s_car = new ArrayDeque<Car>();
                for (int i = 0; i < SlowStations.size(); i++) {
                    if (1 != StationID - FastStations.size()) {
                        ConcurrentLinkedDeque<Car> carQueue = SlowStations.get(i).getCarQueue();
                        while (carQueue.size() > 1) {
                            s_car.addLast(carQueue.removeLast());
                        }
                    }
                }
                while (!s_car.isEmpty()) {
                    Schedule();
                    for (SlowChargeStation slowStation : SlowStations) {
                        if (slowStation.hasEmptySlot() && slowStation.isOnService() && (!slowStation.isFaulty())) {
                            slowStation.JoinSlowStation(s_car.removeFirst());
                        }
                    }
                }
                waitingZone.StartService();
            }
        }
    }
    public double getChargeFee(LocalDateTime Start, LocalDateTime End, double ChargeSpeed_Min) {
        LocalTime StartTime = LocalTime.of(Start.getHour(), Start.getMinute());
        LocalTime EndTime = LocalTime.of(End.getHour(), End.getMinute());
        double Ret = 0;
        /*
        * 一天24小时分为：
        * 0:00~7:00低谷 7:01~10:00平时 10:00~15:00高峰 15:00~18:00平时 18:00~21:00高峰 21:00 ~23:00平时 23:00~23:59低谷
        * */
        if (!(EndTime.isBefore(LocalTime.of(7, 0)) || StartTime.isAfter(LocalTime.of(7, 0)))) {
            LocalTime Max = LocalTime.of(7, 0).isAfter(EndTime) ? LocalTime.of(7, 0) : EndTime;
            LocalTime Min = LocalTime.of(0, 0).isBefore(StartTime) ? LocalTime.of(0, 0) : StartTime;
            Ret += 0.4 * ChargeSpeed_Min * (Math.abs(Duration.between(Max, Min).toMinutes()));
        }
        if (!(EndTime.isBefore(LocalTime.of(10, 0)) || StartTime.isAfter(LocalTime.of(10, 0)))) {
            LocalTime Max = LocalTime.of(10, 0).isAfter(EndTime) ? LocalTime.of(10, 0) : EndTime;
            LocalTime Min = LocalTime.of(7, 1).isBefore(StartTime) ? LocalTime.of(7, 1) : StartTime;
            Ret += 0.7 * ChargeSpeed_Min * (Math.abs(Duration.between(Max, Min).toMinutes()));
        }
        if (!(EndTime.isBefore(LocalTime.of(15, 0)) || StartTime.isAfter(LocalTime.of(15, 0)))) {
            LocalTime Max = LocalTime.of(15, 0).isAfter(EndTime) ? LocalTime.of(15, 0) : EndTime;
            LocalTime Min = LocalTime.of(10, 1).isBefore(StartTime) ? LocalTime.of(10, 1) : StartTime;
            Ret += 1.0 * ChargeSpeed_Min * (Math.abs(Duration.between(Max, Min).toMinutes()));
        }
        if (!(EndTime.isBefore(LocalTime.of(18, 0)) || StartTime.isAfter(LocalTime.of(18, 0)))) {
            LocalTime Max = LocalTime.of(18, 0).isAfter(EndTime) ? LocalTime.of(18, 0) : EndTime;
            LocalTime Min = LocalTime.of(15, 1).isBefore(StartTime) ? LocalTime.of(15, 1) : StartTime;
            Ret += 0.7 * ChargeSpeed_Min * (Math.abs(Duration.between(Max, Min).toMinutes()));
        }
        if (!(EndTime.isBefore(LocalTime.of(21, 0)) || StartTime.isAfter(LocalTime.of(21, 0)))) {
            LocalTime Max = LocalTime.of(21, 0).isAfter(EndTime) ? LocalTime.of(21, 0) : EndTime;
            LocalTime Min = LocalTime.of(18, 1).isBefore(StartTime) ? LocalTime.of(18, 1) : StartTime;
            Ret += 1.0 * ChargeSpeed_Min * (Math.abs(Duration.between(Max, Min).toMinutes()));
        }
        if (!(EndTime.isBefore(LocalTime.of(23, 0)) || StartTime.isAfter(LocalTime.of(23, 0)))) {
            LocalTime Max = LocalTime.of(23, 0).isAfter(EndTime) ? LocalTime.of(23, 0) : EndTime;
            LocalTime Min = LocalTime.of(21, 1).isBefore(StartTime) ? LocalTime.of(21, 1) : StartTime;
            Ret += 0.7 * ChargeSpeed_Min * (Math.abs(Duration.between(Max, Min).toMinutes()));
        }
        if (!(EndTime.isBefore(LocalTime.of(23, 59)) || StartTime.isAfter(LocalTime.of(23, 59)))) {
            LocalTime Max = LocalTime.of(23, 59).isAfter(EndTime) ? LocalTime.of(23, 59) : EndTime;
            LocalTime Min = LocalTime.of(23, 1).isBefore(StartTime) ? LocalTime.of(23, 1) : StartTime;
            Ret += 0.4 * ChargeSpeed_Min * (Math.abs(Duration.between(Max, Min).toMinutes()));
        }
        return Ret;
    }
}