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
  private static HashMap<String, WarehouseReceiver> clients = new HashMap<>();
  private static Registration instance = new Registration();

  private Session session = null;
  private Connection connection = null;
  private MessageConsumer consumer = null;
  private Destination destination = null;

  private Registration() {
    System.out.println("Receiver started.");

    try {
      ActiveMQConnectionFactory connectionFactory =
          new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                                        ActiveMQConnection.DEFAULT_PASSWORD,
                                        ActiveMQConnection.DEFAULT_BROKER_URL);
      connectionFactory.setTrustedPackages(
          List.of("tgm.jscarlata.warehouse.model", "java.util"));
      connection = connectionFactory.createConnection();
      connection.start();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      destination = session.createTopic("registration");
      consumer = session.createConsumer(destination);
    } catch (Exception e) {
      System.out.println("[Initialization Registration] Caught: " + e);
      stop();
    }
  }

  public static void stop() {
    try {
      instance.consumer.close();
      instance.session.close();
      instance.connection.close();
    } catch (Exception e) {
      System.err.println(e);
      return;
    }
  }

  public static WarehouseReceiver get(String key) { return clients.get(key); }

  public static Set<String> keys() { return clients.keySet(); }

  public static void updateRegistrations() {
    try {
      ObjectMessage message = (ObjectMessage)instance.consumer.receiveNoWait();
      while (message != null) {
        String value = (String) message.getObject();
        System.out.println("Message received: " + value);
        message.acknowledge();
        if (clients.get(value) == null)
          clients.put(value, new WarehouseReceiver(value));
        message = (ObjectMessage)instance.consumer.receiveNoWait();
      }
    } catch (JMSException e) {
      System.err.println(e);
      return;
    }
  }
}
