package tgm.jscarlata.controller;

import java.io.Serializable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MOMSender {

  private static String user = ActiveMQConnection.DEFAULT_USER;
  private static String password = ActiveMQConnection.DEFAULT_PASSWORD;
  private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

  Session session = null;
  Connection connection = null;
  MessageProducer producer = null;
  Destination destination = null;

  public MOMSender(String subject) {

    System.out.println("Sender started.");

    // Create the connection.
    try {

      ConnectionFactory connectionFactory =
          new ActiveMQConnectionFactory(user, password, url);
      connection = connectionFactory.createConnection();
      connection.start();

      // Create the session
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      destination = session.createTopic(subject);

      // Create the producer.
      producer = session.createProducer(destination);
      producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

      // Create the message
    } catch (Exception e) {
      System.out.println("[MessageProducer] Caught: " + e);
      e.printStackTrace();
    }
    System.out.println("Sender finished.");
  }

  public void stop() {
    try {
      producer.close();
      session.close();
      connection.close();
    } catch (Exception e) {
      System.err.println("[Failed while closing] " + e);
    }
  }

  public void sendMessage(Serializable obj) {
    try {

      ObjectMessage message = session.createObjectMessage(obj);
      producer.send(message);
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }
}
