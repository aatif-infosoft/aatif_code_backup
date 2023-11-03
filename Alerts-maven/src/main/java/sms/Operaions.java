package sms;

import email.ConnectionPoolProvider;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.sql.*;

public class Operaions {
	static final Logger logger = Logger.getLogger(Operaions.class);

	Connection connection = null;

	private void setDBConnection() {
		try {
			connection = ConnectionPoolProvider.getInstance()
					.getConnectionPool().getConnection();
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
		}
	}

	public int insertIntoMessageAggregation(int messageId, String accountId,
			String subAccountId, float cost) {
		int rowsAffected = 0;
		PreparedStatement preparedStatement = null;
		String insertQuery = "Insert into MessageAggregation (MsgId,SuccessCount,FailureCount,AccountId,SubAccountId,EachSMSCost) values(?,?,?,?,?,?)";
		try {
			logger.info("Insert into MessageAggregation Query : " + insertQuery);
			setDBConnection();
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setInt(1, messageId);
			preparedStatement.setInt(2, 0);
			preparedStatement.setInt(3, 0);
			preparedStatement.setString(4, accountId);
			preparedStatement.setString(5, subAccountId);
			preparedStatement.setFloat(6, cost);
			rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
				releaseConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rowsAffected;
	}

	public void releaseConnection() {
		try {
			if (connection != null && !(connection.isClosed()))
				connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void performAction(String query, Connection dbConnection) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = dbConnection.createStatement();
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public JSONArray getMessagesOutTableDetails(String query,
			Connection connection) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);

			JSONObject obj = new JSONObject();
			JSONArray array = new JSONArray();
			while (rs.next()) {
				obj = new JSONObject();
				obj.put("id", rs.getInt("id"));
				obj.put("msgid", rs.getInt("MsgId"));
				obj.put("deliverystatus", rs.getString("DeliveryStatus"));
				array.add(obj);
			}
			return array;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
