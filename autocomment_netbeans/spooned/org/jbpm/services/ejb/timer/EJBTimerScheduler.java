/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.services.ejb.timer;

import javax.ejb.LockType;
import java.util.concurrent.Callable;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Timer;
import javax.ejb.ConcurrencyManagementType;
import java.util.Date;
import org.drools.core.time.JobHandle;
import javax.ejb.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.TimerConfig;
import javax.ejb.Timeout;
import javax.annotation.Resource;
import javax.ejb.Startup;
import org.drools.core.time.impl.TimerJobInstance;
import javax.ejb.TimerService;
import java.io.Serializable;
import javax.ejb.Singleton;
import org.jbpm.process.core.timer.TimerServiceRegistry;

@Singleton
@Startup
@ConcurrencyManagement(value = ConcurrencyManagementType.CONTAINER)
@Lock(value = LockType.READ)
public class EJBTimerScheduler {
    private static final Logger logger = LoggerFactory.getLogger(EJBTimerScheduler.class);

    private static final Integer OVERDUE_WAIT_TIME = Integer.parseInt(System.getProperty("org.jbpm.overdue.timer.wait", "10000"));

    @Resource
    private TimerService timerService;

    @SuppressWarnings(value = "unchecked")
    @Timeout
    public void executeTimerJob(Timer timer) {
        EjbTimerJob timerJob = ((EjbTimerJob) (timer.getInfo()));
        // debug String{"About to execute timer for job {}"} to Logger{EJBTimerScheduler.logger}
        EJBTimerScheduler.logger.debug("About to execute timer for job {}", timerJob);
        TimerJobInstance timerJobInstance = timerJob.getTimerJobInstance();
        String timerServiceId = ((EjbGlobalJobHandle) (timerJobInstance.getJobHandle())).getDeploymentId();
        // handle overdue timers as ejb timer service might start before all deployments are ready
        long time = 0;
        while ((TimerServiceRegistry.getInstance().get(timerServiceId)) == null) {
            EJBTimerScheduler.logger.debug("waiting for timer service to be available, elapsed time {} ms", time);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time += 500;
            if (time > (EJBTimerScheduler.OVERDUE_WAIT_TIME)) {
                EJBTimerScheduler.logger.debug("No timer service found after waiting {} ms", time);
                break;
            }
        } 
        try {
            ((Callable<Void>) (timerJobInstance)).call();
        } catch (Exception e) {
            EJBTimerScheduler.logger.warn("Execution of time failed due to {}", e.getMessage(), e);
        }
    }

    public void internalSchedule(TimerJobInstance timerJobInstance) {
        TimerConfig config = new TimerConfig(new EjbTimerJob(timerJobInstance), true);
        Date expirationTime = timerJobInstance.getTrigger().nextFireTime();
        if (expirationTime != null) {
            timerService.createSingleActionTimer(expirationTime, config);
            EJBTimerScheduler.logger.debug("Timer scheduled {} on {} scheduler service", timerJobInstance);
        }else {
            EJBTimerScheduler.logger.info("Timer that was to be scheduled has already expired");
        }
    }

    public boolean removeJob(JobHandle jobHandle) {
        EjbGlobalJobHandle ejbHandle = ((EjbGlobalJobHandle) (jobHandle));
        for (Timer timer : timerService.getTimers()) {
            Serializable info = timer.getInfo();
            if (info instanceof EjbTimerJob) {
                EjbTimerJob job = ((EjbTimerJob) (info));
                EjbGlobalJobHandle handle = ((EjbGlobalJobHandle) (job.getTimerJobInstance().getJobHandle()));
                if (handle.getUuid().equals(ejbHandle.getUuid())) {
                    EJBTimerScheduler.logger.debug("Job handle {} does match timer and is going to be canceled", jobHandle);
                    try {
                        timer.cancel();
                    } catch (Throwable e) {
                        EJBTimerScheduler.logger.debug("Timer cancel error due to {}", e.getMessage());
                        return false;
                    }
                    return true;
                }
            }
        }
        // debug String{"Job handle {} does not match any timer on {} scheduler service"} to Logger{EJBTimerScheduler.logger}
        EJBTimerScheduler.logger.debug("Job handle {} does not match any timer on {} scheduler service", jobHandle, this);
        return false;
    }

    public TimerJobInstance getTimerByName(String jobName) {
        for (Timer timer : timerService.getTimers()) {
            Serializable info = timer.getInfo();
            if (info instanceof EjbTimerJob) {
                EjbTimerJob job = ((EjbTimerJob) (info));
                EjbGlobalJobHandle handle = ((EjbGlobalJobHandle) (job.getTimerJobInstance().getJobHandle()));
                if (handle.getUuid().equals(jobName)) {
                    EJBTimerScheduler.logger.debug("Job  {} does match timer and is going to be returned", jobName);
                    return handle.getTimerJobInstance();
                }
            }
        }
        return null;
    }
}

