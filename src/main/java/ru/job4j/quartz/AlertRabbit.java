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

    public static void main(String[] args) {
        Properties cfg = getConfig("rabbit.properties");
        try (Connection cn = getConnection(cfg)) {
            try (Statement st = cn.createStatement()) {
                st.execute("truncate rabbit");
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
            }
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

    public static Properties getConfig(String path) {
        Properties cfg = new Properties();
        try (InputStream in = AlertRabbit.class
                .getClassLoader().getResourceAsStream(path)) {
            cfg.load(in);
        } catch (IOException ex) {
            throw new IllegalArgumentException();
        }
        return cfg;
    }

    public static Connection getConnection(Properties cfg) {
        Connection cn = null;
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password"));
        } catch (ClassNotFoundException | SQLException exception) {
            exception.printStackTrace();
        }
        return cn;
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection cnFromContext = (Connection) context.
                    getJobDetail().getJobDataMap().get("connection");
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