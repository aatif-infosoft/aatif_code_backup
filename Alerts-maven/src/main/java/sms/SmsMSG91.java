package sms;

import email.VinrMessageParser;
import email.VinrMessagesOutTable;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Constants;
import utils.TargetUser;
import utils.Utils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains the sendSMS() implementation for for SmsCountry
 *
 * @author Sushma
 * @date July-08-2010
 * @version 1.0
 */

public class SmsMSG91 implements SMSProvider {
	VinrMessagesOutTable msgouttab = new VinrMessagesOutTable();

	JSONArray ja = new JSONArray();
	static final Logger logger = Logger.getLogger(SmsMSG91.class);

	public static class MyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			// verification of hostname is switched off
			return true;
		}
	}

	public String sendSingleSMS(HashMap providerParams, String msgId,
			List<TargetUser> numbersList, String textMessage) {

		try {

			String userId = (String) providerParams
					.get(VinrMessageParser.PRIMARY_PROVIDER_USERID);
			String mainURL = (String) providerParams
					.get(VinrMessageParser.PRIMARY_PROIVDER_URL);
			String password = (String) providerParams
					.get(VinrMessageParser.PRIMARY_PROIVDER_PASSWORD);

			String authKey = Utils.decrypt(userId);
			String senderId = (String) providerParams
					.get(VinrMessageParser.PRIMARY_PROIVDER_SID);
			String route = Utils.decrypt(password);
			String mobile = null;
			String message = null;

			for (TargetUser tUser : numbersList) {

				mobile = tUser.getMobileNumber();

				if (mobile != null && mobile.isEmpty() == false
						&& textMessage != null
						&& textMessage.isEmpty() == false) {
					String replacedMessage = tUser
							.personalizeMessage(textMessage);
					// String replacedMessage = personalizedSMS ;
					// //Utils.replaceCharacterWithSpacesAround(
					// personalizedSMS, '&', 'n') ;
					// replacedMessage = Utils.replaceCharacterWithWord(
					// replacedMessage, '@', new String("(at)")) ;
					message = URLEncoder.encode(replacedMessage, "UTF-8");
					StringBuilder sbPostData = new StringBuilder(mainURL);
					sbPostData.append("authkey=" + authKey);
					sbPostData.append("&mobiles=" + mobile);
					sbPostData.append("&message=" + message);
					sbPostData.append("&route=" + route);
					sbPostData.append("&sender=" + senderId);

					// final string
					String finalURL = sbPostData.toString();

					URL url = new URL(finalURL);
					URLConnection conn = url.openConnection();
					conn.setDoOutput(true);
					conn.connect();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));
					String response;
					response = reader.readLine();
					while ((response) != null)
						response += response;
					reader.close();
					System.out
							.println("The value of responseId in MSG91 CLASS IS ----========------> "
									+ response);

				}
			}
			return Constants.SUCCESS_STRING;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERROR_STRING;
		}
		// return Constants.SUCCESS_STRING;
	}

	public String sendSMS(HashMap providerParams, String msgId,
			List<TargetUser> numbersList, String textMessage) {
		logger.debug("Inside SEND SMS");
		logger.info("Target numbers list before : " + numbersList.size());
		String providerCode = providerParams.get(
				VinrMessageParser.PRIMARY_PROVIDER_CODE).toString();
		try {
			String userId = (String) providerParams
					.get(VinrMessageParser.PRIMARY_PROVIDER_USERID);
			String mainURL = (String) providerParams
					.get(VinrMessageParser.PRIMARY_PROIVDER_URL);
			String password = (String) providerParams
					.get(VinrMessageParser.PRIMARY_PROIVDER_PASSWORD);

			String authKey = Utils.decrypt(userId);
			String senderId = (String) providerParams
					.get(VinrMessageParser.PRIMARY_PROIVDER_SID);
			String route = Utils.decrypt(password);
			String mobile = null;
			String message = null;

			StringBuilder sbPostData = new StringBuilder(mainURL);
			String xmlData = "<AUTHKEY>" + authKey + "</AUTHKEY>";
			xmlData = xmlData + "<SENDER>" + senderId + "</SENDER>";
			xmlData = xmlData + "<ROUTE>" + route + "</ROUTE>";

			for (TargetUser tUser : numbersList) {
				mobile = tUser.getMobileNumber();
				if (mobile != null && mobile.isEmpty() == false
						&& textMessage != null
						&& textMessage.isEmpty() == false) {
					String replacedMessage = tUser
							.personalizeMessage(textMessage);
					// String replacedMessage = personalizedSMS ;
					// //Utils.replaceCharacterWithSpacesAround(
					// personalizedSMS, '&', 'n') ;
					// replacedMessage = Utils.replaceCharacterWithWord(
					// replacedMessage, '@', new String("(at)")) ;

					message = URLEncoder.encode(replacedMessage, "UTF-8");

					String smsContent = "<SMS TEXT=\"" + replacedMessage
							+ "\">";
					smsContent = smsContent + "<ADDRESS TO=\"" + mobile + "\">"
							+ "</ADDRESS>";
					smsContent = smsContent + "</SMS>";
					xmlData = xmlData + smsContent;

				}

			}

			xmlData = "<MESSAGE>" + xmlData + "</MESSAGE>";
			// System.out.println("XML posted = " + xmlData) ;
			sbPostData.append("data=" + xmlData);

			// final string
			String finalURL = sbPostData.toString();

			URL url = new URL(finalURL);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			((HttpsURLConnection) conn)
					.setHostnameVerifier(new MyHostnameVerifier());
			conn.setDoOutput(true);
			conn.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(),Charset.forName("UTF-8")));
			String response;

			while ((response = reader.readLine()) != null) {
				logger.info("Target numbers list after : " + numbersList.size());
				SentSMS(msgId, numbersList, response, message, providerCode);
				response += response;
				logger.debug("The value of responseId in MSG91 class is : "
						+ response);
			}
			logger.debug("XML posted = " + xmlData);
			reader.close();
			return Constants.SUCCESS_STRING;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERROR_STRING;
		}

	}

	public String SentSMS(String msgId, List<TargetUser> numbersList,
			String response, String message, String providerCode) {
		logger.info("Target numbers list Inside SentSMS method : "
				+ numbersList.size());
		JSONArray messagesArray = new JSONArray();
		try {
			for (TargetUser tUser : numbersList) {
				String mobile = tUser.getMobileNumber();
				logger.info("Mobile number is : " + mobile);
				if (mobile != null && mobile.isEmpty() == false) {
					JSONObject obj = new JSONObject();
					obj.put("MsgId", msgId);
					obj.put("Response ID", response);
					obj.put("Mobile", mobile);
					obj.put("Message", message);
					obj.put("ProviderCode", providerCode);
					messagesArray.put(obj);

				} else {
					logger.info("problem is here");
				}
			}
			logger.debug("Messages array size :" + messagesArray.length());
			msgouttab.writeIntoMessagesOutTable(messagesArray);
			return Constants.SUCCESS_STRING;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERROR_STRING;
		}
	}
}
