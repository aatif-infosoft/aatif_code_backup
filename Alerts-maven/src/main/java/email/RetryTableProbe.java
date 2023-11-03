package email;
/** This thread probes the retrytable and 
 *  tries to resend the previously failed 
 *  sms through a secondary provider.  The
 *  retry attempt will be made only once.
 *  It adds a record to the messages_sent table
 *  with a retry count of 1.  It is similar to the
 *  MessagesInTableProbe.
 *  @author Sunil Tuppale
 *  @date July-19-2010
 *  @version 1.0
 */

public class RetryTableProbe implements Runnable {
 
    private int id = 0;

    public RetryTableProbe(int id) {
        this.id = id;
    }

    /** Implementation to probe the retrytable */
    public void run() {
    }
}
