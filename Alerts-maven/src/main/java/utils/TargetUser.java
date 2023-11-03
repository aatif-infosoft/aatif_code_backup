package utils;

public class TargetUser {

	String name;
	String mobileNumber;
	String prefix;
	String emailId;
	String value1;
	String contactName;
	String contactPersonPrefix;
	String fcmId;
	String serverKey;
	String deviceType;

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	boolean validateEmail(String emailString) {
		if (emailString != null && emailString.isEmpty() == false) {
			String[] email = emailString.split("@");
			if (email != null && email.length == 2) {
				for (int len = 0; len < email[0].length(); len++) {
					char testChar = email[0].charAt(len);
					if ((testChar >= '0' && testChar <= '9') || (testChar >= 'a' && testChar <= 'z')
							|| (testChar >= 'A' && testChar <= 'Z') || testChar == '_' || testChar == '.'
							|| testChar == '-') {
						continue;
					} else
						return false;
				}
			} else {
				return false;
			}

		}
		return true;
	}

	boolean validateDigits(String digitString) {

		if (digitString != null && digitString.isEmpty() == false) {
			for (int len = 0; len < digitString.length(); len++) {
				char val = digitString.charAt(len);
				if (val >= '0' && val <= '9') {
					continue;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	public String personalizeMessage(String mesgBody) {
		String replacedMessage = new String();
		System.out.println("To replace : " + mesgBody);
		System.out.println("With " + "Name : " + name + " ContactPersonName : " + contactName + "Number : "
				+ mobileNumber + " Prefix" + prefix + " Email : " + emailId);
		if (mesgBody != null && mesgBody.trim().isEmpty() == false) {

			replacedMessage = mesgBody;

			// Ideally a tree should be formed, and the tags should be replaced efficiently
			// as the load increases
			replacedMessage = Utils.replaceAll(replacedMessage, "$Name", name);
			replacedMessage = Utils.replaceAll(replacedMessage, "$ContactPersonName", contactName);
			replacedMessage = Utils.replaceAll(replacedMessage, "$Number", mobileNumber);
			replacedMessage = Utils.replaceAll(replacedMessage, "$Prefix", prefix + name);
			replacedMessage = Utils.replaceAll(replacedMessage, "$Email", emailId);
			replacedMessage = Utils.replaceAll(replacedMessage, "$ContactPersonPrefix",
					contactPersonPrefix + contactName);
			replacedMessage = Utils.replaceAll(replacedMessage, "$FCMID", fcmId);
			replacedMessage = Utils.replaceAll(replacedMessage, "$ServerKey", serverKey);
		}
		System.out.println("Replaced Message : " + replacedMessage);
		return replacedMessage;
	}

	public String getFcmId() {
		return fcmId;
	}

	public void setFcmId(String fcmId) {
		this.fcmId = fcmId;
	}

	public String getServerKey() {
		return serverKey;
	}

	public void setServerKey(String serverKey) {
		this.serverKey = serverKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null)
			this.name = name.trim();
		else
			this.name = null;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobNumber) {
		if (mobNumber != null)
			this.mobileNumber = mobNumber.trim();
		else
			this.mobileNumber = null;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String title) {
		if (title != null)
			this.prefix = title.trim();
		else
			this.prefix = null;
	}

	public String getEmailID() {
		return this.emailId;
	}

	public void setEmailID(String emailid) {
		System.out.println("Request to set email: " + emailid);
		if (emailid != null)
			this.emailId = emailid.trim();
		else
			this.emailId = null;
		System.out.println("The Email Set is " + this.emailId);
	}

	public void setValue1(String val) {
		if (val != null)
			this.value1 = val;
		else
			this.value1 = null;
	}

	public String getValue1() {
		return this.value1;
	}

	public void setContactName(String val) {
		if (val != null)
			this.contactName = val;
		else
			this.contactName = null;
	}

	public String getContactName() {
		return this.contactName;
	}

	public void setContactPersonPrefix(String val) {
		if (val != null)
			this.contactPersonPrefix = val.trim();
		else
			this.contactPersonPrefix = null;
	}

	public String getContactPersonPrefix() {
		return this.contactPersonPrefix;
	}

}
