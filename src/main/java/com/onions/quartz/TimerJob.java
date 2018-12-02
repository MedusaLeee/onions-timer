package com.onions.quartz;

import com.onions.mq.TimerTaskProducer;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class TimerJob implements Job {
    private String message;
    private Date startAt;
    private String queueName;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            TimerTaskProducer.getInstance().sendMessage(this.message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
