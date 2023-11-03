package utils;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ForwardRequest {
	
	 static final Logger logger = Logger.getLogger(ForwardRequest.class);

	public static String connectAndReceive(String Url, String jsonString, String authenticationKey) {
		logger.debug("Url : "+Url+" and json : "+jsonString+" auth key : "+authenticationKey);
		String response = "";
		try {
			StringBuffer output = new StringBuffer("");
			InputStream stream = null;
			URL url = new URL(Url);
			System.out.println("URL : " + url);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoOutput(true);
			httpConnection.setConnectTimeout(50000);
			httpConnection.setReadTimeout(50000);
			httpConnection.setRequestProperty("Content-Type", "application/json");
			httpConnection.setRequestProperty("Authorization", "key=" + authenticationKey);
			httpConnection.connect();
			DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
			wr.writeBytes(jsonString);
			wr.flush();
			wr.close();
			System.out.println("Response Code :" + httpConnection.getResponseCode());
			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = httpConnection.getInputStream();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					output.append(s);
				}
				buffer.close();
				response = output.toString();
				return response;

			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return response;

	}

}
