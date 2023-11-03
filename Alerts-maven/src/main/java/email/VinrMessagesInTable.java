package email;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import alerts.utils.Constants;

/**
 * This class connects the code for getting the connection from the connection
 * pool and query the messagesintable.
 * 
 * @author Sushma P
 * @date July-2-2010
 * @version 1.0
 */

public class VinrMessagesInTable {

	private Connection connection = null;
	private Statement stmt = null;
	static final Logger logger = Logger.getLogger(VinrMessageParser.class);

	public void setConnection() {

		try {
			connection = ConnectionPoolProvider.getInstance()
					.getConnectionPool().getConnection();
		} catch (SQLException sqlEx) {
			// System.out.println("Unable to connect to database.");
			sqlEx.printStackTrace();
		}

		if (connection == null) {
			logger.debug("The Connection from the Connection Pool is null therefore creating individual connection");
			connection = ConnectDatabase.establishConnection();
		}
	}

	public ResultSet readNewMessages(int threadId) {
		logger.debug("Is connection null? " + (connection == null));

		int THREAD_POOL_SIZE = ConnectionPoolProvider.getInstance()
				.getThreadPoolSize();
		try {
			stmt = connection.createStatement();

			String QueryString = new StringBuffer(
					"select MsgId, MsgType, Address, Message, Version, Provider, Attachment, Date, CustomData,AccountId,SubAccountId,Cost from MessagesInTable where (")
					.append("Date <= sysdate() and ((").append("MsgId % ")
					.append(THREAD_POOL_SIZE).append(")").append(" = ")
					.append(threadId).append("))").toString();
			logger.debug("The query is ===> "
					+ QueryString
					+ " Connection pool address is "
					+ ConnectionPoolProvider.getInstance().getConnectionPool()
							.hashCode() + " Connection address is "
					+ connection.hashCode());
			// System.out.println("The query is ===> " + QueryString +
			// " Connection pool address is " +
			// ConnectionPoolProvider.getInstance().getConnectionPool().hashCode()
			// + " Connection address is " +
			// connection.hashCode()+THREAD_POOL_SIZE+"----"+threadId);
			ResultSet rs = stmt.executeQuery(QueryString);
			return rs;

		} catch (Exception e) {
			// System.out.println("Unable to connect to database.");
			e.printStackTrace();
			return null;
		}
	}

	public int deleteMessage(int msgId) {

		try {
			int deleteStatus;

			stmt = connection.createStatement();
			String QueryString = "delete from MessagesInTable where MsgId = \'"
					+ msgId + "\'";
			// System.out.println(QueryString);
			deleteStatus = stmt.executeUpdate(QueryString);
			return deleteStatus;
		} catch (Exception e) {
			// System.out.println("Unable to connect to database.");
			e.printStackTrace();
			return -1;
		}
	}

	public int touchMessage(int msgId, long postpone_secs) {

		try {
			int touchStatus;

			stmt = connection.createStatement();
			String QueryString = "update MessagesInTable set date = DATE_ADD(sysdate(), INTERVAL "
					+ postpone_secs
					+ " second) where MsgId = \'"
					+ msgId
					+ "\'";
			// System.out.println(QueryString);
			touchStatus = stmt.executeUpdate(QueryString);
			return touchStatus;
		} catch (Exception e) {
			// System.out.println("Unable to connect to database.");
			e.printStackTrace();
			return -1;
		}
	}

	public void releaseConnection() {

		try {
			if (stmt != null)
				stmt.close();

			if ((connection != null) && !(connection.isClosed()))
				connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}
}
