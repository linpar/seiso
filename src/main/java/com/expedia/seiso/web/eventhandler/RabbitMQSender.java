package com.expedia.seiso.web.eventhandler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RabbitMQSender {
	
	private final ConnectionFactory factory;
	
	private Channel channel;
	
	private Connection connection;
	
	private final String queueName;
	
	public static final String NODE_UPDATE_MESSAGE = "TBD";

	public static final String SERVICE_INSTANCE_UPDATE_MESSAGE = "TBD";
	
	public static final String MQ_CHANNEL_NAME = "CHANNEL";
	
	@Autowired
	public RabbitMQSender(@Value("localhost") String mqServerAddress,
			@Value(MQ_CHANNEL_NAME) String queueName) throws IOException {
		factory = new ConnectionFactory();
		try {
			this.queueName = queueName;
			this.factory.setHost(mqServerAddress);
			this.connection = factory.newConnection();
			this.channel = connection.createChannel();
			this.channel.queueDeclare(this.queueName, false, false, false, null);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public boolean sendMessage(String message){
	    try {
			channel.basicPublish("", this.queueName, null, message.getBytes());
			log.info("MQ message Sent '" + message + "'");
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
			log.info("MQ connection closed");
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	
}
