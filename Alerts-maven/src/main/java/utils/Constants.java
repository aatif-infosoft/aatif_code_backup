package utils;

/**
 * Simple container class for commonly used constant values.
 * 
 * @author Sunil Tuppale
 * @date July-07-2010
 * @version 1.0
 */

public class Constants {

	public static final String EMPTY_STRING = "";
	public static final String ERROR_STRING = "error";
	public static final String EXCEPTION_STRING = "exception";
	public static final String FAILURE_STRING = "failure";
	public static final String SUCCESS_STRING = "success";
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String DEFAULT_SMS_PROVIDER = "smsc";

	public static final int EMAIL_CODE = 0;
	public static final int SMS_CODE = 1;
	public static final int EMAIL_AND_SMS_CODE = 2;
	public static final int FCM_CODE = 3;
	public static final int EMAIL_CALENDAR = 4;

	public static final int FIRST_ATTEMPT = 0;
	public static final int RETRY_ATTEMPT = 1;

	// public static final int THREAD_POOL_SIZE = 4;
	public static final int THREAD_SLEEP_TIME = 5000;

	public static final int ANDROID_DEVICE = 0;
	public static final int IOS_DEVICE = 1;
}
