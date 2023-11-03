package Threads;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class BaseThread implements Runnable {
	int threadid;
	private Connection dbConnection = null;

	public BaseThread(int s, String url, String username, String passwd) {
		threadid = s;
		try {
			dbConnection = (Connection) DriverManager.getConnection(url,
					username, passwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		processmessage();
	}

	public void processmessage() {
		try {
			process(dbConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	abstract void process(Connection dbConn);

}
