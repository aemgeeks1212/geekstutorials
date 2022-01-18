package com.geeks.aem.tutorials.core.schedulers;

import com.geeks.aem.tutorials.core.config.JSONCreatorConfig;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = JSONCreatorConfig.class)
public class JSONCreator implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(JSONCreator.class);

    private int schedulerId;
    private String[] countries;

    @Reference
    private Scheduler scheduler;

    @Activate
    protected void activate(JSONCreatorConfig config) {
        schedulerId = config.schedulerName().hashCode();
        addScheduler(config);
        countries=config.setCountries();
    }

    @Deactivate
    protected void deactivate(JSONCreatorConfig config) {
        removeScheduler();
    }

    protected void removeScheduler() {
        scheduler.unschedule(String.valueOf(schedulerId));
    }

    protected void addScheduler(JSONCreatorConfig config) {
        ScheduleOptions scheduleOptions = scheduler.EXPR(config.cronExpression());
        scheduleOptions.name(String.valueOf(schedulerId));
        scheduler.schedule(this, scheduleOptions);

        //LOG.info("\n ---------Scheduler added----------");
        /*ScheduleOptions scheduleOptionsNow = scheduler.NOW(3,5);
        scheduler.schedule(this, scheduleOptionsNow);*/
    }
   @Override
    public void run() {
       for(String country:countries){
           LOG.info("\n ====> COUNTRY - {} ",country);
       }

    }
}
