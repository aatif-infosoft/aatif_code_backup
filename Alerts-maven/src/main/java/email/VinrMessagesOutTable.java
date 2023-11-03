package email;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Utils;

import java.sql.Connection;
import java.sql.Statement;

/**
 * This class connects the code for getting the connection from the connection
 * pool and query the messagesouttable.
 * 
 * @author Sushma P
 * @date July-2-2010
 * @version 1.0
 */

public class VinrMessagesOutTable {
	Connection conn = null;
	static final Logger logger = Logger.getLogger(VinrMessagesOutTable.class);

	public void setConnection() {
		try {
			conn = ConnectionPoolProvider.getInstance().getConnectionPool()
					.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Utils utils = new Utils();

	public boolean writeIntoMessagesOutTable(JSONArray messagesArray) {
		Statement stmt = null;
		String currentTime = utils.getCurrentTimeInUTC();
		try {
			setConnection();
			stmt = conn.createStatement();
			for (int i = 0; i < messagesArray.length(); i++) {
				JSONObject jo1 = messagesArray.getJSONObject(i);
				String msgid = jo1.getString("MsgId");
				String mobile = jo1.getString("Mobile");
				String message = jo1.getString("Message");
				String response = jo1.getString("Response ID");
				String providerCode = jo1.getString("ProviderCode");
				String sql = "insert into messagesouttable(msgId,Address,message,resid,providercode,createdtime,Processed) values ('"
						+ msgid
						+ "','"
						+ mobile
						+ "','"
						+ message
						+ "','"
						+ response
						+ "','"
						+ providerCode
						+ "','"
						+ currentTime
						+ "',false)";
				logger.debug("Insert into MOT Query : " + sql);
				stmt.executeUpdate(sql);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				releseConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void releseConnection() {
		try {
			if ((conn != null) && !(conn.isClosed()))
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
