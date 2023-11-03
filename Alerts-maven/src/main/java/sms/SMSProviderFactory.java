package sms;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.util.Properties;

/** This is the Factory class that is the main engine of the
 *  Factory pattern.  It has two methods.  One method reads
 *  the class name of the Provider from a proerties file
 *  and the other creates an instance of that class using
 *  reflection and returns the instance to the calling class
 *  @author Sushma
 *  @date July-08-2010
 *  @version 1.0
 */

public class SMSProviderFactory {
    static final Logger logger = Logger.getLogger(SMSProviderFactory.class);
 
    /** This method calls the getProviderClassName() method to get the
     *  name of the class from a particular service provider and returns
     *  an instance of the class after creating it using reflection
     */
    
    public static String getSMSProviderURL( String smsProviderCode ) {

    	String smsProviderURL = null;
        try {
            String fileName = System.getenv("SmsProvider_CONFIG_FILE");
            if (fileName == null) {
                System.out.println("Env.Variable SmsProvider_CONFIG_FILE not set,use default file vinralerts.properties");
                fileName="vinralerts.properties" ;
            }

            Properties p = new Properties(System.getProperties());	    
            FileInputStream propFile = new FileInputStream(fileName);
            p.load(propFile);

            smsProviderURL = p.getProperty(smsProviderCode.toUpperCase() + "_URL");
            if (smsProviderURL == null || (smsProviderURL !=null && smsProviderURL.isEmpty() == true) ) {
            	return null ;
            }
            logger.debug("In getProviderClassName of SmsProviderFactory, the value of class is      :"   +smsProviderURL);

        } catch(Exception e) {
            e.printStackTrace();
        }
        logger.debug("The value of class outside try is        :"    +smsProviderURL );
	return smsProviderURL ;
    	
    }
    
    public static SMSProvider getSMSProviderInstance(String smsProviderCode) {
    
        SMSProvider provider = null;
        try {
            logger.debug("In SmsProviderFactory Class. The value of provider code is        :"     + smsProviderCode);
            String name = getProviderClassName(smsProviderCode);
            logger.debug("The value of className is     :  "      +name);
            System.out.println("The value of className is     :  "      +name);
            
            // Using reflection to instantiate the provider class at run time
            Class c = Class.forName(name);
	    logger.debug("The value of className is     :  "      +c);
			
	    provider = (SMSProvider)c.newInstance();
			
	    logger.debug("In SMSProvider class, the value of classInstance is        : "        +provider);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return provider;			
    }

    /**  This method returns the name of a class after reading it from the properties file
     */
    public static String getProviderClassName(String providerCode) {
        
        String smsClassName = null;
        try {
            String fileName = System.getenv("SmsProvider_CONFIG_FILE");
            if (fileName == null) {
                System.out.println("Env.Variable SmsProvider_CONFIG_FILE not set,use default file vinralerts.properties");
                fileName="vinralerts.properties" ;
            }

            Properties p = new Properties(System.getProperties());	    
            FileInputStream propFile = new FileInputStream(fileName);
	    p.load(propFile);

            smsClassName = p.getProperty(providerCode.toUpperCase());
            logger.debug("In getProviderClassName of SmsProviderFactory, the value of class is      :"   +smsClassName);

        } catch(Exception e) {
            e.printStackTrace();
        }
        logger.debug("The value of class outside try is        :"    +smsClassName);
	return smsClassName;

    }
    
}	
