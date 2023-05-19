# SoftwareEngineering
软工
DDL两周,狠狠大干赶英超美

## 调度算法

充电站分为“充电区”和“等候区”两个区域。

电动车到达充电站后首先进入等候区，通过客户端软件向服务器提交充电请求。

服务器根据请求充电模式的不同为客户分配两种类型排队号码:

- 如果是请求“快充”模式，则号码首字母为 F，后续为下类型排队顺序号(从 1开始，如 F1、F2); 
- 如果是请求“慢充”模式，则号码首字母为 T，后续为T类型排队顺序号(从 1开始，如 T1、T2)。

此后，电动车在等候区等待叫号进入充电区。等候区最大车位容量为6。

充电区安装有 2 个快充电桩(A、B)和 3 个慢充电桩(C、D、E)，快充功率为 30 度/小时慢充功率为 7度/小时。



每个充电桩设置有等长的排队队列，长度为 2 个车位(只有第1个车位可充电)。

当任意充电桩队列存在空位时，系统开始叫号，按照排队顺序号“先来先到”的方式，选取等候区与该充电桩模式匹配的一辆车进入充电区(即快充桩对应 F类型号码，慢充桩对应T类型号码)，并按照调度策略加入到匹配充电桩的排队队列中



系统调度策略为: 对应匹配充电模式下(快充/慢充)，被调度车辆完成充电所需时长(等待时间+自己充电时间) 最短。

(等待时间=选定充电桩队列中所有车辆完成充电时间之和;自己充电时间=请求充电量/充电桩功率)

- 例: 快充桩按照 F1>F2 先来先到的顺序进行叫号; 慢充桩按照T1>T2>T3>T4 先来先到的顺序进行叫号。
- 当 F1 被调度时，由于快充桩 A、B 均有空位，它可以分派到这两个队列;同样当 T1 被调度时，它可以分派到慢充桩 D、E 两个队列。
- 它们最终被分配到哪个队列需要按照调度策略，即 F1 完成充电所需时长(等待时间+自己充电时间) 最短，以及 T1 完成充电所需时长 (等待时间+自己充电时间) 最短。

## 目前已有的各个类的说明

### Car

车子类。

这个类目前有的字段有：

```java
private static int nextPrimaryKey = 0;//下一辆车的主键
private boolean isFastCharge; //是否是快充模式
private int QueueSeq; //到达的序号,由等候区负责分配
private int PrimaryKey;//车的主键，相当于车的身份证号
private double ChargingCapacity;//车的充电容量。就是需要充多少电
```

为什么需要 `PrimaryKey`这个字段呢？

- 因为考虑到，我们有从排队的队列中移除某辆指定车的需求（比如从出故障的充电桩的等待队列移除全部的车）。
- 并且实现这些需求的方法的参数，都是传入一个Car类型。
- 所以我们需要某种方式，来定义“参数里传入的那辆车和我们现在打算移除的这辆车是同一辆车”这件事。

### ChargeStation

充电桩（充电站）。这是一个父类，他有两个子类：快充站和慢充站

因为考虑到后期我们还会给充电桩新增很多统计信息，实现充电桩管理的功能，而这些信息应该是快充桩和慢充桩所共有的，所以我们可以将他们抽取出来，然后放在这个父类ChargeStation里面。

- 比如充电桩当前充电队列长度
- 当前剩余等待时间
- 这些是快慢桩都有的



目前就只有两个字段：

```java
    protected static final int MAX_SIZE = 2;//充电桩的等待队列的最大长度。一个充电桩最多两辆车
    private ArrayDeque<Car> CarQueue;//队列本身，默认0号是正在充电的车，1号是正在等待的车
```

大部分的方法可以看名字推知其功能，挑几个说明一下：

- 这个是用于取消正在充电的车的充电的。

```java
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
```

- 当前充电桩是否还有空位。就是说总共两个位置，还有没有空的。

```java
public synchronized boolean hasEmptySlot() {
    return this.Size() < 2;
}
```

#### FastChargeStation

充电桩的子类，快充桩。

基本上没什么字段，直接全部贴上来吧。看注释就行

可能唯一需要强调的一点就是，**不要直接调用父类的JoinStation方法，因为那个方法没判断车的充电类型（而且父类本身逻辑上来说既不是快充也不是慢充，快充车放快充桩，慢充车放慢充桩，但是什么车放充电桩呢？我认为任何车都放充电桩，所以不需要去判断类型，要把判定充电类型的工作放在子类快慢桩）**。

```java
public static final int ChargingSpeed = 30;
public FastChargeStation() {
    super();
}

public synchronized boolean JoinFastStation(Car car) {
    if (car.isFastCharging()) {
        return super.JoinStation(car);
    }
    return false;
}
    public synchronized double getWaitingTime() {//得到当前充电桩的等待时间
        double time = 0;
        for (Car car : super.getCarQueue()) {
            time += car.getChargingCapacity() / ChargingSpeed;
        }
//TODO：目前这个方法使用每辆车的充电容量除以充电速度来估计充电时间。但是对于正在充电的车，应该用其剩余充电容量（比如要冲100度，已经充了80度了，应该用20除以30，而不是1000/30）
        return time;
    }
```

#### SlowChargeStation

慢充桩就不多说了

```java
    public static final int ChargingSpeed = 7;
    public SlowChargeStation() {
        super();
    }

    public synchronized boolean JoinSlowStation(Car car) {
        if (!car.isFastCharging()) {
            return super.JoinStation(car);
        }
        return false;
    }
    public synchronized double getWaitingTime() {
        double time = 0;
        for (Car car : super.getCarQueue()) {
            time += car.getChargingCapacity() / ChargingSpeed;
        }
        //TODO：目前这个方法使用每辆车的充电容量除以充电速度来估计充电时间。但是对于正在充电的车，应该用其剩余充电容量（比如要冲100度，已经充了80度了，应该用20除以30，而不是1000/30）
        return time;
    }
```

### WaitingZone

等候区

这个类目前的字段如下：

- `boolean isOnService`：这个字段的来源是，当充电桩故障时，采用优先级调度或者时间顺序调度策略的时候，**需要先暂停等候区的叫号服务，调度完毕后重新开启服务**。我们将这个字段置为`false`以示等候区服务暂停。
-  `static int TotalCarCount = 0`：这个字段记录了来充电的车次，这个字段实际上是用来给新到来的一辆车分配排队号码的。

```java
private static final int MAX_SIZE = 6;//等候区里最多六辆车
public static final int Priority_Scheduling = 0;//优先级调度
public static final int Time_Sequence_Scheduling = 1;//时间顺序调度
private boolean isOnService;//等候区当前是否提供服务
private ArrayDeque<Car> FastQueue;//快充车的等待队列
private ArrayDeque<Car> SlowQueue;//慢充车的等待队列
private static int TotalCarCount = 0;//充电的总车次数(是车次数，不是车辆数，如果一辆车充一百次，那么车次要加一百)

private int size;//当前等候区的车辆数，不超过6
```

然后挑几个方法说一下：

- `changeChargeMode_Waiting(Car car)`：这个来源于用户请求修改的场景。下划线后面的Waiting代表是处于等待区，改变充电模式。
  - 这里传入的参数不需要“是快充还是慢充”这个参数。因为Car自带当前它是快充还是慢充，反正往相反的改就行了
- `changeChargeCapacity_Waiting(Car car, double NewValue)`：也来源于用户请求修改的场景。改变“这辆车要充多少电”这件事
- `CancelCharging_Waiting(Car car)`：取消充电呗

### Server

服务端。目前只写了服务端调度（等候区叫号等等）、处理充电桩出错。

字段目前就这些。应该不需要解释吧。

```java
private WaitingZone waitingZone;
private List<FastChargeStation> FastStations;
private List<SlowChargeStation> SlowStations;
```

