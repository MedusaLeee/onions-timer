package com.onions;

import com.onions.mq.TimerTaskConsumer;
import com.onions.quartz.OnionQuartz;
import org.quartz.SchedulerException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class App {
    public static void main( String[] args ) throws IOException, TimeoutException, SchedulerException {
        // 启动quartz
        OnionQuartz.start();
        String consumerQueueName = "timerProducerQ";
        int basicQos = 50;
        TimerTaskConsumer timerTaskConsumer = new TimerTaskConsumer(consumerQueueName, basicQos);
        timerTaskConsumer.start();
    }
}
