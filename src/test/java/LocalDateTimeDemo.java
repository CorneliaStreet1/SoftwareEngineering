import org.junit.Test;

import java.time.LocalDateTime;

public class LocalDateTimeDemo {
    @Test
    public void LocalDateTimeToString() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String s = localDateTime.toString();
        //2023-06-07T18:46:04.047
        System.out.println(s);
    }
    @Test
    public void StringToLocalDateTime() {
        String s = "2023-06-07T18:46:04.047";
        /*
        * 可以看出来，别的不说，至少parse方法，可以将
        * String s = localDateTime.toString()得到的字符串s
        * 还原回原来的LocalDateTime对象
        * 我觉得这样应该够用了？
        * */
        LocalDateTime dateTime = LocalDateTime.parse(s);
        System.out.println(dateTime);
    }
}
