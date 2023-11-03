package sms;

import email.VinrMessageParser;
import org.apache.log4j.Logger;
import utils.Constants;
import utils.TargetUser;
import utils.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;


/** This class contains the sendSMS() implementation for 
 *  for SmsCountry
 *
 *  @author Sushma
 *  @date July-08-2010
 *  @version 1.0
 */

public class VinrSMSCountry implements SMSProvider {
    static final Logger logger = Logger.getLogger(VinrSMSCountry.class);
    public String sendSMS(HashMap providerParams, String msgId, List<TargetUser> numbersList, String textMessage) { 
	System.out.println("The length of hashmap in VinrSMSCountry is  _______ " + providerParams.size());
	

        try {
	    String userId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROVIDER_USERID);
	    String password = (String) providerParams.get( VinrMessageParser.PRIMARY_PROIVDER_PASSWORD);
	    String senderId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_SID);
	    String urlString = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_URL) ;
	    
	    String numbersString = new String() ;
	    String decryptedUserID = Utils.decrypt( userId ) ;
	    String decryptedPassword = Utils.decrypt( password ) ;
	    int count = 1  ;
	    
	    StringBuffer completeSMSString = new StringBuffer( "<smscountry>" ).
	    										append("<user>").append(decryptedUserID).append("</user>").        
	    										append("<passwd>").append(decryptedPassword).append("</passwd>") ;
        
	    for ( TargetUser tUser : numbersList ) {
	    	String mobNum = tUser.getMobileNumber() ;
	    	
	    	if ( mobNum != null && mobNum.isEmpty() == false) {
	    	
	    String personalizedSMS = tUser.personalizeMessage(textMessage) ;
	    String replacedMessage = personalizedSMS ; //Utils.replaceCharacterWithSpacesAround( personalizedSMS, '&', 'n') ; 
	    //replacedMessage = Utils.replaceCharacterWithWord( replacedMessage, '@', new String("(at)")) ; 
	    String encodedSMSMessage = URLEncoder.encode( replacedMessage, "UTF-8" ) ;
            StringBuffer finalSMSMessage = new StringBuffer("<SMS>").
                                               append("<to>").append( tUser.getMobileNumber() ).append("</to>").
                                               append("<sid>").append(senderId).append("</sid>").
                                               append("<text>").append(encodedSMSMessage).append("</text>").
                                               append("<DR>Y</DR><MTYPE>N</MTYPE>"). 
                                               append("</SMS>");
    	    completeSMSString.append( finalSMSMessage ) ;
	    	}
	    }
	    completeSMSString.append("</smscountry>") ;
	    
        String response = "";
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        String XML_DATA = "XML_DATA="+completeSMSString.toString() ;
    //  String XML_DATA = "XML_DATA="+URLEncoder.encode(singleSmsToSend);
        System.out.println("Encoded XML Sent :"+XML_DATA);
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(XML_DATA);
        out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                response += inputLine;

            System.out.println("The value of responseId in VinrSMSCountry CLASS IS ----========------> " + response);                
            in.close();         
            //System.out.println("The list of Mobile Nos are :" +toList);
            //System.out.println("response of sendSms routine is : " + response);                     
  	    //return response;                               								

	    return Constants.SUCCESS_STRING;			
        } catch(Exception e) {
            e.printStackTrace();
            return Constants.ERROR_STRING;
        } 
        //return Constants.SUCCESS_STRING;     
    } 
}                  
