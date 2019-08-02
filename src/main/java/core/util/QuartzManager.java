package core.util;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 定时任务管理类, 用此类就不要用配置文件那个了
 * @author qwx
 *
 */
public class QuartzManager {
  
  private static SchedulerFactory gSchedulerFactory = null;  
  private static String JOB_GROUP_NAME = "EXTJWEB_JOBGROUP_NAME";  
  private static String TRIGGER_GROUP_NAME = "EXTJWEB_TRIGGERGROUP_NAME"; 
  private static Scheduler sched = null;
  
  public static void init() {
    try {
      gSchedulerFactory = gSchedulerFactory==null ? new StdSchedulerFactory(): gSchedulerFactory;
      sched = (sched==null)? gSchedulerFactory.getScheduler() : sched;
    } catch (SchedulerException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
   
  
  /**
   * 查找JOB，使用默认的任务名称
   * @param jobName 任务名
   * @return
   */
  public static JobDetail findJob(String jobName) {
    try {
      JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
      JobDetail jobDetail = sched.getJobDetail(jobKey); 
      return jobDetail;
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 查找JOB
   * @param jobName 任务名
   * @param jobGroupName 任务组名称
   * @return
   */
  public static JobDetail findJob(String jobName, String jobGroupName){
    try {
      JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
      JobDetail jobDetail = sched.getJobDetail(jobKey); 
      return jobDetail;
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  
  /**
   * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
   * @param jobName 任务名称
   * @param cls 任务类
   * @param time 触发时间
   */
  public static void addJob(String jobName, Class<? extends Job> cls, String time) {  
    try {
      JobDetail detail = JobBuilder.newJob(cls).withIdentity(jobName, JOB_GROUP_NAME).build();// 任务名，任务组，任务执行类
      Trigger trigger =  TriggerBuilder.newTrigger().forJob(detail)
                        .withIdentity("trigger_"+jobName, TRIGGER_GROUP_NAME)
                        .withSchedule( CronScheduleBuilder.cronSchedule(time))
                        .build();
      sched.scheduleJob(detail, trigger);  
     // 启动  
      if (!sched.isShutdown()) {  
          sched.start();  
      } 
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
   * @param jobName 任务名称
   * @param jobGroupName 任务组
   * @param triggerName  触发器名称
   * @param triggerGroupName 触发器组
   * @param jobClass  任务类
   * @param time  执行时间
   */
  public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
      Class<? extends Job> jobClass, String expressTime) {
    try {
      JobDetail jobDetail = JobBuilder.newJob(jobClass)// 任务名，任务组，任务执行类
                           .withIdentity(jobName, jobGroupName)
                           .build();
      Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)// 触发器名,触发器组
                        .withIdentity(triggerName, triggerGroupName)
                        .withSchedule(CronScheduleBuilder.cronSchedule(expressTime))
                        .build();
      // 触发器
      sched.scheduleJob(jobDetail, trigger);
      if (!sched.isShutdown()) {
        sched.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名) 
   * @param jobName 任务名称
   * @param time    执行时间
   */
  public static void modifyJobTime(String jobName, String time) {
    try {
      TriggerKey triggerKey = TriggerKey.triggerKey("trigger_"+jobName, TRIGGER_GROUP_NAME);
      CronTrigger trigger = (CronTrigger)sched.getTrigger(triggerKey);
      if ( trigger == null ) {
        return;
      }
      String oldTime = trigger.getCronExpression(); //老的时间
      if ( !oldTime.equals(time) ) {
        JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
        JobDetail jobDetail = sched.getJobDetail(jobKey);  
        Class<? extends Job> objJobClass = jobDetail.getJobClass();
        removeJob(jobName);  //删除此job
        addJob(jobName, objJobClass, time); //加入新的job
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 修改一个任务的触发时间
   * @param jobName 任务名称
   * @param jobGroupName 任务组名
   * @param triggerName 触发器名
   * @param triggerGroupName 触发器组名
   * @param expressTime 执行时间
   */
  public static void modifyJobTime(String jobName, String jobGroupName, String triggerName
                                               , String triggerGroupName, String expressTime) {
    try {
      TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
      CronTrigger trigger = (CronTrigger)sched.getTrigger(triggerKey);
      if ( trigger == null ) {
        return;
      }
      String oldTime = trigger.getCronExpression(); //老的时间
      if ( !oldTime.equals(expressTime) ) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        JobDetail jobDetail = sched.getJobDetail(jobKey);  
        Class<? extends Job> objJobClass = jobDetail.getJobClass();
        removeJob(jobName, jobGroupName, triggerName, triggerGroupName);//删除此job
        addJob(jobName, jobGroupName, triggerName, triggerGroupName,
            objJobClass, expressTime);//加入新的job
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 移除一个任务，使用默认的任务组名，触发器名，触发器组名
   * @param jobName
   */
  public static void removeJob(String jobName) {
    try {
      TriggerKey triggerKey = TriggerKey.triggerKey("trigger_"+jobName, TRIGGER_GROUP_NAME);//new TriggerKey(triggerName);
      sched.pauseTrigger(triggerKey);  // 停止触发器  
      sched.unscheduleJob(triggerKey); // 移除触发器  
      JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
      sched.deleteJob(jobKey);//删除任务  
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  
  /**
   * 移除一个任务 
   * @param jobName
   * @param jobGroupName
   * @param triggerName
   * @param groupName
   */
  public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
    try {
      TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);//new TriggerKey(triggerName);
      sched.pauseTrigger(triggerKey);  // 停止触发器  
      sched.unscheduleJob(triggerKey); // 移除触发器  
      JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
      sched.deleteJob(jobKey);//删除任务  
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  /**
   * 暂停job，
   * @param jobName 任务名称
   * @param jobGroupName 任务组名称
   */
  public static void pauseJob(String jobName, String jobGroupName) {
    try {
      JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
      sched.pauseJob(jobKey);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 暂停job，使用默认的任务组名
   * @param jobName 任务名称
   */
  public static void pauseJob(String jobName) {
    try {
      JobKey jobKey = JobKey.jobKey(jobName, TRIGGER_GROUP_NAME);
      sched.pauseJob(jobKey);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 暂停触发器
   * @param triggerName
   * @param triggerGroupName
   */
  public static void pauseTrigger(String triggerName, String triggerGroupName) {
    try {
      TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
      sched.pauseTrigger(triggerKey); //暂停触发器
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 暂停执行,使用默认的任务名，触发器名，触发器组名
   * @param jobName 任务名称
   */
  public static void pauseTrigger(String jobName) {
    try {
      TriggerKey triggerKey = TriggerKey.triggerKey("trigger_"+jobName, TRIGGER_GROUP_NAME);
      sched.pauseTrigger(triggerKey); //暂停触发器
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 重启触发器
   * @param triggerName 触发器名称
   * @param triggerGroupName 触发器组名
   */
  public static void resumeTrigger(String triggerName, String triggerGroupName) {  
    try {  
      sched.resumeTrigger(new TriggerKey(triggerName, triggerGroupName));// 重启触发器  
    } catch (SchedulerException e) {  
      e.printStackTrace();
      throw new RuntimeException(e);  
    }  
  } 
  
  /**
   * 重启触发器,使用默认的任务名，触发器名，触发器组名
   * @param jobName 任务名称
   */
  public static void resumeTrigger(String jobName) {  
    try {  
      sched.resumeTrigger(new TriggerKey("trigger_"+jobName, TRIGGER_GROUP_NAME));// 重启触发器  
    } catch (SchedulerException e) {  
      e.printStackTrace();
      throw new RuntimeException(e);  
    }  
  } 
  
  /**
   * 重启任务, 默认使用任务组名
   * @param jobName
   */
  public static void resumeJob(String jobName) {
    try {  
      JobKey jobKey = JobKey.jobKey(jobName, TRIGGER_GROUP_NAME);
      sched.resumeJob(jobKey);
    } catch (SchedulerException e) {  
      e.printStackTrace();
      throw new RuntimeException(e);  
    } 
  }
  
  /**
   * 重启任务
   * @param jobName 任务名称
   * @param jobGroupName 任务组
   */
  public static void resumeJob( String jobName, String jobGroupName ) {
    try {  
      JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
      sched.resumeJob(jobKey);
    } catch (SchedulerException e) {  
      e.printStackTrace();
      throw new RuntimeException(e);  
    } 
  }
  
  /**
   * 启动所有的job
   */
  public static void startJobs() {
    try {  
      sched.start();  
    } catch (Exception e) {  
      e.printStackTrace();
      throw new RuntimeException(e);  
    } 
  }
  
  /**
   * 关闭所有的job
   */
  public static void shutDownJobs() {
    try {  
      if ( sched != null && !sched.isShutdown()) {
        sched.standby();
      }
    } catch (Exception e) { 
      e.printStackTrace();
      throw new RuntimeException(e);  
    } 
  }
}
