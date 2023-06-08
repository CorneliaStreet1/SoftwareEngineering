package pojo;

public class ChargingRecord {
    private int userId;
    private String orderId;
    private String createTime;
    private float chargedAmount;
    private int chargedTime;
    private String beginTime;
    private String endTime;
    private float chargingCost;
    private float serviceCost;
    private float totalCost;
    private String pileId;

    public ChargingRecord() {
    }

    public ChargingRecord(int userId, String orderId, String createTime, float chargedAmount, int chargedTime, String beginTime, String endTime, float chargingCost, float serviceCost, float totalCost, String pileId) {
        this.userId = userId;
        this.orderId = orderId;
        this.createTime = createTime;
        this.chargedAmount = chargedAmount;
        this.chargedTime = chargedTime;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.chargingCost = chargingCost;
        this.serviceCost = serviceCost;
        this.totalCost = totalCost;
        this.pileId = pileId;
    }

    // Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public float getChargedAmount() {
        return chargedAmount;
    }

    public void setChargedAmount(float chargedAmount) {
        this.chargedAmount = chargedAmount;
    }

    public int getChargedTime() {
        return chargedTime;
    }

    public void setChargedTime(int chargedTime) {
        this.chargedTime = chargedTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public float getChargingCost() {
        return chargingCost;
    }

    public void setChargingCost(float chargingCost) {
        this.chargingCost = chargingCost;
    }

    public float getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(float serviceCost) {
        this.serviceCost = serviceCost;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public String getPileId() {
        return pileId;
    }

    public void setPileId(String pileId) {
        this.pileId = pileId;
    }

    // ToString method

    @Override
    public String toString() {
        return "ChargingRecord{" +
                "userId=" + userId +
                ", orderId='" + orderId + '\'' +
                ", createTime='" + createTime + '\'' +
                ", chargedAmount=" + chargedAmount +
                ", chargedTime=" + chargedTime +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", chargingCost=" + chargingCost +
                ", serviceCost=" + serviceCost +
                ", totalCost=" + totalCost +
                ", pileId='" + pileId + '\'' +
                '}';
    }
}


