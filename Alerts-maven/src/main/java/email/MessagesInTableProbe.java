package email;

import fcm.Fcm;
import org.apache.log4j.Logger;
import sms.Operaions;
import sms.SMSProvider;
import sms.SMSProviderFactory;
import utils.Constants;
import utils.TargetUser;
import utils.Utils;

import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * The thread or probe that looks for messages in the messagesintable and sends
 * the message via email or sms based on the messageType. Once the message is
 * sent, if it is an sms, it is stored in the messages_sent_table along with the
 * responseId. Later on the MessagesSentTableProbe probes the
 * messages_sent_table and comes up with the status of the message. The messages
 * are deleted from the messagesintable by this thread.
 * 
 * @author Sushma P
 * @date July-19-2010
 * @version 1.0
 */

public class MessagesInTableProbe implements Runnable { // Thread Runnable
	Operaions op = new Operaions();
	private int id = 0;
	private HashMap<String, String> defaultProviderParameters;
	static final Logger logger = Logger.getLogger(MessagesInTableProbe.class);

	public MessagesInTableProbe(int id) { // Constructor of messagesintableprobe
		logger.debug("Thread Id in MITP : " + id);
		this.id = id;
	}

	public HashMap<String, String> getSMSProviderParameters(
			VinrMessageParser parser) {

		// Hashmap returntype method

		String providerCode;

		HashMap<String, String> providerParameters = null; // class obj
		try {

			String fileName = System.getenv("DefaultSmsProvider_CONFIG_FILE"); // getting
																				// environment
																				// variable
																				// from
																				// Default
																				// Sms
																				// Provider_Config_file
			FileInputStream propFile = new FileInputStream(fileName);
			Properties p = new Properties(System.getProperties());
			p.load(propFile);
			String senderId = parser.getSenderId();

			if (parser.isForceDefaultProvider() == false) {
				providerCode = parser.getPrimaryProviderCode();
				providerParameters = parser.getPrimaryProviderParameters();
			} else {
				providerCode = p.getProperty("default_provider_code"); // This
																		// is
																		// the
																		// default
																		// provider
																		// code
																		// set
																		// in
																		// defaultProvider.ini
				String userName = p.getProperty("default_username");
				if (senderId != null && senderId.isEmpty() == false) {
					// retain the sender id
				} else {
					senderId = p.getProperty("default_sid");
				}
				String passwd = p.getProperty("default_password");
				providerParameters = new HashMap<String, String>();
				providerParameters.put(VinrMessageParser.PRIMARY_PROVIDER_CODE,
						providerCode);
				providerParameters.put(
						VinrMessageParser.PRIMARY_PROVIDER_USERID,
						Utils.encrypt(userName));
				providerParameters.put(
						VinrMessageParser.PRIMARY_PROIVDER_PASSWORD,
						Utils.encrypt(passwd));
			}

			providerParameters.put(VinrMessageParser.PRIMARY_PROIVDER_SID,
					senderId);
			String urlCode = providerCode.toUpperCase() + "_URL";
			String url = p.getProperty(urlCode);
			if (url == null) {
				System.out.println("URL not found for provider code : "
						+ urlCode + " in " + fileName);
			}

			if (senderId != null && senderId.isEmpty() == false) {
				senderId = p
						.getProperty(VinrMessageParser.PRIMARY_PROIVDER_SID);
			}

			providerParameters.put(VinrMessageParser.PRIMARY_PROIVDER_URL, url);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return providerParameters;

	}

	public void run() {
		VinrMessagesInTable mesgInTable = new VinrMessagesInTable();
		int msgid = -1;
		while (true) {
			try {
				Thread.sleep(1000);
				mesgInTable.setConnection();
				logger.debug("MITP : " + this.id);
				ResultSet rs1 = mesgInTable.readNewMessages(this.id);
				while (rs1.next()) {
					String sms_response = null;
					String email = null;
					boolean is_Insert_Success = false;
					msgid = rs1.getInt("MsgId");
					int msgtype = rs1.getInt("MsgType");
					String address = rs1.getString("Address");
					String message = rs1.getString("Message");
					String versionColumn = rs1.getString("Version");
					String provider = rs1.getString("Provider");
					Blob blobContent = rs1.getBlob("Attachment");
					String date = rs1.getString("Date");
					String customData = rs1.getString("CustomData");
					String accountId = rs1.getString("AccountId");
					String subAccountId = rs1.getString("SubAccountId");
					String msgIdString = "" + msgid;
					float cost = rs1.getFloat("Cost");
					boolean bodyType;
					/*
					 * String xmlMessage = new StringBuffer("<message>")
					 * .append(rs1.getString(3)).append(rs1.getString(4))
					 * .append(rs1.getString(5)).append(rs1.getString(6))
					 * .append("</message>").toString();
					 */
					String xmlMessage = new StringBuffer("<message>")
							.append(rs1.getString("Version"))
							.append(rs1.getString("Address"))
							.append(rs1.getString("Message"))
							.append(rs1.getString("Provider"))
							.append("</message>").toString();

					logger.debug("XML Text : " + xmlMessage);

					VinrMessageParser parser = new VinrMessageParser();
					parser.parse(xmlMessage);

					String sender = parser.getFromList();
					List<TargetUser> recipients = parser.getToList();
					if (recipients != null && recipients.size() > 0) {
						logger.info("Recipients size : " + recipients.size());
						for (int i = 0; i < recipients.size(); i++) {
							logger.info("Printing mobile : "
									+ recipients.get(i).getMobileNumber());

						}

					}
					List<TargetUser> informList = parser.getInformUsers();
					String cc = parser.getCCList();
					String subject = parser.getSubject();
					String body = parser.getBody();
					String attachmentName = parser.getAttachmentName();
					String version = parser.getVersion();
					String numbersList = parser.getMobileList();
					String textMessage = parser.getShortText();
					String primaryProviderCode;

					bodyType = parser.getBodyContentType();

					if (msgtype == Constants.EMAIL_CODE) {

						if (attachmentName == null) {
							email = VinrEmailDispatcher.sendPersonalPlainMail(
									subject, body, sender, recipients, cc,
									msgid, bodyType);
						} else {
							email = VinrEmailDispatcher
									.sendPersonalAttachmentMail(subject, body,
											sender, recipients, cc, msgid,
											blobContent, attachmentName,
											bodyType);
						}

						if (email.equalsIgnoreCase(Constants.SUCCESS_STRING)) {
							if (informList != null
									&& informList.isEmpty() == false) {
								VinrEmailDispatcher.sendConfirmation(subject,
										body, sender, recipients, informList,
										msgid, blobContent, attachmentName);
							}
							mesgInTable.deleteMessage(msgid);
							// is_Insert_Success =
							// messagesSentTable.insertDataIntoTable(msgid,
							// msgtype,
							// sms_response, address, message, provider, date,
							// customData);
						}
						if (email.equalsIgnoreCase(Constants.ERROR_STRING)) {
							mesgInTable.touchMessage(msgid, 3600);
						}
						// Thread.sleep(150000);
					}

					if (msgtype == Constants.SMS_CODE) {
						SMSProvider smsProvider;
						HashMap<String, String> providerParameters = getSMSProviderParameters(parser);
						String providerCode = providerParameters
								.get(VinrMessageParser.PRIMARY_PROVIDER_CODE);
						logger.debug("Primary Provider Code : " + providerCode);

						smsProvider = SMSProviderFactory
								.getSMSProviderInstance(providerCode);
						logger.debug("The value of txtMsg before passing to sendSMSroutine is  ----> "
								+ textMessage);
						logger.info("Recipients size : " + recipients.size());
						sms_response = smsProvider.sendSMS(providerParameters,
								msgIdString, recipients, textMessage); // gets
																		// the
																		// response
																		// back
																		// from
																		// the
																		// call
																		// to
																		// sendSMS()
						System.out.println("Response From Alerts SMS : "
								+ sms_response);

						if (sms_response
								.equalsIgnoreCase(Constants.SUCCESS_STRING)) {
							System.out.println("Deleting Entry From MSGsInTable");
							mesgInTable.deleteMessage(msgid);
							System.out.println("Creating Success Entry From MSGsInTable");
							op.insertIntoMessageAggregation(msgid, accountId,
									subAccountId, cost);
							// is_Insert_Success =
							// messagesSentTable.insertDataIntoTable(msgid,
							// msgtype,
							// sms_response, address, message, provider, date,
							// customData);
						}
						if (sms_response
								.equalsIgnoreCase(Constants.ERROR_STRING)) {
							mesgInTable.touchMessage(msgid, 3600);
						}
					}
					if (msgtype == Constants.FCM_CODE) {
						Fcm fcm = new Fcm();
						fcm.sendFcmNotification(recipients, textMessage,
								customData);
						mesgInTable.deleteMessage(msgid);

					}
					if (msgtype == Constants.EMAIL_CALENDAR) {
						email = VinrEmailDispatcher
								.sendCalendarMail(subject, body,
										sender, recipients, cc, msgid,
										blobContent, attachmentName);
						if (email.equalsIgnoreCase(Constants.SUCCESS_STRING)) {
							if (informList != null
									&& informList.isEmpty() == false) {
								VinrEmailDispatcher.sendConfirmation(subject,
										body, sender, recipients, informList,
										msgid, blobContent, attachmentName);
							}
							mesgInTable.deleteMessage(msgid);
					}
						if (email.equalsIgnoreCase(Constants.ERROR_STRING)) {
							mesgInTable.touchMessage(msgid, 3600);
						}

				}
				}// end of while
				rs1.close();
			} catch (Exception e) {
				logger.error("Excepton Caught in MessagesinTableProbe Class");
				logger.error(e.getMessage());
				e.printStackTrace();
				mesgInTable.touchMessage(msgid, 3600);
				mesgInTable.releaseConnection();
				// messagesSentTable.releaseConnection();
				// System.exit(0) ;
			} finally {
				mesgInTable.releaseConnection();
				// messagesSentTable.releaseConnection();
			}

			Thread.yield();
		}
	}
}

/*
 * if (msgtype == Constants.EMAIL_AND_SMS_CODE) {
 * 
 * SMSProviderFactory factory = new SMSProviderFactory(); SMSProvider
 * smsProvider = null;
 * 
 * if (attachmentName == null) { email =
 * VinrEmailDispatcher.sendPersonalPlainMail(subject, body, sender, recipients,
 * cc, msgid, bodyType); } else { email =
 * VinrEmailDispatcher.sendPersonalAttachmentMail(subject, body,
 * sender,recipients,cc, msgid, blobContent, attachmentName, bodyType); } if
 * (email.equalsIgnoreCase( Constants.ERROR_STRING ) ) {
 * mesgInTable.touchMessage(msgid, 300) ; }
 * 
 * 
 * if((version != null) && !(version.equals(""))) { smsProvider =
 * factory.getSMSProviderInstance(primaryProvider); sms_response =
 * smsProvider.sendSMS(primaryProviderParameters, msgIdString, recipients,
 * textMessage); } else { String defaultProvider =
 * Constants.DEFAULT_SMS_PROVIDER;
 * 
 * smsProvider = factory.getSMSProviderInstance(defaultProvider); sms_response =
 * smsProvider.sendSMS(defaultProviderParameters, msgIdString, recipients,
 * textMessage); } if (sms_response.equalsIgnoreCase( Constants.ERROR_STRING ) )
 * { mesgInTable.touchMessage(msgid, 300) ; }
 * 
 * // SMS Unicel code if
 * ((sms_response.equalsIgnoreCase(Constants.SUCCESS_STRING)) ||
 * (email.equalsIgnoreCase(Constants.SUCCESS_STRING) &&
 * sms_response.equalsIgnoreCase(Constants.SUCCESS_STRING))) {
 * mesgInTable.deleteMessage(msgid); //is_Insert_Success =
 * messagesSentTable.insertDataIntoTable(msgid, msgtype, sms_response, address,
 * message, provider, date, customData); } //mesgInTable.deleteMessage(msgid); }
 * 
 * // String defaultLowProvider = Constants.DEFAULT_SMS_PROVIDER; SMSProvider
 * defaultHighProvider = SMSProviderFactory.getSMSProviderInstance(new String
 * ("unicel")) ; //smsProvider =
 * SMSProviderFactory.getSMSProviderInstance(defaultHighProvider); HashMap
 * defaultUnicelProviderParameters = new HashMap();
 * defaultUnicelProviderParameters.put("userid", "RaosIn");
 * defaultUnicelProviderParameters.put("password", "56R2Q");
 * defaultUnicelProviderParameters.put("sid", "groupz");
 * 
 * sms_response = defaultHighProvider.sendSMS(defaultUnicelProviderParameters,
 * msgIdString, recipients, textMessage); }
 */
