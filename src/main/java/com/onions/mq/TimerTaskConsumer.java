package com.onions.mq;


import com.alibaba.fastjson.JSONObject;
import com.onions.quartz.OnionQuartz;
import com.onions.utils.Utils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class TimerTaskConsumer {
    private String queueName;
    private int basicQos = 50;
    private final String consumerName = "TimerTaskConsumer";

    public TimerTaskConsumer(String queueName, int basicQos) {
        this.queueName = queueName;
        this.basicQos = basicQos;
    }
    public void start () throws IOException, TimeoutException {
        // 监听一个发过来的定时消息，设置quartz定时任务
        final Channel channel = ConnectionPool.getConnection().createChannel(); //创建信道
        channel.queueDeclare(this.queueName, true, false, false, null);
        channel.basicQos(this.basicQos); //设置客户端最多接收未确认消息个数,当消费者确认后RabbitMQ才会继续向该消费者发送消息
        boolean autoAck = false; //设置为false,RabbitMQ会等待消费者确认消费后才移除消息
        channel.basicConsume(queueName, autoAck, this.consumerName, new DefaultConsumer(channel) {
            @Override
            //Broker 向消费者推送消息,处理RabbitMQ推送过来的消息
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String messageBody = new String(body);
                System.out.println("TimerTaskConsumer consumerTag: " + consumerTag + ", deliveryTag: " + envelope.getDeliveryTag() + ", body:" + messageBody );
                JSONObject messageObject = Utils.jsonStringToJSONObject(messageBody);
                long startAt = messageObject.getLongValue("startAt");
                System.out.println("basicConsume startAt: " + startAt);
                try {
                    OnionQuartz.scheduleJob(startAt, messageBody);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 消费后向Broker发送确认消息
                channel.basicAck(envelope.getDeliveryTag(), false);
                // channel.basicReject(envelope.getDeliveryTag(), false); // 第二个参数设置成true会被重新入队
            }
        });
        System.out.println( "onions timer start..." );
    }

}
