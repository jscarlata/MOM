package tgm.jscarlata.controller;

import java.util.ArrayList;
import java.util.List;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import tgm.jscarlata.model.WarehouseData;

public class WarehouseReceiver {

  private Session session = null;
  private Connection connection = null;
  private MessageConsumer consumer = null;
  private Destination destination = null;

  public WarehouseReceiver(String subject) {
    System.out.println("Registration started.");

    try {
      ActiveMQConnectionFactory connectionFactory =
          new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                                        ActiveMQConnection.DEFAULT_PASSWORD,
                                        ActiveMQConnection.DEFAULT_BROKER_URL);
      connectionFactory.setTrustedPackages(
          List.of("tgm.jscarlata.model", "java.util"));
      connection = connectionFactory.createConnection();
      connection.start();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      destination = session.createTopic(subject);
      consumer = session.createConsumer(destination);
    } catch (Exception e) {
      System.out.println("[Initialization Registration] Caught: " + e);
      stop();
    }
  }

  public void stop() {
    try {
      consumer.close();
      session.close();
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ArrayList<WarehouseData> getMessage() {
    ArrayList<WarehouseData> msgList = new ArrayList<>();
    try {
      ObjectMessage message = (ObjectMessage)consumer.receiveNoWait();
      while (message != null) {
        WarehouseData value = (WarehouseData)message.getObject();
        System.out.println(value.toString());
        msgList.add(value);
        message.acknowledge();
        message = (ObjectMessage)consumer.receiveNoWait();
      }
    } catch (JMSException e) {
      System.err.println(e);
      return null;
    }
    System.out.println("Received " + msgList.size());
    return msgList;
  }
}
