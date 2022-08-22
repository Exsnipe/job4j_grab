package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

public class AlertRabbit {
    private static Properties cfg;
    private static Connection cn;

    public static void main(String[] args) {
        init();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", cn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void init() {
        cfg = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            cfg.load(in);
        } catch (NullPointerException | IOException ex) {
            throw new IllegalArgumentException();
        }
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(cfg.getProperty("url"),
                    cfg.getProperty("username"), cfg.getProperty("password"));
            try (Statement st = cn.createStatement()) {
                st.execute("truncate rabbit");
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
            }
        } catch (ClassNotFoundException | SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection cnFromContext = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement ps = cnFromContext
                   .prepareStatement("insert into rabbit (created_date) values (?)")) {
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.execute();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }
}