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
        timeBlocks.add(new TimeBlockPrice(0,7,1));
        timeBlocks.add(new TimeBlockPrice(7,12.5, 2));
        timeBlocks.add(new TimeBlockPrice(12.5, 19.8, 3));
    }

    ChargingPriceCount(List<TimeBlockPrice> timeBlocks) {
        this.timeBlocks = timeBlocks;
    }

    double count(double begin, double end) {
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
