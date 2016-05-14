package dsanchez.ssii.psi5;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil {
	
	private final static Logger LOGGER = Logger.getLogger("ssii-psi5");
	
	private static void configureLogger(){
		try {
			FileHandler fh = new FileHandler("ssii-psi5.log",true);
			SimpleFormatter sf = new SimpleFormatter();
			
			fh.setFormatter(sf);
			
			LOGGER.addHandler(fh);
			
			LOGGER.setLevel(Level.ALL);
		} catch (Throwable oops) {
			oops.printStackTrace();
		}
	}
	
	public static Logger getLogger(){
		configureLogger();
		return LOGGER;
	}

}