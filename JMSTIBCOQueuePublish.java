import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.*;

public class JMSTIBCOQueuePublish {

	static String serverUrl = " tcp://dfwlndtibem-09.supermedia.com:7222,tcp://dfwlndtibem-11.supermedia.com:7222 | tcp://dfwlndtibem-10.supermedia.com:7222,tcp://dfwlndtibem-12.supermedia.com:7222"; // values changed
	static String userName = "vision";
	static String password = "vision";

	static QueueConnection connection;
	static QueueReceiver queueReceiver;
	static Queue queue;

	static TextMessage message;

	/**
	 * 
	 * @param queueName
	 * @param messageStr
	 */
	public static void sendQueueMessage(String queueName, String messageStr) {

		Connection connection 		= null;
		Session session 	 		= null;
		MessageProducer msgProducer = null;
		Destination destination 	= null;
		TextMessage msg				= null;

		try {
			
			System.out.println("Publishing to destination '" + queueName+ "'\n");

			ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(serverUrl);
			connection = factory.createConnection(userName, password);


			session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(queueName);

			msgProducer = session.createProducer(null);
			msg = session.createTextMessage();
			msg.setStringProperty("SourceId", userName);
			msg.setStringProperty("BusinessEvent", password);

			msg.setText(messageStr);
			msgProducer.send(destination, msg);

			System.out.println("Published message: " + messageStr);

		}catch (JMSException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(connection != null)
					connection.close();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	

	/**
	 * 
	 * @param topicName
	 * @param messageStr
	 */
	public static void retrieveQueueMessage(String queueName) {

		Connection connection 	= null;
		Session session 		= null;     
		Destination destination = null;


		try {
			TextMessage msg;

			System.out.println("Retrieving to destination '" + queueName+ "'\n");

			ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(serverUrl);
			connection = factory.createConnection(userName, password);




			session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(queueName);

			MessageConsumer msgConsumer = session.createConsumer(destination);            


			connection.start();

			/* read messages */
			while (true)
			{
				/* receive the message */
				msg = (TextMessage) msgConsumer.receive();
				if (msg == null)
					break;

				/*if (ackMode == Session.CLIENT_ACKNOWLEDGE ||
                    ackMode == Tibjms.EXPLICIT_CLIENT_ACKNOWLEDGE ||
                    ackMode == Tibjms.EXPLICIT_CLIENT_DUPS_OK_ACKNOWLEDGE)
                {
                    msg.acknowledge();
                }*/
				msg.acknowledge();
				System.err.println("Received message: "+ msg);
			}


		}catch (JMSException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(connection != null)
					connection.close();
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}
	}


	public static void main(String[] args) throws JMSException {
		JMSTIBCOQueuePublish.sendQueueMessage("DM.ENTERPRISE.VISION.TEST","Arijit21");
		//JMSTIBCOQueuePublish.retrieveQueueMessage("DM.ENTERPRISE.VISION.TEST");
		

	}
}