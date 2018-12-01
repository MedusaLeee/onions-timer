package com.onions;

import com.onions.mq.TimerTaskConsumer;
import com.onions.mq.TimerTaskProducer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class App {
    public static void main( String[] args ) throws IOException, TimeoutException {
        String consumerQueueName = "timerProducerQ";
        String producerQueueName = "timerConsumerQ";
        int basicQos = 50;
        TimerTaskProducer taskProducer = new TimerTaskProducer(producerQueueName);
        TimerTaskConsumer timerTaskConsumer = new TimerTaskConsumer(consumerQueueName, basicQos, taskProducer);
        timerTaskConsumer.start();
    }
}
