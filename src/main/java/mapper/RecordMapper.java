package mapper;

import ChargeStation.ChargingRecord;

import java.util.List;


public interface RecordMapper {

    //查看所有user
    List<ChargingRecord> selectALLRecord();

    //根据username查询
    //User ChargingRecord(String userName);

    //根据userid查询
    List<ChargingRecord> selectByUserId(int userId);

    //添加user
    void add(ChargingRecord chargingRecord);
}
