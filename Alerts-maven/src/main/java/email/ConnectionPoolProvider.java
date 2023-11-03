package email;

import org.apache.log4j.Logger;
import snaq.db.ConnectionPool;

import java.io.FileInputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

/** This is a Singleton class that contains the reference
 *  to the ConnectionPool object.  The implementation
 *  of the connection pooling is done by the third party
 *  software of http://www.snaq.net/java/DBPool.  Initially,
 *  a static initializer was written to create a DataSource
 *  object.  This has been commented out.  Later on, the
 *  creation of the ConnectionPool object was coded in the
 *  private Constructor of this class.
 *
 *  @author Sunil Tuppale
 *  @date July-19-2010
 *  @version 1.0
 */

public class ConnectionPoolProvider {

    private static final ConnectionPoolProvider INSTANCE = new ConnectionPoolProvider();
    //private static DBPoolDataSource dataSource = null;
    static final Logger logger = Logger.getLogger(ConnectionPoolProvider.class);
    private int THREAD_POOL_SIZE;
    private ConnectionPool pool = null;

    /** The private constructor of the class in which the ConnectionPool
     *  is initialized by reading all the parameters to be passed to it
     *  from the 'databaseSettings.ini' file
     */

    private ConnectionPoolProvider() {
        Properties p = null;
    
        try {
            String fileName = System.getenv("VINR_CONFIG_FILE");
            if (fileName == null) {
                //System.out.println("Env. Variable VINR_CONFIG_FILE is not set, using default file vinralerts.properties") ;
                //fileName="vinralerts.properties" ;
            }

            p = new Properties(System.getProperties());
            FileInputStream propFile = new FileInputStream(fileName);
            p.load(propFile);

            Class c = Class.forName(p.getProperty("driver"));
            Driver driver = (Driver)c.newInstance();
            DriverManager.registerDriver(driver);

            String poolName = p.getProperty("connPoolName");
            int minPool = Integer.parseInt(p.getProperty("minpool"));
            int maxPool = Integer.parseInt(p.getProperty("maxpool"));
            int maxSize = Integer.parseInt(p.getProperty("maxsize"));
            long idleTimeout = Long.parseLong(p.getProperty("idleTimeout"));
            String url = p.getProperty("url");
            String user = p.getProperty("userName");
            String password = p.getProperty("password");

            pool = new ConnectionPool(poolName, minPool, maxPool, maxSize, idleTimeout, url, user, password);

            THREAD_POOL_SIZE = Integer.parseInt(p.getProperty("THREAD_POOL_SIZE"));

        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Error opening properties file."+e);
        }
    }

    /*static {
        Properties p = null;
        try {
            String fileName = System.getenv("VINR_CONFIG_FILE");
            if (fileName == null) {
                //System.out.println("Env. Variable VINR_CONFIG_FILE is not set, using default file vinralerts.properties") ;
                //fileName="vinralerts.properties" ;
            }

            p = new Properties(System.getProperties());
            FileInputStream propFile = new FileInputStream(fileName);
            p.load(propFile);
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Error opening properties file."+e);
        }

        try {
            dataSource = new DBPoolDataSource();
            dataSource.setName(p.getProperty("connPoolName"));
            dataSource.setDescription(p.getProperty("description"));
            dataSource.setDriverClassName(p.getProperty("driver"));
            dataSource.setUrl(p.getProperty("url"));
	    String dUrl = p.getProperty("url");
	    logger.debug("In ConnectionPooling Class , the value of URL is =======>      "  + dUrl);	
            dataSource.setUser(p.getProperty("userName"));
            dataSource.setPassword(p.getProperty("password"));
            dataSource.setMinPool(Integer.parseInt(p.getProperty("minpool")));
            dataSource.setMaxPool(Integer.parseInt(p.getProperty("maxpool")));
            dataSource.setMaxSize(Integer.parseInt(p.getProperty("maxsize")));
            dataSource.setIdleTimeout(Integer.parseInt(p.getProperty("idleTimeout")));  
            dataSource.setValidationQuery("SELECT COUNT(*) FROM messagesintable");

            THREAD_POOL_SIZE = Integer.parseInt(p.getProperty("THREAD_POOL_SIZE"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }*/

    public static ConnectionPoolProvider getInstance() {
	return INSTANCE;
    }

   /*
    public static DBPoolDataSource getDataSource() {
        return dataSource;
    }*/

    public ConnectionPool getConnectionPool() {
        return pool;
    }

    public int getThreadPoolSize() {
        return THREAD_POOL_SIZE;
    }
}
