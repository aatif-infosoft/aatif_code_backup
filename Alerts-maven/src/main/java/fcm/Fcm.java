package fcm;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import utils.Constants;
import utils.ForwardRequest;
import utils.TargetUser;
import utils.Utils;

import java.util.List;

public class Fcm {
	static final Logger logger = Logger.getLogger(Fcm.class);

	public void sendFcmNotification(List<TargetUser> toList, String message, String customData) {
		logger.debug("TO LIST : " + toList);
		logger.debug("Message : " + message);
		try {
			if (toList != null && toList.size() > 0) {
				for (TargetUser tu : toList) {
					String fcmId = tu.getFcmId();
					String serverKey = tu.getServerKey();
					int deviceType = Integer.parseInt(tu.getDeviceType());

					logger.debug("Fcm id : " + fcmId);
					logger.debug("server key : " + serverKey);

					JSONObject outerObject = new JSONObject();
					JSONObject innerObject = new JSONObject();
					logger.debug("Custom data status : " + Utils.isNullOrEmpty(customData));
					if (Utils.isNullOrEmpty(customData) == false) {
						JSONObject extendedObject = JSONObject.fromObject(customData);
						logger.debug("Extended obj : " + extendedObject);
						innerObject.putAll(extendedObject);
						logger.debug("Inner Obj : " + innerObject);
					}
					outerObject.put("priority", "high");
					outerObject.put("content_available", true);
					outerObject.put("to", fcmId);
					if (deviceType == Constants.ANDROID_DEVICE) {
						message = "\"" + message + "\"";
						innerObject.put("body", message);
						outerObject.put("data", innerObject);
					} else if (deviceType == Constants.IOS_DEVICE) {
						innerObject.put("body", message);
						innerObject.put("sound", "default");
						outerObject.put("notification", innerObject);
					}
					sendNotification(outerObject.toString(), serverKey);

				}

			} else {
				logger.debug("Empty target users ");
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void sendNotification(String jsonRequest, String serverKey) {
		ForwardRequest fr = new ForwardRequest();
		String url = "https://fcm.googleapis.com/fcm/send";
		logger.debug("Request JSON : " + jsonRequest);
		String s = fr.connectAndReceive(url, jsonRequest, serverKey);
		logger.debug("Response from Fcm : " + s);
	}

}
