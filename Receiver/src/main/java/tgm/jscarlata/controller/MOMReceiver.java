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

public class MOMReceiver {

  private Session session = null;
  private Connection connection = null;
  private MessageConsumer consumer = null;
  private Destination destination = null;

  public MOMReceiver(String subject) {
    System.out.println("Registration started.");

    try {
      // Set up ActiveMQ connection for the WarehouseReceiver
      ActiveMQConnectionFactory connectionFactory =
              new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                      ActiveMQConnection.DEFAULT_PASSWORD,
                      ActiveMQConnection.DEFAULT_BROKER_URL);
      connectionFactory.setTrustedPackages(
              List.of("tgm.jscarlata.model", "java.util"));
      connection = connectionFactory.createConnection();
      connection.start();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      destination = session.createQueue(subject);
      consumer = session.createConsumer(destination);
    } catch (Exception e) {
      // Handle initialization exceptions
      System.out.println("[Initialization WarehouseReceiver] Caught: " + e);
      stop();
    }
  }

  // Method to stop the WarehouseReceiver
  public void stop() {
    try {
      consumer.close();
      session.close();
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Method to receive and process JMS messages
  public ArrayList<WarehouseData> getMessage() {
    ArrayList<WarehouseData> msgList = new ArrayList<>();
    try {
      ObjectMessage message = (ObjectMessage)consumer.receiveNoWait();
      while (message != null) {
        // Extract WarehouseData from the JMS message
        WarehouseData value = (WarehouseData)message.getObject();
        System.out.println(value.toString());
        msgList.add(value);
        // Acknowledge the receipt of the message
        message.acknowledge();

        // ack
        sendAcknowledgment(value);

        message = (ObjectMessage)consumer.receiveNoWait();

      }
    } catch (JMSException e) {
      System.err.println(e);
      return null;
    }
    System.out.println("Received " + msgList.size());
    return msgList;
  }

  private void sendAcknowledgment(WarehouseData data) {
    try {
      // Create the acknowledgment sender
      MOMSender acknowledgmentSender = new MOMSender("ACKNOWLEDGMENT_QUEUE");

      // Send acknowledgment message
      acknowledgmentSender.sendMessage("Received: " + data.toString());

      // Stop the acknowledgment sender after sending the message
      acknowledgmentSender.stop();
    } catch (Exception e) {
      System.err.println("Error sending acknowledgment: " + e);
    }
  }
}
