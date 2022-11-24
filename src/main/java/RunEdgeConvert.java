import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunEdgeConvert {

   private static final Logger logger = LogManager.getLogger("runner." + RunEdgeConvert.class.getName());
   // private static final Logger timeLogger = LogManager.getLogger("timer." + RunEdgeConvert.class.getName());
   public static void main(String[] args) {
      new EdgeConvertGUI();

      logger.debug("An object of EdgeConvertGUI has been created");
      logger.info("GUI has been loaded");
   }
}