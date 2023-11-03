package email;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author palithya
 */
public class ConnectDatabase {

    private static Connection myConnection = null;
    static final Logger logger = Logger.getLogger(ConnectDatabase.class);

    public static Connection establishConnection() {
  
        if (myConnection == null) {
            String url = null;
            String dbName = null;
            String driver = null;
            String userName = null;
            String password = null;

            try {
                String fileName = System.getenv("VINR_CONFIG_FILE");
                if (fileName == null) {
                    logger.debug("Env. Variable VINR_CONFIG_FILE is not set, using default file vinralerts.properties");
                    fileName="vinralerts.properties";
                }

                Properties p = new Properties(System.getProperties());
                FileInputStream propFile = new FileInputStream(fileName);
                p.load(propFile);
                url = p.getProperty("url");
                dbName =p.getProperty("dbName");
                driver =p.getProperty("driver");
                userName = p.getProperty("userName") ;
                password = p.getProperty("password") ;
            } catch (Exception e) {
                logger.debug("Error opening properties file."+e);
            }

            try {
                Class.forName(driver).newInstance();
                myConnection = DriverManager.getConnection(url,userName,password);
                logger.debug("In ConnectDatabase.java : The url and dbname is : " + url+dbName);
                logger.debug("Connected to the database");
            } catch(SQLException sqlException) {
                sqlException.printStackTrace();
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
        return myConnection ;
    }

    public void closeConnection(Connection connection) {
        
        try {
            connection.close();
            logger.debug("Disconnected from database");
        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }
}
		
