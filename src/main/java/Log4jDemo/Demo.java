package Log4jDemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Demo {
    private static Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        logger.debug("Debug");
        logger.info("Info");
        logger.trace("Trace");
        logger.fatal("Fatal");
        logger.warn("Warn");
        logger.error("Error");
        //ALL < TRACE < DEBUG < INFO < WARN < ERROR < FATAL
    }
}
