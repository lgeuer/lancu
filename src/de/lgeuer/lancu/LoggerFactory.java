package de.lgeuer.lancu;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class LoggerFactory {
    
    static {
	DOMConfigurator.configure("conf/log4j.xml");
    }
    
    public static Logger getLogger(Class c) {
	
	return Logger.getLogger(c);
	
    }

}
