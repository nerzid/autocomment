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

import org.jbpm.process.core.timer.impl.GlobalTimerService.GlobalJobHandle;
import java.util.concurrent.atomic.AtomicLong;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import javax.naming.InitialContext;
import org.jbpm.process.instance.timer.TimerManager.ProcessJobContext;
import org.jbpm.process.core.timer.SchedulerServiceInterceptor;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.impl.TimerJobInstance;
import org.drools.core.time.Job;
import org.jbpm.process.instance.timer.TimerManager.StartProcessJobContext;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.jbpm.process.core.timer.NamedJobContext;
import org.drools.core.time.Trigger;
import org.drools.core.time.TimerService;
import javax.naming.NamingException;

public class EjbSchedulerService implements GlobalSchedulerService {
    private AtomicLong idCounter = new AtomicLong();

    private TimerService globalTimerService;

    private EJBTimerScheduler scheduler;

    private SchedulerServiceInterceptor interceptor = new DelegateSchedulerServiceInterceptor(this);

    @Override
    public JobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger) {
        Long id = idCounter.getAndIncrement();
        String jobName = getJobName(ctx, id);
        EjbGlobalJobHandle jobHandle = new EjbGlobalJobHandle(id, jobName, ((GlobalTimerService) (globalTimerService)).getTimerServiceId());
        TimerJobInstance jobInstance = scheduler.getTimerByName(jobName);
        if (jobInstance != null) {
            return jobInstance.getJobHandle();
        }
        jobInstance = globalTimerService.getTimerJobFactoryManager().createTimerJobInstance(job, ctx, trigger, jobHandle, ((InternalSchedulerService) (globalTimerService)));
        // set timer TimerJobInstance{((TimerJobInstance) (jobInstance))} to EjbGlobalJobHandle{jobHandle}
        jobHandle.setTimerJobInstance(((TimerJobInstance) (jobInstance)));
        // internal schedule TimerJobInstance{jobInstance} to SchedulerServiceInterceptor{interceptor}
        interceptor.internalSchedule(jobInstance);
        return jobHandle;
    }

    @Override
    public boolean removeJob(JobHandle jobHandle) {
        boolean result = scheduler.removeJob(jobHandle);
        return result;
    }

    @Override
    public void internalSchedule(TimerJobInstance timerJobInstance) {
        // internal schedule TimerJobInstance{timerJobInstance} to EJBTimerScheduler{scheduler}
        scheduler.internalSchedule(timerJobInstance);
    }

    @Override
    public void initScheduler(TimerService timerService) {
        this.globalTimerService = timerService;
        try {
            this.scheduler = InitialContext.doLookup("java:module/EJBTimerScheduler");
        } catch (NamingException e) {
            throw new RuntimeException("Unable to find EJB scheduler for jBPM timer service", e);
        }
    }

    @Override
    public void shutdown() {
        // managed by container - no op
    }

    @Override
    public JobHandle buildJobHandleForContext(NamedJobContext ctx) {
        return new EjbGlobalJobHandle((-1), getJobName(ctx, (-1L)), ((GlobalTimerService) (globalTimerService)).getTimerServiceId());
    }

    @Override
    public boolean isTransactional() {
        return false;
    }

    @Override
    public boolean retryEnabled() {
        return false;
    }

    @Override
    public void setInterceptor(SchedulerServiceInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public boolean isValid(GlobalJobHandle jobHandle) {
        return true;
    }

    private String getJobName(JobContext ctx, Long id) {
        String jobname = null;
        if (ctx instanceof ProcessJobContext) {
            ProcessJobContext processCtx = ((ProcessJobContext) (ctx));
            jobname = ((((processCtx.getSessionId()) + "-") + (processCtx.getProcessInstanceId())) + "-") + (processCtx.getTimer().getId());
            if (processCtx instanceof StartProcessJobContext) {
                jobname = (("StartProcess-" + (((StartProcessJobContext) (processCtx)).getProcessId())) + "-") + (processCtx.getTimer().getId());
            }
        }else
            if (ctx instanceof NamedJobContext) {
                jobname = ((NamedJobContext) (ctx)).getJobName();
            }else {
                jobname = (("Timer-" + (ctx.getClass().getSimpleName())) + "-") + id;
            }
        
        return jobname;
    }
}

