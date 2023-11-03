package email;

import java.sql.Connection;
import java.sql.DriverManager;

abstract class RIJDBBaseThread implements Runnable {
    private String message;
    int threadid ;
    private Connection  dbConnection = null ;
    public RIJDBBaseThread(int s, String url, String username, String passwd){
	threadid = s ;
	try{
		dbConnection = DriverManager.getConnection(url, username, passwd) ;
	}
	catch(Exception e){
		e.printStackTrace() ;
	}
        this.message=""+s;
    }
 

    public void run() {
	while( true ) {
        	processmessage();
		try{
			Thread.sleep(5000) ;
		}
		catch(Exception e){
			e.printStackTrace(); 
		}
	}
    }
 
    public void processmessage() {
        try {
		  process(dbConnection) ;
  	} 
	catch (Exception e) { 	
		e.printStackTrace(); 
	}
    }

	abstract void process(Connection dbConn )  ;
}

