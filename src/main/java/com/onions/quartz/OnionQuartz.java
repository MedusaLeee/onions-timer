package com.onions.quartz;

import com.onions.utils.Utils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class OnionQuartz {
    private static Scheduler scheduler;

    public static void start() throws SchedulerException {
        if (scheduler == null) {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            System.out.println("OnionQuartz schedule start...");
        }
    }
    public static void scheduleJob(long startAt, String message) throws Exception {
        if (scheduler == null) {
            throw new Exception("OnionQuartz 必须先 start");
        }
        // 创建一个JobDetail实例，将该实例与HelloJob Class绑定
        String uuid = Utils.getUuid();
        JobDetail jobDetail = JobBuilder.newJob(TimerJob.class)
                .withIdentity("job:"+ uuid, "onion")
                .usingJobData("message", message)
                .build();
        System.out.printf("job time: " + Utils.formatDate(new Date(startAt)));
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger:"+ uuid, "onion")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow()) // 失效立即执行
                .startAt(new Date(startAt)).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
