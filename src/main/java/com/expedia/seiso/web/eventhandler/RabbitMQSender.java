package com.expedia.seiso.web.eventhandler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Service
public class RabbitMQSender {
	
	private final ConnectionFactory factory;
	
	private Channel channel;
	
	private Connection connection;
	
	private final String queueName;
	
	@Autowired
	public RabbitMQSender(@Value("localhost") String mqServerAddress,
			@Value("SEISO") String queueName) throws IOException {
		factory = new ConnectionFactory();
		try {
			this.queueName = queueName;
			factory.setHost(mqServerAddress);
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(this.queueName, false, false, false, null);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public boolean sendMessage(String message){
	    try {
			channel.basicPublish("", this.queueName, null, message.getBytes());
			System.out.println(" [x] Sent '" + message + "'");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean close(){
		try {
			channel.close();
			connection.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	
}
