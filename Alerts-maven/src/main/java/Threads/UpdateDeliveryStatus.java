package Threads;

import org.apache.log4j.Logger;
import sms.Operaions;
import utils.PropertiesUtils;
import utils.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UpdateDeliveryStatus extends BaseThread {

	static final Logger logger = Logger.getLogger(UpdateDeliveryStatus.class);

	int thread_sleep_time = Integer.parseInt(PropertiesUtils
			.getProperty("thread_sleep_time"));

	int THREAD_POOL_SIZE = 5;/*
							 * ConnectionPoolProvider.getInstance()
							 * .getThreadPoolSize();
							 */

	String readNewDeliveryReportSQL = "Select * from DeliveryReport where S_no %% %d=%d";

	String updateMessegesOutTableSQL = "Update messagesouttable set DeliveryStatus='%s',UpdatedTime='%s' where Address='%s' and ResId='%s'";

	String deleteDeliveryReportEntrySQL = "Delete from DeliveryReport where S_no=%d";

	int threadId = 0;

	public UpdateDeliveryStatus(int s, String url, String username,
			String passwd) {
		super(s, url, username, passwd);
		threadId = s;
	}

	@SuppressWarnings("static-access")
	@Override
	void process(Connection connection) {
		System.out.println("Inside Process");
		ResultSet rs = null;
		Statement stmt = null;

		Utils utils = new Utils();
		Operaions op = new Operaions();
		try {
			while (true) {
				String readNewDeliveryReportQuery = readNewDeliveryReportSQL
						.format(readNewDeliveryReportSQL, THREAD_POOL_SIZE,
								threadid);
//				System.out.println("readNewDeliveryReportQuery : "
//						+ readNewDeliveryReportQuery);
//				logger.info("Read new delivery report Query : "
//						+ readNewDeliveryReportQuery);
				stmt = connection.createStatement();
				rs = stmt.executeQuery(readNewDeliveryReportQuery);

				String currentTime = utils.getCurrentTimeInUTC();
				while (rs.next()) {
					int s_no = rs.getInt("S_no");
					String req_id = rs.getString("requestid");
					String mobile_number = rs.getString("number");
					String status = rs.getString("description");

					String updateMOTQuery = updateMessegesOutTableSQL.format(
							updateMessegesOutTableSQL, status, currentTime,
							mobile_number, req_id);
					System.out
							.println("Updating MOT Query : " + updateMOTQuery);
					op.performAction(updateMOTQuery, connection);

					String deleteDRQuery = deleteDeliveryReportEntrySQL.format(
							deleteDeliveryReportEntrySQL, s_no);
					logger.info("Delete DR Query : " + deleteDRQuery);
					op.performAction(deleteDRQuery, connection);
				}
				Thread.sleep(thread_sleep_time);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
