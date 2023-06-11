package Server;

import java.util.ArrayList;
import java.util.List;

public class ChargingPriceCount {

    static class TimeBlockPrice {
        double beginTime;
        double endTime;
        double price; // 单位时间价格

        TimeBlockPrice(double beginTime, double endTime, double price) {
            this.beginTime = beginTime;
            this.endTime = endTime;
            this.price = price;
        }
    }

    List<TimeBlockPrice> timeBlocks;

    ChargingPriceCount() {
        timeBlocks = new ArrayList<>();
        timeBlocks.add(new TimeBlockPrice(0,7,12));
        timeBlocks.add(new TimeBlockPrice(7,10, 21));
        timeBlocks.add(new TimeBlockPrice(10, 15, 30));
        timeBlocks.add(new TimeBlockPrice(15, 18, 21));
        timeBlocks.add(new TimeBlockPrice(18, 21, 30));
        timeBlocks.add(new TimeBlockPrice(21, 23, 21));
        timeBlocks.add(new TimeBlockPrice(23, 23.99, 12));
    }
    ChargingPriceCount(boolean isFast) {
        timeBlocks = new ArrayList<>();
        timeBlocks.add(new TimeBlockPrice(0,7,2.8));
        timeBlocks.add(new TimeBlockPrice(7,10, 4.9));
        timeBlocks.add(new TimeBlockPrice(10, 15, 7));
        timeBlocks.add(new TimeBlockPrice(15, 18, 4.9));
        timeBlocks.add(new TimeBlockPrice(18, 21, 7));
        timeBlocks.add(new TimeBlockPrice(21, 23, 4.9));
        timeBlocks.add(new TimeBlockPrice(23, 23.99, 2.8));
    }
    ChargingPriceCount(List<TimeBlockPrice> timeBlocks) {
        this.timeBlocks = timeBlocks;
    }

    double count(double begin, double end) {// 这个函数只能计算一天的费用，且在00:00的分界线需要分成两段处理，所以大于1天的，请直接按一天的计费，最后模天数剩余的小时再用这个函数计算
        if(begin > end){
            return -1;
        }
        double totalPrice = 0;
        for(TimeBlockPrice tPrice : timeBlocks) {
            if(!(end <= tPrice.beginTime || begin >= tPrice.endTime)) {
                double blockBegin = Math.max(tPrice.beginTime, begin);
                double blockEnd = Math.min(tPrice.endTime, end);
                totalPrice += (blockEnd - blockBegin) * tPrice.price;
            }
        }
        //可以用这个记录暂停时间段的钱，然后最后计算没有暂停的时间段，再减去暂停时间段的钱就行
        return totalPrice;
    }

    public static void main(String[] args) {
        ChargingPriceCount chargingPriceCount = new ChargingPriceCount();
        System.out.println(chargingPriceCount.count(0,17));
    }
}
