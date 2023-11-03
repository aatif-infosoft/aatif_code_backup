package sms;

import utils.TargetUser;

import java.util.HashMap;
import java.util.List;

/** The main interface that contains the sendSMS()
 *  method.  This interface has to be implemented
 *  by all classes that have different implementations
 *  of sendSMS()
 *  @author Sushma P
 *  @date July-08-2010
 *  @version 1.0
 */

public interface SMSProvider {
    public String sendSMS(HashMap providerParams, String msgId, List<TargetUser> numbersList, String textMessage);
}
