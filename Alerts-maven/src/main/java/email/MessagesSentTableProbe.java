package email;

import org.apache.log4j.Logger;

/** This thread probes the messages_sent_table and gets
 *  the status of the sms sent for each record and based
 *  whether the sms was sent successfully or not, it puts
 *  the record in the success_table or the retry table.
 *  The records in the retrytable will be probed by
 *  RetryTableProbe and the sms will be sent through
 *  the secondary provider
 *  @author Sunil Tuppale
 *  @date July-19-2010
 *  @version 1.0
 */
public class MessagesSentTableProbe implements Runnable  {

    private int id = 0;
    static final Logger logger = Logger.getLogger(MessagesSentTableProbe.class);

    public MessagesSentTableProbe(int id) {
        this.id = id;
    }

    /** All the code for probing the messages_sent_table and finding
     *  the status of a sent sms
     */
    public void run() {
        logger.debug("Running MessagesSentTableProbe");
    }
}

