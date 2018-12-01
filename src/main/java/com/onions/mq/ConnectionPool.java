package com.onions.mq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionPool {
    private static Connection connection = null;
    static Connection getConnection () throws IOException, TimeoutException {
        if (connection != null) {
            return connection;
        }
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        connection = factory.newConnection();
        return connection;
    }
}
