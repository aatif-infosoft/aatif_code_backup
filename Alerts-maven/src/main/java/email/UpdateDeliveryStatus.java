package email;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class UpdateDeliveryStatus extends RIJDBBaseThread {

	int threadid ;

	UpdateDeliveryStatus(int tid, String url, String username, String passwd){
	    super( tid, url, username, passwd ) ;
	    threadid = tid ;
    	}
 

	void process(Connection conn ){
		Statement stmt = null ;
		ResultSet rs = null ;
		try {
			stmt = conn.createStatement();
			String sql = "select * from deliveryreport where processed = false and s_no % 10 =" + threadid ;
			rs = stmt.executeQuery(sql);
			while (rs.next() == true){
	            String id = rs.getString("requestid");
	            String number = rs.getString("number");
	            String desc = rs.getString("description");
	            int s_no=rs.getInt("S_no");
	            System.out.println(id+"  "+ number +"   "+ desc + "   " + s_no);
	            /*String query = "insert into messagesouttable(Address,resid,deliveryStatus) values ('" +number+ "','"+ id + "','sent');";
	            stmt1.executeUpdate(query);
*/	          
	            Statement stmt1 = conn.createStatement();
	            String query1 = "update messagesouttable set deliverystatus = '" + desc
	            		+ "' where address ='" + number + "' and resid = '" + id + "'" ;
	            stmt1.executeUpdate(query1);
	            Statement stmt2 = conn.createStatement();
	           String query2 = "update deliveryreport set processed = true where s_no = " + s_no + ";";
	           stmt2.executeUpdate(query2);
      			}
		}
		catch( SQLException se ){
			se.printStackTrace() ;
		}
		finally{
		try{
			if ( rs != null )
      				rs.close();
			if( stmt != null )
      				stmt.close();	
		}
		catch(Exception e) {
			e.printStackTrace() ;
		}
		}
	}  
}
