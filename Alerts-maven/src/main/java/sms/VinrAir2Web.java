package sms;

import org.apache.log4j.Logger;
import utils.Constants;
import utils.TargetUser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

public class VinrAir2Web implements SMSProvider {
    static final Logger logger = Logger.getLogger(VinrAir2Web.class);	

    public String sendSMS(HashMap providerParams, String msgId, List<TargetUser> numbersList, String textMessage) {  
        System.out.println("The length of hashmap in VinrAir2Web is  _______ " + providerParams.size());
        try {
		String pcode = (String)	providerParams.get("pcode");
		String acode = (String) providerParams.get("acode");
		String pin = (String) providerParams.get("pin");
		String signature = (String) providerParams.get("signature");	

                String response = Constants.EMPTY_STRING;
                URL url = new URL("http://cosmos.air2web.co.in/failsafe/HttpPublishLink");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);                                                    
                        
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());                        
                String requestParameters = new StringBuffer("pcode=").append(pcode).
                                                     append("&acode=").append(acode).
                                                     append("&mnumber=").append(numbersList).
                                                     append("&message=").append(textMessage).
                                                     append("&pin=").append(pin).
                                                     append("&signature=").append(signature).toString();
                        
                out.write(requestParameters);
                out.flush();                    

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                
        	while((inputLine = in.readLine()) != null)
                    response += inputLine;

                int codeIndex = response.indexOf("code=");
                int codeEndPoint = codeIndex + 11;

                String codeBuf = response.substring(codeIndex, codeEndPoint).trim();
                String[] codeBufSplit = codeBuf.split("=");
          
		System.out.println("The response returned by VinrAir2Web is :::::::: " + codeBufSplit[1]);

                out.close();
                in.close();  
                
		//return codeBufSplit[1];
           // } // end of while
            return Constants.SUCCESS_STRING;	
        } catch(Exception e) {
            e.printStackTrace();
            return Constants.ERROR_STRING;
        }
        //return Constants.SUCCESS_STRING;       
    }
}                
