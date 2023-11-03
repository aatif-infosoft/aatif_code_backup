package sms;

import net.sf.json.JSONObject;
import operations.DatabaseOperations;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import javax.jms.*;

public class DeliveryReport implements Runnable {
	static final Logger logger = Logger.getLogger(DeliveryReport.class);
	DatabaseOperations DBO = new DatabaseOperations();

	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
						"tcp://192.168.0.22:61616");
				Connection connection = connectionFactory.createConnection();
				connection.start();
				Session session = connection.createSession(false,
						Session.AUTO_ACKNOWLEDGE);
				Destination destination = session.createQueue("RequestQueue");
				MessageConsumer messageConsumer = session
						.createConsumer(destination);
				Message message = messageConsumer.receive();
				String jsondata = null;
				if (message instanceof TextMessage) {
					TextMessage tm = (TextMessage) message;
					jsondata = tm.getText();
					logger.info("Received request by Delivery report class : "
							+ jsondata);
				}
				messageConsumer.close();
				if (jsondata != null) {
					JSONObject obj = JSONObject.fromObject(jsondata);
					String uniqueID = obj.getString("uniqueid");
					int messageID = obj.getInt("messageid");
					JSONObject resObj = DBO.getDeliveryReport(messageID);
					if (resObj == null) {
						resObj = new JSONObject();
						resObj.put("deliverystatus", "FAILURE");
					}
					resObj.put("uniqueid", uniqueID);
					String responseData = resObj.toString();
					logger.info("sent response : " + responseData);
					Destination destinationOne = session
							.createQueue("ResponseQueue");
					MessageProducer messageProducer = session
							.createProducer(destinationOne);
					messageProducer
							.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
					TextMessage ts = session.createTextMessage(responseData);
					messageProducer.send(ts);
					session.close();
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread.yield();
		}
	}
}
