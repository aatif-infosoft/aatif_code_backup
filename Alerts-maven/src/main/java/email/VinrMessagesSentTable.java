package email;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VinrMessagesSentTable {

    public Connection connection = null;
    public PreparedStatement ps = null;
    static final Logger logger = Logger.getLogger(VinrMessagesSentTable.class);

    public void setConnection(Connection connection) {
    
        /*try {
            connection = ConnectionPoolProvider.getInstance().getConnectionPool().getConnection();
	} catch (SQLException sqlEx) {
            logger.debug("Unable to connect to database.");
	    sqlEx.printStackTrace();
        }*/
        this.connection = connection;

        if (connection == null) {
            logger.debug("The Connection from the Connection Pool is null therefore creating individual connection");
            connection = ConnectDatabase.establishConnection();
        }

    }

    //    public boolean insertDataIntoTable(int msgid, int msgtype, String ack, int retry_count, String primaryProvider, String secondaryProvider, String address, String message, String date, String customData) {		
    public boolean insertDataIntoTable(int msgid, int msgtype, String ack, String address, String message, String provider, String date, String customData) {   
        try {	
            //System.out.println("The ACK value in messages sent table is : " + ack);		
            //String sql = "INSERT INTO messages_sent_table(msgid, msgtype, responseId, retry_count, primaryProvider, secondaryProvider, address, message, received_date, customData) VALUES(?,?,?,?,?,?,?,?,?,?)";

            String sql = "INSERT INTO messages_sent_table(msgid, msgtype, responseId, address, message, provider, received_date, customData) VALUES(?,?,?,?,?,?,?,?)";
	    logger.info("Messages Sent Table The sql Query is ---> " + sql + " ConnectionPool address is " + ConnectionPoolProvider.getInstance().getConnectionPool().hashCode() + " Connection address is " + connection.hashCode());
            ps = connection.prepareStatement(sql); 
	    ps.setInt(1, msgid);
	    ps.setInt(2, msgtype);
	    ps.setString(3, ack);
	    ps.setString(4, address);				
	    ps.setString(5, message);
	    ps.setString(6, provider);	
	    ps.setString(7, date);
	    ps.setString(8, customData);

	    int rowCount = ps.executeUpdate();
 	    //System.out.println("The value of row count is : " + rowCount);

            return true;			
        } catch(SQLException e) {
            e.printStackTrace();
	    //System.out.println("Could not insert data into table");
            return false;
        }
    }

	
    public void releaseConnection() {

        try {
            if(ps != null)
	        ps.close();


            if((connection != null) && !(connection.isClosed()))
	        connection.close();
	} catch(Exception e) {
            e.printStackTrace();
        }
    }
}
