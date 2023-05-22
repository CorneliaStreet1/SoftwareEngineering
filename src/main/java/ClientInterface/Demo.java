package ClientInterface;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;



public class Demo {

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8000);
        tomcat.addWebapp("", "ClientInterface");
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        tomcat.getServer().await();
    }
}
