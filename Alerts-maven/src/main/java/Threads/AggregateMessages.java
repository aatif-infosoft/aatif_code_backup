package Threads;

import org.apache.log4j.Logger;
import sms.Operaions;
import utils.PropertiesUtils;
import utils.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AggregateMessages extends BaseThread {
	static final Logger logger = Logger.getLogger(AggregateMessages.class);

	int thread_sleep_time = Integer.parseInt(PropertiesUtils
			.getProperty("thread_sleep_time"));
	int THREAD_POOL_SIZE = 5;/*ConnectionPoolProvider.getInstance()
			.getThreadPoolSize()*/;

	String readUpdatedMessagesSQL = "Select * from MessagesOutTable where Id %% %d=%d and Processed=false";

	String updateEntrySQL = "Update MessagesOutTable set Processed=true,UpdatedTime='%s' where Id=%d";

	int threadId = 0;

	public AggregateMessages(int s, String url, String username, String passwd) {
		super(s, url, username, passwd);
		threadId = s;
	}

	@SuppressWarnings("static-access")
	@Override
	void process(Connection connection) {
		System.out.println("THREAD_POOL_SIZE : " + THREAD_POOL_SIZE);
		ResultSet rs = null;
		Statement stmt = null;

		Utils utils = new Utils();
		Operaions op = new Operaions();
		try {
			String currentTime = utils.getCurrentTimeInUTC();
			while (true) {
				String readUpdatedMessagesQuery = readUpdatedMessagesSQL
						.format(readUpdatedMessagesSQL, THREAD_POOL_SIZE,
								threadId);
				/*
				 * logger.info("Read updated messages Query  : " +
				 * readUpdatedMessagesQuery);
				 */
//				System.out.println("Read updated messages Query  : "
//						+ readUpdatedMessagesQuery);
				stmt = connection.createStatement();
				rs = stmt.executeQuery(readUpdatedMessagesQuery);
				while (rs.next()) {
					int id = rs.getInt("id");
					int msgId = rs.getInt("MsgId");

					String updateQuery = null;
					String deliveryReport = rs.getString("DeliveryStatus");
					if (deliveryReport.equalsIgnoreCase(Variables.Delivered)
							|| deliveryReport.equalsIgnoreCase(Variables.Sent)
							|| deliveryReport
									.equalsIgnoreCase(Variables.Pending)) {
						updateQuery = "Update MessageAggregation set SuccessCount=SuccessCount+1,TotalSMSCost=TotalSMSCost+EachSMSCost where MsgId="
								+ msgId;
					} else {
						updateQuery = "Update MessageAggregation set FailureCount=FailureCount+1 where MsgId="
								+ msgId;
					}
					System.out.println("Update Message aggregation Query : "
							+ updateQuery);
					op.performAction(updateQuery, connection);

					String updateEntryQuery = updateEntrySQL.format(
							updateEntrySQL, currentTime, id);
					logger.info("Update MOT entry query : " + updateEntryQuery);
					op.performAction(updateEntryQuery, connection);
				}
				Thread.sleep(thread_sleep_time);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
