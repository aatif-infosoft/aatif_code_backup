package operations;

import email.ConnectionPoolProvider;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseOperations {
	static final Logger logger = Logger.getLogger(DatabaseOperations.class);
	Connection connection = null;

	private void setDBConnection() {
		try {
			connection = ConnectionPoolProvider.getInstance()
					.getConnectionPool().getConnection();
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
		}
	}

	public JSONObject getDeliveryReport(int messageId) {
		String query = "Select * from MessagesOutTable where MsgId="
				+ messageId + " and UpdatedTime IS NOT NULL";
		logger.info("Get DeliveryReport for MsgId : " + query);
		ResultSet rs = null;
		Statement stmt = null;
		try {
			setDBConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("deliverystatus", rs.getString("DeliveryStatus"));
				return obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				releseConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void releseConnection() {
		try {
			if ((connection != null) && !(connection.isClosed()))
				connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
