package tgm.jscarlata.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Registration {
  private static HashMap<String, MOMReceiver> clients = new HashMap<>();
  private static Registration instance = new Registration();

  private Session session = null;
  private Connection connection = null;
  private MessageConsumer consumer = null;
  private Destination destination = null;

  private Registration() {
    try {
      // Set up ActiveMQ connection
      ActiveMQConnectionFactory connectionFactory =
              new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                      ActiveMQConnection.DEFAULT_PASSWORD,
                      ActiveMQConnection.DEFAULT_BROKER_URL);
      connectionFactory.setTrustedPackages(
              List.of("tgm.jscarlata.warehouse.model", "java.util"));
      connection = connectionFactory.createConnection();
      connection.start();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      destination = session.createQueue("registration");
      consumer = session.createConsumer(destination);
    } catch (Exception e) {
      // Handle initialization exceptions
      System.out.println("[Initialization Registration] Caught: " + e);
      stop();
    }
  }

  public static void stop() {
    try {
      // Close JMS connection and session
      instance.consumer.close();
      instance.session.close();
      instance.connection.close();
    } catch (Exception e) {
      // Handle closing exceptions
      System.err.println(e);
      return;
    }
  }

  public static void updateRegistrations() {
    try {
      // Receive and process JMS messages
      ObjectMessage message = (ObjectMessage)instance.consumer.receiveNoWait();
      while (message != null) {
        String value = (String) message.getObject();
        System.out.println("Message received: " + value);
        message.acknowledge();
        if (clients.get(value) == null)
          clients.put(value, new MOMReceiver(value));
        message = (ObjectMessage)instance.consumer.receiveNoWait();
      }
    } catch (JMSException e) {
      // Handle JMS exceptions
      System.err.println(e);
      return;
    }
  }
  public static MOMReceiver get(String key) { return clients.get(key); }

  public static Set<String> keys() { return clients.keySet(); }
}
