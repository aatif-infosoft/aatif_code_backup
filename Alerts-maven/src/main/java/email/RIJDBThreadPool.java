package email;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RIJDBThreadPool {
 
	static String dbusername = "root" ;
	static String dbpassword = "password";
	static String dburl = "jdbc:mysql:/"+"/localhost:3306/groupzivr" ;	
	static String jdbcDriver = "com.mysql.jdbc.Driver";
	static int noofthreads = 10 ;

	public static void init(){
		// initialize the static variables here 

	}

    public static void main(String[] args) {
	
		try{
			Class.forName( jdbcDriver ) ;
		}catch(Exception e){
		}
        ExecutorService executor = Executors.newFixedThreadPool(noofthreads);
        for (int i = 0; i < 10; i++) {
            Runnable worker = new UpdateDeliveryStatus( i , dburl, dbusername, dbpassword  );
            executor.execute(worker);
          }
        executor.shutdown();
        while (!executor.isTerminated()) {
		try{
			Thread.sleep(1000) ;
		}catch(Exception e){
		}
        }
        System.out.println("Finished all threads");
    }
 
}
