/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.process.instance.timer;

import java.util.Date;
import org.drools.core.time.JobHandle;
import java.io.Serializable;

/**
 */
public class TimerInstance implements Serializable {
    /**
     * * Generated serial version UID
     */
    private static final long serialVersionUID = 9161292833931227195L;

    private long id;

    private long timerId;

    private long delay;

    private long period;

    private JobHandle jobHandle;

    private Date activated;

    private Date lastTriggered;

    private long processInstanceId;

    private int repeatLimit = -1;

    private long sessionId;

    private String cronExpression;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        TimerInstance.this.id = id;
    }

    public long getTimerId() {
        return timerId;
    }

    public void setTimerId(long timerId) {
        TimerInstance.this.timerId = timerId;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        TimerInstance.this.delay = delay;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        TimerInstance.this.period = period;
    }

    public JobHandle getJobHandle() {
        return jobHandle;
    }

    public void setJobHandle(JobHandle jobHandle) {
        TimerInstance.this.jobHandle = jobHandle;
    }

    public Date getActivated() {
        return activated;
    }

    public void setActivated(Date activated) {
        TimerInstance.this.activated = activated;
    }

    public void setLastTriggered(Date lastTriggered) {
        TimerInstance.this.lastTriggered = lastTriggered;
    }

    public Date getLastTriggered() {
        return lastTriggered;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        TimerInstance.this.processInstanceId = processInstanceId;
    }

    public int getRepeatLimit() {
        return repeatLimit;
    }

    public void setRepeatLimit(int stopAfter) {
        TimerInstance.this.repeatLimit = stopAfter;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        TimerInstance.this.sessionId = sessionId;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        TimerInstance.this.cronExpression = cronExpression;
    }

    @Override
    public String toString() {
        return ((((((((((((((("TimerInstance [id=" + (id)) + ", timerId=") + (timerId)) + ", delay=") + (delay)) + ", period=") + (period)) + ", jobHandle=") + (jobHandle)) + ", activated=") + (activated)) + ", lastTriggered=") + (lastTriggered)) + ", processInstanceId=") + (processInstanceId)) + "]";
    }
}

