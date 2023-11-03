package sms;

import email.VinrMessageParser;
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


public class TextLocalSMS  implements SMSProvider {

	@Override
	public String sendSMS(HashMap providerParams, String msgId,
			List<TargetUser> numbersList, String textMessage) {
		
        System.out.println("The length of hashmap in Textlocal SMS is  _______ " + providerParams.size());
        try {
            String userId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROVIDER_USERID);
            String password = (String) providerParams.get( VinrMessageParser.PRIMARY_PROIVDER_PASSWORD);
            String senderId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_SID);
            String urlString = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_URL) ;

            String numbersString = new String() ;
            String decryptedUserID = Utils.decrypt( userId ) ;
            String decryptedPassword = Utils.decrypt( password ) ;

            String response = Constants.EMPTY_STRING;
            int count = 1  ;

            for ( TargetUser tUser : numbersList ) {

            StringBuffer personalizedSMS = new StringBuffer( tUser.personalizeMessage(textMessage)) ;
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            System.out.println("TextLocal SMS Message : " + personalizedSMS ) ;            
            String encodedSMS = URLEncoder.encode(personalizedSMS.toString(), "UTF-8");
            
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            String txtLocalParameters = "apikey="+decryptedUserID+"&" ;
            txtLocalParameters += "sender="+senderId+"&" ;
            txtLocalParameters += "numbers="+tUser.getMobileNumber()+"&" ;
            txtLocalParameters += "message="+encodedSMS ;
            System.out.println( txtLocalParameters) ;		
            
            out.write(txtLocalParameters);
            out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    response += inputLine;

                System.out.println("The value of responseId in Text Local CLASS IS ----========------> " + response);                
                in.close();         
            }
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
