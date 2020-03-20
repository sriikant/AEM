package idea.core.schedulers;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import idea.core.schedulers.SampleSchedularConfiguration;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
@Component(immediate = true, service = Runnable.class)
@Designate(ocd = SampleSchedularConfiguration.class)
public class SampleSchedular implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleSchedular.class);
 
    private String customPathParameter;
    private int schedulerId;
 
    @Reference
    private Scheduler scheduler;
 
    @Activate
    protected void activate(SampleSchedularConfiguration config) {
        schedulerId = config.schedulerName().hashCode();
        customPathParameter = config.customPathParameter();
 
        addSchedular(config);
    }
 
    @Deactivate
    protected void deactivate(SampleSchedularConfiguration config) {
        removeScheduler();
    }
 
    @Modified
    protected void modified(SampleSchedularConfiguration config) {
        removeScheduler();
 
        schedulerId = config.schedulerName().hashCode();
 
        addSchedular(config);
    }
 
 
    private void removeScheduler() {
        scheduler.unschedule(String.valueOf(schedulerId));
    }
 
    /**
     * This method adds the scheduler
     *
     * @param config
     */
    private void addSchedular(SampleSchedularConfiguration config) {
        if(config.enabled()) {
            ScheduleOptions scheduleOptions = scheduler.EXPR(config.cronExpression());
            scheduleOptions.name(config.schedulerName());
            scheduleOptions.canRunConcurrently(false);
 
            scheduler.schedule(this, scheduleOptions);
            LOGGER.info("Experience AEM Scheduler added");
        } else {
            LOGGER.info("Experience AEM Scheduler disabled");
        }
    }
 
    public void run() {
        LOGGER.info("Experience AEM, customPathParameter {}", customPathParameter);
    }
}
