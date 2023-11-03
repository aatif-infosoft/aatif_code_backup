package email;

import org.apache.log4j.Logger;
import utils.Constants;
import utils.TargetUser;


import jakarta.activation.DataHandler;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**  This class handles dispatching both plain emails and 
 *   emails with attachment.  It has two static methods
 *   to handle each
 *   @author Sushma P
 *   @date July-07-2010
 *   @version 1.0
 */

public class VinrEmailDispatcher {
     static final Logger logger = Logger.getLogger(VinrEmailDispatcher.class);

     /** A static method to send a plain email without any attachments
       * which returns the string "success" if mail is successfully sent
       * or "exception" if there was a problem
       * 
       * 
       * This is DEPRECATED
       */

     
     public static String sendPersonalPlainMail(String subject, String body, String fromAddress, List<TargetUser> toAddress , String ccAddressString, int messageID, boolean bodyType) throws Exception {   
         try {
             logger.debug( "The subject in VinrEmailDispatcher class is :::=====>  " + subject );
             logger.debug( "The body in VinrEmailDispatcher class is *+**+*+*+*+*+*+ " + body) ;
             logger.debug("The from Address in VinrEmailDispatcher class is:: >"+ fromAddress);
             logger.debug("The targetUser in VinrEmailDispatcher class is:: >"+ toAddress);
             logger.debug("The from CC Address in VinrEmailDispatcher class is:: >"+ ccAddressString);
             logger.debug("The from MessageId in VinrEmailDispatcher class is:: >"+ messageID);
             logger.debug("The from BodyType in VinrEmailDispatcher class is:: >"+ bodyType);
             Properties props = System.getProperties();
             props.put("mail.smtp.host", "localhost");
             Session session = Session.getDefaultInstance(props, null);

             if( toAddress == null || toAddress.isEmpty()) {
            	 return Constants.EMPTY_STRING;
             }
             
             
             List<String> sentList = new ArrayList<String>() ;
             List<String> errorList = new ArrayList<String>() ;
             List<String> dupeList = new ArrayList<String>() ;


             
             // Create a new message
             
             // Set the FROM and TO fields 
             for( TargetUser toUser : toAddress ) {            	 
		Thread.sleep( 1500 ) ;
                 String toEmail = toUser.getEmailID() ;
            	 if (toEmail == null) {
            		 System.out.println( "NULL email ID received for sending email message from " + fromAddress) ;
            		 continue ;
            	 }
            	 toEmail = toEmail.trim();
            	 if (toEmail.isEmpty() == true ) {
            		 System.out.println( "Empty email ID received for sending email message from " + fromAddress) ;
            		 continue ;
            	 }
                 if( sentList.contains(toEmail) == true) {
            		 System.out.println( "Duplicate email ID " + toEmail + "received for sending email message from " + fromAddress) ;
                	 dupeList.add(toEmail) ;
                	 continue ;
                 }
                 try {
                	 sentList.add( toEmail ) ;

	                 Message msg = new MimeMessage(session);
	                 msg.setFrom(new InternetAddress(fromAddress));
	                 msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse( toEmail , false ));
	
	                 
	                 if (subject != null)
	                     msg.setSubject(subject);
	                 else
	                     msg.setSubject(Constants.EMPTY_STRING);
	                 
	
	                 if (body != null) {
	                 	if (bodyType == true) 
	                		msg.setContent(toUser.personalizeMessage(body), "text/html") ;
	                	else
	                		msg.setContent(toUser.personalizeMessage(body), "text/plain") ;
	                 } else {
	                     msg.setText(Constants.EMPTY_STRING);
	                 }
	                 msg.setHeader("X-Mailer", "LOTONtechEmail");
	                
	                 Transport.send(msg);
	                 //System.out.println("Plain Text Message sent OK.");
                 } catch (Exception ex) {
                	 ex.printStackTrace() ;
                	 errorList.add( toEmail) ;
                 }
             }
             
             // For now, we will keep CC also in the to list


             for (String errStr : errorList ) {
            	 System.out.println(" Erroneus email found : " + errStr + " From " + fromAddress) ;
             }

             System.out.println("SUCCESS MESSAGE: " + Constants.SUCCESS_STRING);
             return Constants.SUCCESS_STRING;
          
         } catch (Exception ex) {
             ex.printStackTrace();
             return Constants.ERROR_STRING;
         }

         //System.out.println("Message : " + messageID +" Subject: " + subject + "send successfull" ) ;
     }

     /** A static method to send an email with attachment
       * which returns the string "success" if mail is successfully sent
       * or "exception" if there was a problem
       * 
       * 
       * This is DEPRECATED
       *
       */
     
     public static String sendPersonalAttachmentMail(String subject, String body, String fromAddress, List<TargetUser> toAddress, String ccAddressString, int messageID, Blob blobContent, String attachment, boolean bodyType) throws Exception {     
         VinrLookupTable lookupTable = null;  
         try {
         
             String contentType = null;
             String[] ext = attachment.split("\\.");

             //System.out.println("the extension is :" + ext[ext.length-1]);
             //System.out.println("out of for loop the extension is :" + ext[ext.length-1]);

             Object extension = (Object)ext[ext.length-1].toLowerCase();
             
             contentType = "application/octet-stream";


             //System.out.println("In the VinrEmailNotification class Main method - The Content-Type is : " + contentType);                           
             //System.out.println( "The To List: " + toAddressList ) ;
             //System.out.println( "The From List: " + fromAddress ) ;

             Properties props = System.getProperties();
             props.put("mail.smtp.host", "localhost");
             Session session = Session.getDefaultInstance(props, null);

             List<String> sentList = new ArrayList<String>() ;
             List<String> errorList = new ArrayList<String>() ;
             List<String> dupeList = new ArrayList<String>() ;
             

             for( TargetUser toUser : toAddress ) {            	 
		Thread.sleep( 1500 ) ;
            	 String toEmailId = toUser.getEmailID();
             // Create a new message
            	 if (toEmailId == null) {
            		 System.out.println( "NULL email ID received for sending email message from " + fromAddress) ;
            		 continue ;
            	 }
            	 toEmailId = toEmailId.trim() ;
            	 if (toEmailId.isEmpty() == true) {
            		 System.out.println( "Empty email ID received for sending email message from " + fromAddress) ;            		 
            	 }
        		 if (sentList.contains(toEmailId) == true) {
            		 System.out.println( "Duplicate email ID " + toEmailId + "received for sending email message from " + fromAddress) ;
        			 dupeList.add( toEmailId ) ;
        			 continue ;
        		 }

            	 try {
            	 
            		 sentList.add( toEmailId ) ;
            		 Message msg = new MimeMessage(session) ; 
	
            		 //Set the FROM and TO fields 
            		 msg.setFrom(new InternetAddress(fromAddress));
            		 msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailId, false));
	
	
            		 if (subject != null)
            			 msg.setSubject(subject);
            		 else
            			 msg.setSubject(Constants.EMPTY_STRING);
	  
            		 MimeBodyPart mbp1 = new MimeBodyPart();
	             
            		 if( bodyType == true) {
            			 mbp1.setContent(toUser.personalizeMessage(body), "text/html");		             
            		 }
            		 else
            			 mbp1.setContent(toUser.personalizeMessage(body), "text/plain");
	
            		 MimeBodyPart firstAttachment = new MimeBodyPart();
            		 firstAttachment.setDataHandler(new DataHandler( new ByteArrayDataSource(blobContent.getBinaryStream(), contentType)));
            		 firstAttachment.setFileName(attachment) ;
	
            		 Multipart mp = new MimeMultipart();
            		 mp.addBodyPart(mbp1);
            		 mp.addBodyPart(firstAttachment);
	           
            		 msg.setContent(mp) ;
	
            		 msg.setHeader("X-Mailer", "LOTONtechEmail");
            		 msg.setHeader("List-Unsubscribe", "<mailto:"+fromAddress+">");
            		 Transport.send(msg);
            	 } catch (Exception ex) {
            		 ex.printStackTrace() ;
            		 errorList.add(toEmailId) ;
            	 }
            	 
	             //System.out.println("Plain Text Message sent OK.");
             }
             for (String errStr : errorList ) {
            	 System.out.println(" Erroneus email found : " + errStr + " From " + fromAddress) ;
             }
             return Constants.SUCCESS_STRING;
         } catch (Exception ex) {
             //System.out.println("Unable to read LookupTable");
             ex.printStackTrace();
             return Constants.FAILURE_STRING;
         } finally {
        	 
         }
     }

     public static String sendCalendarMail(String subject, String body, String fromAddress, List<TargetUser> toAddress, String ccAddressString, int messageID, Blob blobContent, String attachment) throws Exception {     
         VinrLookupTable lookupTable = null;  
         try {
         
             String contentType = null;
             attachment = "test.ics";
             String[] ext = attachment.split("\\.");

             //System.out.println("the extension is :" + ext[ext.length-1]);
             //System.out.println("out of for loop the extension is :" + ext[ext.length-1]);

             Object extension = (Object)ext[ext.length-1].toLowerCase();
             
             contentType = "text/calendar";


             //System.out.println("In the VinrEmailNotification class Main method - The Content-Type is : " + contentType);                           
             //System.out.println( "The To List: " + toAddressList ) ;
             //System.out.println( "The From List: " + fromAddress ) ;

             Properties props = System.getProperties();
             props.put("mail.smtp.host", "localhost");
             Session session = Session.getDefaultInstance(props, null);

             List<String> sentList = new ArrayList<String>() ;
             List<String> errorList = new ArrayList<String>() ;
             List<String> dupeList = new ArrayList<String>() ;
             

             for( TargetUser toUser : toAddress ) {            	 
		Thread.sleep( 1500 ) ;
            	 String toEmailId = toUser.getEmailID();
             // Create a new message
            	 if (toEmailId == null) {
            		 System.out.println( "NULL email ID received for sending email message from " + fromAddress) ;
            		 continue ;
            	 }
            	 toEmailId = toEmailId.trim() ;
            	 if (toEmailId.isEmpty() == true) {
            		 System.out.println( "Empty email ID received for sending email message from " + fromAddress) ;            		 
            	 }
        		 if (sentList.contains(toEmailId) == true) {
            		 System.out.println( "Duplicate email ID " + toEmailId + "received for sending email message from " + fromAddress) ;
        			 dupeList.add( toEmailId ) ;
        			 continue ;
        		 }

            	 try {
            	 
            		 sentList.add( toEmailId ) ;
            		 Message msg = new MimeMessage(session) ; 
	
            		 //Set the FROM and TO fields 
            		 msg.setFrom(new InternetAddress(fromAddress));
            		 msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailId, false));
	
	
            		 if (subject != null)
            			 msg.setSubject(subject);
            		 else
            			 msg.setSubject(Constants.EMPTY_STRING);
	  
            		 MimeBodyPart mbp1 = new MimeBodyPart();
	             
            			 mbp1.setContent(toUser.personalizeMessage(body), "text/html");		             
	
            		 MimeBodyPart firstAttachment = new MimeBodyPart();
            		 firstAttachment.setDataHandler(new DataHandler( new ByteArrayDataSource(blobContent.getBinaryStream(), contentType)));
            		 firstAttachment.setFileName(attachment) ;
	
            		 Multipart mp = new MimeMultipart();
            		 mp.addBodyPart(mbp1);
            		 mp.addBodyPart(firstAttachment);
	           
            		 msg.setContent(mp) ;
	
            		 msg.setHeader("X-Mailer", "LOTONtechEmail");
            		 msg.setHeader("List-Unsubscribe", "<mailto:"+fromAddress+">");
            		 Transport.send(msg);
            	 } catch (Exception ex) {
            		 ex.printStackTrace() ;
            		 errorList.add(toEmailId) ;
            	 }

	             //System.out.println("Plain Text Message sent OK.");
             }
             for (String errStr : errorList ) {
            	 System.out.println(" Erroneus email found : " + errStr + " From " + fromAddress) ;
             }
        	 System.out.println("SUCCESS MESSAGE: " + Constants.SUCCESS_STRING);
             return Constants.SUCCESS_STRING;
         } catch (Exception ex) {
             //System.out.println("Unable to read LookupTable");
             ex.printStackTrace();
             return Constants.FAILURE_STRING;
         } finally {
        	 
         }
     }

     
     public static void sendConfirmation(String subject, 
    		 String body, 
    		 String fromAddress, 
    		 List<TargetUser> toAddress, 
    		 List<TargetUser> informList,  
    		 int messageID, 
    		 Blob blobContent, 
    		 String attachment ) throws Exception {
         String completeMessage = null ;
         try {
             logger.debug( "The subject in VinrEmailDispatcher class is :::=====>  " + subject );
             logger.debug( "The body in VinrEmailDispatcher class is *+**+*+*+*+*+*+ " + body) ;
             //System.out.println( "The From List: " + fromAddress ) ;	
             String contentType = "application/octet-stream";
             
             Properties props = System.getProperties();
             props.put("mail.smtp.host", "localhost");
             Session session = Session.getDefaultInstance(props, null);

             if( toAddress == null || toAddress.isEmpty()) {
            	 return ;
             }

             if (subject != null) {            	 
                 subject = "Confirmation Message : " + subject ;
             }
             else {
            	 subject = "Confirmation Message : <no subject>" ;
             }
             
             List<String> sentList = new ArrayList<String>() ;
             
             String userHtmlTable = "<table border=\"5\">" ;
             userHtmlTable += "<tr>" ;

             userHtmlTable += "<th>" ;
             userHtmlTable += "Name" ;
             userHtmlTable += "</th>" ;
             
             userHtmlTable += "<th>" ;
             userHtmlTable += "EmailID" ;
             userHtmlTable += "</th>" ;
       
             userHtmlTable += "</tr>" ;
             for (TargetUser toUser : toAddress ) {
            	 if (toUser.getEmailID() == null) {
            		 System.out.println( "Empty email ID received for sending confirmation email message from " + fromAddress) ;
            		 continue ;
            	 }
            	 userHtmlTable += "<tr>" ;
            	 userHtmlTable += "<td>" ;
            	 userHtmlTable += toUser.getPrefix() + toUser.getName()  ;
            	 userHtmlTable += "</td>" ;
            	 userHtmlTable += "<td>" ;
            	 userHtmlTable += toUser.getEmailID()  ;
            	 userHtmlTable += "</td>" ;
            	 userHtmlTable += "</tr>" ;
             }
             userHtmlTable += "</table>" ;
             
             String informationText = "<p>" ;
             informationText += "<b>" ;
             informationText += "The following message is sent by " + fromAddress ;
             informationText += "</b>" ;
             informationText += "</p>" ;
             
             completeMessage = informationText ;
             if (body != null) {
            	 completeMessage += "<p>" ;
            	 completeMessage += body ;
            	 completeMessage += "</p>" ;
             }
             if (userHtmlTable.isEmpty() == false) {
            	 completeMessage += "<b>to the following users </b> <br/>" ;
            	 completeMessage += userHtmlTable ;
             }
             


             
             // Create a new message
             
             // Set the FROM and TO fields 
             for( TargetUser toUser : informList ) {            	 
            	 
                 String toEmail = toUser.getEmailID().trim();
                 if( sentList.contains(toUser) == true) {
                	 continue ;
                 }
                 sentList.add( toEmail ) ;

                 Message msg = new MimeMessage(session);
                 msg.setFrom(new InternetAddress(fromAddress));
                 msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse( toEmail , false ));

                 
                 if (subject != null)
                     msg.setSubject(subject);
                 else
                     msg.setSubject(Constants.EMPTY_STRING);

	            MimeBodyPart mbp1 = new MimeBodyPart();
	             
		        mbp1.setContent(completeMessage, "text/html");		             
	
	
	            Multipart mp = new MimeMultipart();

	            mp.addBodyPart(mbp1);
	            
	            if ( blobContent != null && attachment != null) {
		            MimeBodyPart firstAttachment = new MimeBodyPart();
		            firstAttachment.setDataHandler(new DataHandler( new ByteArrayDataSource(blobContent.getBinaryStream(), contentType)));
		            firstAttachment.setFileName(attachment) ;
		            mp.addBodyPart(firstAttachment);
	            }
	           
	            msg.setContent(mp) ;


                msg.setHeader("X-Mailer", "LOTONtechEmail");
                
                 Transport.send(msg);
                 //System.out.println("Plain Text Message sent OK.");
             }
             
             // For now, we will keep CC also in the to list


             System.out.println( completeMessage ) ;
             return ;
          
         } catch (Exception ex) {
             ex.printStackTrace();
             if (completeMessage != null) {
            	 System.out.println( completeMessage ) ;
             }
             return ;
         }
     } 


}
