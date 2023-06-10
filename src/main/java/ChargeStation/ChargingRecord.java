package ChargeStation;

import UserManagement.SqlSessionFactoryUtils;
import UserManagement.UserManager;
import mapper.RecordMapper;
import mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import pojo.User;

import java.rmi.server.UID;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/*
* 用户客户端所查看的充电详单
* 至少包括以下信息：
* 详单编号
* 详单生成时间
* 充电桩编号
* 充电电量
* 充电时长
* 启动时间/停止时间
* 充电费用
* 服务费用
* 总费用
* */
public class ChargingRecord {
    private static SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtils.getSqlSessionFactory();


    private String OrderID; //详单编号。生成时间+序号，例如07T18:46:04.0473
    private int Usrid;//用户id(车辆id)
    private String Order_Generation_Time; //详单生成的时间，例如07T18:46:04.047
    private int ChargeStation_ID;//充电桩编号
    private double Total_Electricity_Amount_Charged; //充电电量
    private String StartTime;
    private String EndTime;//充电时长直接用起始时间相减
    private double ElectricityCost; //充电费用
    private double ServiceFee; //服务费
    private double TotalCost;//总花费
    private double ChargeTimeDuration;//单位：秒

    public ChargingRecord(String orderID, int userId, String order_Generation_Time, int chargeStationID,
                          double total_Electricity_Amount_Charged, String startTime, String endTime,
                          double electricityCost, double serviceFee) {
        OrderID = orderID;
        Usrid = userId;
        Order_Generation_Time = order_Generation_Time;
        ChargeStation_ID = chargeStationID;
        Total_Electricity_Amount_Charged = total_Electricity_Amount_Charged;
        StartTime = startTime;
        EndTime = endTime;
        ElectricityCost = electricityCost;
        ServiceFee = serviceFee;
        TotalCost = ServiceFee + ElectricityCost;
        ChargeTimeDuration = (Duration.between(LocalDateTime.parse(startTime),LocalDateTime.parse(endTime)).toMinutes()) * 60;
    }
    public double getChargeTimeDuration() {
        return ChargeTimeDuration;
    }
    public int getUserId() {
        return Usrid;
    }
    public double getTotalCost() {
        return TotalCost;
    }
    public String getOrderId() {
        return OrderID;
    }
    public String getOrderGenerationTime() { return Order_Generation_Time; }
    public int getChargeStationId() {
        return ChargeStation_ID;
    }
    public double getTotalElectricityAmountCharged() {
        return Total_Electricity_Amount_Charged;
    }
    public String getStartTime() { return StartTime; }
    public double getElectricityCost() {
        return ElectricityCost;
    }
    public String getEndTime() { return EndTime; }
    public double getServiceFee() {
        return ServiceFee;
    }
    public void setChargeTimeDuration(double chargeTimeDuration) { ChargeTimeDuration = chargeTimeDuration; }
    public void setUsrid(int usrid) { Usrid = usrid; }
    public void setTotalCost(double totalCost) { TotalCost = totalCost; }
    public void setOrderID(String orderID) { OrderID = orderID; }
    public void setOrder_Generation_Time(String order_Generation_Time) { Order_Generation_Time = order_Generation_Time; }
    public void setChargeStation_ID(int chargeStation_ID) { ChargeStation_ID = chargeStation_ID; }
    public void setTotal_Electricity_Amount_Charged(double total_Electricity_Amount_Charged) { Total_Electricity_Amount_Charged = total_Electricity_Amount_Charged; }
    public void setStartTime(String startTime) { StartTime = startTime; }
    public void setElectricityCost(double electricityCost) { ElectricityCost = electricityCost; }
    public void setEndTime(String endTime) { EndTime = endTime; }
    public void setServiceFee(double serviceFee) { ServiceFee = serviceFee; }

    @Override
    public String toString() {
        return "OrderID: " + OrderID +
                "\nUsrid: " + Usrid +
                "\nOrder_Generation_Time: " + Order_Generation_Time +
                "\nChargeStation_ID: " + ChargeStation_ID +
                "\nTotal_Electricity_Amount_Charged: " + Total_Electricity_Amount_Charged +
                "\nStartTime: " + StartTime +
                "\nEndTime: " + EndTime +
                "\nElectricityCost: " + ElectricityCost +
                "\nServiceFee: " + ServiceFee +
                "\nTotalCost: " + TotalCost +
                "\nChargeTimeDuration: " + ChargeTimeDuration;
    }

    public boolean StoreNewOrder(ChargingRecord chargingRecord) {
        //TODO：当this调用StoreNewOrder的时候，将this这个订单写入持久化层。
        // 存储成功返回True(虽然我目前还没想到怎么会存储失败)
        /*
         * 举例：
         * ChargingOrderForm chargingOrderForm = new ChargingOrderForm();
         * chargingOrderForm.StoreNewOrder()
         * 将会把chargingOrderForm这份订单存入持久化层
         */
        SqlSession sqlSession = sqlSessionFactory.openSession();
        RecordMapper recordMapper = sqlSession.getMapper(RecordMapper.class);
        recordMapper.add(chargingRecord);
        sqlSession.commit();
        sqlSession.close();
        return true;
    }

    /***
     * 给定一个用户名
     * 从数据库中，找到与这个用户名对应的
     * 全部！！是全部！订单
     * 然后以List的形式返回给我
     * @param userName 用户名
     * @return UserName指定的用户，其全部订单
     */
    public static List<ChargingRecord> FindAllOrderByUserName(String userName) {
        //TODO :注意，我记得最开始我让你以字符串的形式来存储日期。但是我这里的日期是LocalDateTime形式的。
        // 所以这需要你来实现:从字符串形式的日期到LocalDateTime，以及从LocalDateTime到字符串形式的日期的转换。
        // 所以在你动手写之前。先看一下src/Test/java的LocalDateTimeDemo.java的Main方法。
        // 之所以不直接在下面写是担心没办法运行起来，但是在LocalDateTimeDemo.java可以借助Junit框架直接运行

        //TODO 完成这个方法。虽然不一定会用到。记得看上面的注释。
        // 如果用户名不存在，那么返回一个null给我(第二个if()做了，可以检查一下有没有问题)
        // 如果userName传进来一个null，也返回一个null给我(已经做了)
        if (userName == null) {
            return null;
        }
        else if (UserManager.FindUserInfoByUsrName(userName) == null) {
            return null;
        }else {
            //TODO 条件成立的情况下，返回给我一个由订单构成的非空List。
            SqlSession sqlSession = sqlSessionFactory.openSession();
            RecordMapper recordMapper = sqlSession.getMapper(RecordMapper.class);
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.selectByUserName(userName);
            List<ChargingRecord> chargingRecords = recordMapper.selectByUserId(user.getUID());
            sqlSession.close();

            return chargingRecords;
        }
    }
    /***
     * 给定一个用户ID(类似用户的QQ号，算了，不类似了，就是用户的账号)
     * 从数据库中，找到与这个UID对应的
     * 全部！！是全部！订单
     * 然后以List的形式返回给我
     * @param UID 用户账号
     * @return UID指定的用户，其全部订单
     */
    public static List<ChargingRecord> FindAllOrderByUID(int UID) {

        //TODO 完成这个方法。虽然不一定会用到。记得看上面的注释。
        // 如果UID不存在，那么返回一个null给我(第二个if()做了，可以检查一下有没有问题)
        // 如果UID <= 0(我记得跟你说过UID从1开始递增?)，也返回一个null给我(已经做了)
        if (UID <= 0) {
            return null;
        }
        else if (UserManager.FindUserInfoByUsrUID(UID) == null) {
            return null;
        }else {
            //TODO 条件成立的情况下，返回给我一个由订单构成的非空List。
            SqlSession sqlSession = sqlSessionFactory.openSession();
            RecordMapper recordMapper = sqlSession.getMapper(RecordMapper.class);
            List<ChargingRecord> chargingRecords = recordMapper.selectByUserId(UID);
            sqlSession.close();

            return chargingRecords;
        }
    }
    //TODO 在完成了这三个方法之后，请顺手测试一下这三个方法。我已经直接在我的代码中调用这三个方法了
}
