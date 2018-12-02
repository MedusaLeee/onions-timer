package com.onions.mq;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TimerTaskProducer {
    private String queueName = "timerConsumerQ";
    private Channel channel;
    private static TimerTaskProducer producer;

    private TimerTaskProducer() throws IOException, TimeoutException {
        this.channel = ConnectionPool.getConnection().createChannel(); //创建信道
        channel.queueDeclare(this.queueName, true, false, false, null);
        this.channel.confirmSelect();
        //异步 confirm 方法  // https://blog.csdn.net/u013256816/article/details/55515234
        this.channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) {
                System.out.println("收到消息确认回调, deliveryTag: " + deliveryTag + ",multiple: " + multiple);
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) {
                System.out.println("收到消息丢失回调, deliveryTag: " + deliveryTag + ",multiple: " + multiple);
            }
        });
    }

    public void sendMessage (String message) throws IOException {
        // 监听一个发过来的定时消息，设置quartz定时任务
        long nextSeqNo = this.channel.getNextPublishSeqNo();
        channel.basicPublish("", this.queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        System.out.println("TimerTaskProducer nextSeqNo: " + nextSeqNo );
    }
    public static TimerTaskProducer getInstance() throws IOException, TimeoutException {
        if (producer == null) {
            producer = new TimerTaskProducer();
            return producer;
        }
        return producer;
    }
}
