import com.google.gson.Gson;
import org.junit.Test;

public class DraftTest {
    @Test
    public void GsonTest() {
        Gson gson = new Gson();
        System.out.println(gson.toJson(null));
        System.out.println();
    }
}
