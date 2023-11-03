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
 *  for VinrUnicel
 *
 *  @author Sunil Tuppale
 *  @date Aug-25-2010
 *  @version 1.0
 */

public class VinrUnicel implements SMSProvider {

    static final Logger logger = Logger.getLogger(VinrUnicel.class);

    public String sendSMS(HashMap providerParams, String msgId, List<TargetUser> recipients, String textMessage) {
    
        System.out.println("The length of hashmap in VinrSMSCountry is  _______ " + providerParams.size());
        
        try {
             String userId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROVIDER_USERID);
             String password = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_PASSWORD);
             String senderId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_SID);
             String urlString = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_URL) ;
             //String id = (String) providerParams.get("id");
     	    String decryptedUserID = Utils.decrypt( userId ) ;
    	    String decryptedPassword = Utils.decrypt( password ) ;


             logger.debug("The value of userId in VinrUnicel is :::::::::::::::::::::::: "  +userId);

             logger.debug("the value of password in VinrUnicel is ******************** "  +password );
             logger.debug("The value of senderId in VinrUnicel is +++++++++++++++++++  "  +senderId);
             logger.debug("The value of message in VinrUnicel is +*+*+*+*+*+*  " + textMessage);

             StringBuffer messageHeader = new StringBuffer("<MESSAGE VER=\"1.2\">").
                                                    append("<USER USERNAME=\"").append(decryptedUserID).append("\"").
                                                    append(" PASSWORD=\"").append(decryptedPassword).append("\"").
                                                    append(" DLR=\"0\"").append(" />");
             StringBuffer completeMessage = messageHeader ;
             
             int count=1 ;
             for (TargetUser toUser:recipients) {
            	 StringBuffer messageBuffer = new StringBuffer( "<SMS TEXT=\"" ) ;
            	 
            	 messageBuffer.append(toUser.personalizeMessage(textMessage)).append("\"").append(" ID=\"").append(count++).append("\" >");
            	 
                 messageBuffer.append("<ADDRESS FROM=\"").append(senderId).append("\"").
                               append(" TO=\"").append(toUser.getMobileNumber().trim()).append("\"").
                               append(" SEQ=\"").append(1).append("\" />");
                 messageBuffer.append("</SMS>" );
                 System.out.println( "The Personalized SMS Text is : " + messageBuffer ) ;
                 completeMessage.append( messageBuffer ) ;
             }
             completeMessage.append( "</MESSAGE>");

             String messagesToSend =  completeMessage.toString();
             String response = "";
             URL url = new URL("http://www.unicel.in/servxml/XML_parse_API.php");
             URLConnection conn = url.openConnection();
             conn.setDoOutput(true);

             System.out.println("XML Request :" + messagesToSend);
             String XML_DATA = "action=send&data=" + URLEncoder.encode(messagesToSend, "UTF-8");
             //  String XML_DATA = "XML_DATA="+URLEncoder.encode(singleSmsToSend);
             //System.out.println("XML PRINTING :"+XML_DATA);

             System.out.println("Encoded XML Request :" + XML_DATA);
             OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
             out.write(XML_DATA);
             out.close();

             BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
             String inputLine;
             while ((inputLine = in.readLine()) != null)
                 response += inputLine;
                
             System.out.println("The value of responseId in VinrSMSCountry CLASS IS ----========------> " + response);
             logger.debug("The value of responseId in VinrSMSCountry CLASS IS ----========------> " + response);
             in.close();
             //System.out.println("The list of Mobile Nos are :" +toList);
             //System.out.println("response of sendSms routine is : " + response);
             //return response;

             return Constants.SUCCESS_STRING;

          } catch(Exception e) {
            e.printStackTrace();
            return Constants.ERROR_STRING;
        }
    }
}
