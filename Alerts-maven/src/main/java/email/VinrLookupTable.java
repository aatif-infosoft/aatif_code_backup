package email;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/** This class is for getting the extension of the attachment in an email
 *  (like .gif, .pdf, .jpeg, .txt, etc )and setting the appropriate
 *  content-type by looking up from the lookup table.
 *  @author Sushma P
 *  @date July-2-2010
 *  @version 1.0
 */

public class VinrLookupTable {

    public Connection connection = null;
    public Statement stmt = null;
    private static HashMap hm = null;
    static final Logger logger = Logger.getLogger(VinrLookupTable.class);

    /** This routine is for establishing a connection with the database
     *  using the datasource
     */
    
    public void setConnection()	{
    
        try {
            connection = ConnectionPoolProvider.getInstance().getConnectionPool().getConnection();
	} catch (SQLException sqlEx) {
            //System.out.println("Unable to connect to database.");
	    sqlEx.printStackTrace();
	}

        if (connection == null) {
            logger.debug("The Connection from the Connection Pool is null therefore creating individual connection");
            connection = ConnectDatabase.establishConnection();
        }

    }

    /** This routine is to get the extension, content-Type from the lookup table and 
     *  add them to the HashMap.The extension is stored as a 'key' and content-Type 
     *  is stored as a 'value' object in the HashMap.   
     */
    public void readLookupTable() {
        
        try {
            stmt = connection.createStatement();		
            String cType = "select Extension, ContentType from LookupTable";	
	
            ResultSet rs = stmt.executeQuery(cType);
	    hm = new HashMap() ;

            while(rs.next()) {
                hm.put(rs.getString(1) , rs.getString(2));
                //System.out.println("in the result set of vinrlookup table"+ rs.getString(2));				
	    }

            //Set keys = hm.keySet();         // The set of keys in the map.
	    //Iterator keyIter = keys.iterator();
	    //System.out.println("The map contains the following associations:");
	    //while (keyIter.hasNext()) {
            //    System.out.println("In the Key Iterator of vinr lookup table");
            //    Object key = keyIter.next();  // Get the next key.
	    //    //String extn = key.toString();
	    //    Object value = hm.get(key);  // Get the value for that key.
	    //    System.out.println( "   (" + key + "," + value + ")" );
	    //    System.out.println("The value of Key is :" + value);
	    //}
            
            //return rs;
         
        } catch (Exception e) {
            //System.out.println("Unable to connect to database.");
            e.printStackTrace();
            //return null;
        }
    }

    /**   This routine iterates through the contents of HashMap and get the 
     *    content-Type(which is the 'value' object) for the coressponding 
     *    extension(which is the 'key' object).
     *    @param input String to get the corressponding content-Type for the 
     *    extension passed.
     *    @return content-Type for the given extension   
     */
    public static String getContent( Object extn ) {
        
        String content = "application/octet-stream";
        if (hm != null && extn != null ) {
	    content = hm.get( extn ).toString() ;
	    //System.out.println("In the LookUpTable Class - The content type is : " + content);		
        }
        return content ;
    }

    /** Release all the resources */
    public void releaseConnection() {
        
        try {
            if(stmt != null)
                stmt.close();
            
            if((connection != null) && !(connection.isClosed()))
                connection.close();
		
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
