/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.task.impl;

import java.util.concurrent.Callable;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.runtime.CommandExecutor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.DeadlineSummary;
import org.kie.internal.task.api.model.Deadlines;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.kie.internal.task.api.model.InternalTask;
import org.drools.core.common.InternalWorkingMemory;
import java.util.Iterator;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.process.core.timer.NamedJobContext;
import org.jbpm.services.task.deadlines.NotificationListener;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.io.Serializable;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskPersistenceContext;
import java.util.concurrent.TimeUnit;
import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.drools.core.time.Trigger;

public class TaskDeadlinesServiceImpl implements TaskDeadlinesService {
    private static final Logger logger = LoggerFactory.getLogger(TaskDeadlinesServiceImpl.class);

    // static instance so it can be used from background jobs
    protected static volatile CommandExecutor instance;

    protected static NotificationListener notificationListener;

    // use single ThreadPoolExecutor for all instances of task services within same JVM
    private static volatile ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(3);

    private static volatile Map<Long, List<ScheduledFuture<TaskDeadlinesServiceImpl.ScheduledTaskDeadline>>> startScheduledTaskDeadlines = new ConcurrentHashMap<Long, List<ScheduledFuture<TaskDeadlinesServiceImpl.ScheduledTaskDeadline>>>();

    private static volatile Map<Long, List<ScheduledFuture<TaskDeadlinesServiceImpl.ScheduledTaskDeadline>>> endScheduledTaskDeadlines = new ConcurrentHashMap<Long, List<ScheduledFuture<TaskDeadlinesServiceImpl.ScheduledTaskDeadline>>>();

    private static volatile Map<String, JobHandle> jobHandles = new ConcurrentHashMap<String, JobHandle>();

    private TaskPersistenceContext persistenceContext;

    public TaskDeadlinesServiceImpl() {
    }

    public TaskDeadlinesServiceImpl(TaskPersistenceContext persistenceContext) {
        TaskDeadlinesServiceImpl.this.persistenceContext = persistenceContext;
    }

    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
        TaskDeadlinesServiceImpl.this.persistenceContext = persistenceContext;
    }

    public void schedule(long taskId, long deadlineId, long delay, DeadlineType type) {
        Task task = persistenceContext.findTask(taskId);
        String deploymentId = task.getTaskData().getDeploymentId();
        TimerService timerService = TimerServiceRegistry.getInstance().get((deploymentId + (TimerServiceRegistry.TIMER_SERVICE_SUFFIX)));
        if ((timerService != null) && (timerService instanceof GlobalTimerService)) {
            TaskDeadlinesServiceImpl.TaskDeadlineJob deadlineJob = new TaskDeadlinesServiceImpl.TaskDeadlineJob(taskId, deadlineId, type);
            Trigger trigger = new org.drools.core.time.impl.IntervalTrigger(timerService.getCurrentTime(), null, null, (-1), delay, 0, null, null);
            JobHandle handle = timerService.scheduleJob(deadlineJob, new TaskDeadlinesServiceImpl.TaskDeadlineJobContext(deadlineJob.getId(), task.getTaskData().getProcessInstanceId()), trigger);
            TaskDeadlinesServiceImpl.logger.debug("scheduling timer job for deadline {} and task {}  using timer service {}", deadlineJob.getId(), taskId, timerService);
            TaskDeadlinesServiceImpl.jobHandles.put(deadlineJob.getId(), handle);
        } else {
            ScheduledFuture<TaskDeadlinesServiceImpl.ScheduledTaskDeadline> scheduled = TaskDeadlinesServiceImpl.scheduler.schedule(new TaskDeadlinesServiceImpl.ScheduledTaskDeadline(taskId, deadlineId, type), delay, TimeUnit.MILLISECONDS);
            List<ScheduledFuture<TaskDeadlinesServiceImpl.ScheduledTaskDeadline>> knownFutures = null;
            if (type == (DeadlineType.START)) {
                knownFutures = TaskDeadlinesServiceImpl.startScheduledTaskDeadlines.get(taskId);
            } else if (type == (DeadlineType.END)) {
                knownFutures = TaskDeadlinesServiceImpl.endScheduledTaskDeadlines.get(taskId);
            } 
            if (knownFutures == null) {
                knownFutures = new CopyOnWriteArrayList<ScheduledFuture<TaskDeadlinesServiceImpl.ScheduledTaskDeadline>>();
            } 
            knownFutures.add(scheduled);
            if (type == (DeadlineType.START)) {
                TaskDeadlinesServiceImpl.startScheduledTaskDeadlines.put(taskId, knownFutures);
            } else if (type == (DeadlineType.END)) {
                TaskDeadlinesServiceImpl.endScheduledTaskDeadlines.put(taskId, knownFutures);
            } 
        }
    }

    public void unschedule(long taskId, DeadlineType type) {
        Task task = persistenceContext.findTask(taskId);
        String deploymentId = task.getTaskData().getDeploymentId();
        Deadlines deadlines = ((InternalTask) (task)).getDeadlines();
        TimerService timerService = TimerServiceRegistry.getInstance().get((deploymentId + (TimerServiceRegistry.TIMER_SERVICE_SUFFIX)));
        if ((timerService != null) && (timerService instanceof GlobalTimerService)) {
            if (type == (DeadlineType.START)) {
                List<Deadline> startDeadlines = deadlines.getStartDeadlines();
                List<DeadlineSummary> resultList = ((List<DeadlineSummary>) (persistenceContext.queryWithParametersInTransaction("UnescalatedStartDeadlinesByTaskId", persistenceContext.addParametersToMap("taskId", taskId), ClassUtil.<List<DeadlineSummary>>castClass(List.class))));
                for (DeadlineSummary summary : resultList) {
                    TaskDeadlinesServiceImpl.TaskDeadlineJob deadlineJob = new TaskDeadlinesServiceImpl.TaskDeadlineJob(summary.getTaskId(), summary.getDeadlineId(), DeadlineType.START);
                    TaskDeadlinesServiceImpl.logger.debug("unscheduling timer job for deadline {} {} and task {}  using timer service {}", deadlineJob.getId(), summary.getDeadlineId(), taskId, timerService);
                    JobHandle jobHandle = TaskDeadlinesServiceImpl.jobHandles.remove(deadlineJob.getId());
                    if (jobHandle == null) {
                        jobHandle = ((GlobalTimerService) (timerService)).buildJobHandleForContext(new TaskDeadlinesServiceImpl.TaskDeadlineJobContext(deadlineJob.getId(), task.getTaskData().getProcessInstanceId()));
                    } 
                    timerService.removeJob(jobHandle);
                    // mark the deadlines so they won't be rescheduled again
                    for (Deadline deadline : startDeadlines) {
                        if ((deadline.getId()) == (summary.getDeadlineId())) {
                            deadline.setEscalated(true);
                        } 
                    }
                }
            } else if (type == (DeadlineType.END)) {
                List<Deadline> endDeadlines = deadlines.getStartDeadlines();
                List<DeadlineSummary> resultList = ((List<DeadlineSummary>) (persistenceContext.queryWithParametersInTransaction("UnescalatedEndDeadlinesByTaskId", persistenceContext.addParametersToMap("taskId", taskId), ClassUtil.<List<DeadlineSummary>>castClass(List.class))));
                for (DeadlineSummary summary : resultList) {
                    TaskDeadlinesServiceImpl.TaskDeadlineJob deadlineJob = new TaskDeadlinesServiceImpl.TaskDeadlineJob(summary.getTaskId(), summary.getDeadlineId(), DeadlineType.END);
                    TaskDeadlinesServiceImpl.logger.debug("unscheduling timer job for deadline {} and task {}  using timer service {}", deadlineJob.getId(), taskId, timerService);
                    JobHandle jobHandle = TaskDeadlinesServiceImpl.jobHandles.remove(deadlineJob.getId());
                    if (jobHandle == null) {
                        jobHandle = ((GlobalTimerService) (timerService)).buildJobHandleForContext(new TaskDeadlinesServiceImpl.TaskDeadlineJobContext(deadlineJob.getId(), task.getTaskData().getProcessInstanceId()));
                    } 
                    timerService.removeJob(jobHandle);
                    // mark the deadlines so they won't be rescheduled again
                    for (Deadline deadline : endDeadlines) {
                        if ((deadline.getId()) == (summary.getDeadlineId())) {
                            deadline.setEscalated(true);
                        } 
                    }
                }
            } 
        } else {
            List<ScheduledFuture<TaskDeadlinesServiceImpl.ScheduledTaskDeadline>> knownFutures = null;
            if (type == (DeadlineType.START)) {
                knownFutures = TaskDeadlinesServiceImpl.startScheduledTaskDeadlines.get(taskId);
            } else if (type == (DeadlineType.END)) {
                knownFutures = TaskDeadlinesServiceImpl.endScheduledTaskDeadlines.get(taskId);
            } 
            if (knownFutures == null) {
                return ;
            } 
            Iterator<ScheduledFuture<TaskDeadlinesServiceImpl.ScheduledTaskDeadline>> it = knownFutures.iterator();
            while (it.hasNext()) {
                ScheduledFuture<TaskDeadlinesServiceImpl.ScheduledTaskDeadline> scheduled = it.next();
                try {
                    if ((!(scheduled.isDone())) && (!(scheduled.isCancelled()))) {
                        scheduled.cancel(true);
                    } 
                } catch (Exception e) {
                    TaskDeadlinesServiceImpl.logger.error("Error while cancelling scheduled deadline task for Task with id {} -> {}", taskId, e);
                }
            }
        }
    }

    public static class ScheduledTaskDeadline implements Serializable , Callable<TaskDeadlinesServiceImpl.ScheduledTaskDeadline> {
        private static final long serialVersionUID = 1L;

        private long taskId;

        private long deadlineId;

        private DeadlineType type;

        public ScheduledTaskDeadline(long taskId, long deadlineId, DeadlineType type) {
            TaskDeadlinesServiceImpl.ScheduledTaskDeadline.this.taskId = taskId;
            TaskDeadlinesServiceImpl.ScheduledTaskDeadline.this.deadlineId = deadlineId;
            TaskDeadlinesServiceImpl.ScheduledTaskDeadline.this.type = type;
        }

        public long getTaskId() {
            return taskId;
        }

        public long getDeadlineId() {
            return deadlineId;
        }

        public DeadlineType getType() {
            return TaskDeadlinesServiceImpl.ScheduledTaskDeadline.this.type;
        }

        public TaskDeadlinesServiceImpl.ScheduledTaskDeadline call() throws Exception {
            CommandExecutor executor = TaskDeadlinesServiceImpl.getInstance();
            if (executor != null) {
                executor.execute(new org.jbpm.services.task.commands.ExecuteDeadlinesCommand(taskId, deadlineId, type));
            } else {
                TaskDeadlinesServiceImpl.logger.error("TaskDeadlineService instance is not available, most likely was not properly initialized - Job did not run!");
            }
            return null;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((int) (((deadlineId) ^ ((deadlineId) >>> 32))));
            result = (prime * result) + ((int) (((taskId) ^ ((taskId) >>> 32))));
            result = (prime * result) + (type.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((TaskDeadlinesServiceImpl.ScheduledTaskDeadline.this) == obj) {
                return true;
            } 
            if (obj == null) {
                return false;
            } 
            if (!(obj instanceof TaskDeadlinesServiceImpl.ScheduledTaskDeadline)) {
                return false;
            } 
            TaskDeadlinesServiceImpl.ScheduledTaskDeadline other = ((TaskDeadlinesServiceImpl.ScheduledTaskDeadline) (obj));
            if ((deadlineId) != (other.deadlineId)) {
                return false;
            } 
            if ((taskId) != (other.taskId)) {
                return false;
            } 
            if ((type) == null) {
                if ((other.getType()) != null) {
                    return false;
                } 
            } else if (type.equals(other.getType())) {
                return false;
            } 
            return true;
        }
    }

    @SuppressWarnings(value = "unused")
    private static class TaskDeadlineJob implements Serializable , Job {
        private static final long serialVersionUID = -2453658968872574615L;

        private long taskId;

        private long deadlineId;

        private DeadlineType type;

        public TaskDeadlineJob(long taskId, long deadlineId, DeadlineType type) {
            TaskDeadlinesServiceImpl.TaskDeadlineJob.this.taskId = taskId;
            TaskDeadlinesServiceImpl.TaskDeadlineJob.this.deadlineId = deadlineId;
            TaskDeadlinesServiceImpl.TaskDeadlineJob.this.type = type;
        }

        public long getTaskId() {
            return taskId;
        }

        public long getDeadlineId() {
            return deadlineId;
        }

        public DeadlineType getType() {
            return TaskDeadlinesServiceImpl.TaskDeadlineJob.this.type;
        }

        @Override
        public void execute(JobContext ctx) {
            CommandExecutor executor = TaskDeadlinesServiceImpl.getInstance();
            if (executor != null) {
                executor.execute(new org.jbpm.services.task.commands.ExecuteDeadlinesCommand(taskId, deadlineId, type));
            } else {
                TaskDeadlinesServiceImpl.logger.error("TaskDeadlineService instance is not available, most likely was not properly initialized - Job did not run!");
            }
        }

        public String getId() {
            return ((((taskId) + "_") + (deadlineId)) + "_") + (type);
        }
    }

    private static class TaskDeadlineJobContext implements NamedJobContext {
        private static final long serialVersionUID = -6838102884655249845L;

        private JobHandle jobHandle;

        private String jobName;

        private Long processInstanceId;

        public TaskDeadlineJobContext(String jobName, Long processInstanceId) {
            TaskDeadlinesServiceImpl.TaskDeadlineJobContext.this.jobName = jobName;
            TaskDeadlinesServiceImpl.TaskDeadlineJobContext.this.processInstanceId = processInstanceId;
        }

        @Override
        public void setJobHandle(JobHandle jobHandle) {
            TaskDeadlinesServiceImpl.TaskDeadlineJobContext.this.jobHandle = jobHandle;
        }

        @Override
        public JobHandle getJobHandle() {
            return jobHandle;
        }

        @Override
        public String getJobName() {
            return jobName;
        }

        @Override
        public Long getProcessInstanceId() {
            return processInstanceId;
        }

        @Override
        public InternalWorkingMemory getWorkingMemory() {
            return null;
        }
    }

    public static CommandExecutor getInstance() {
        return TaskDeadlinesServiceImpl.instance;
    }

    public static synchronized void initialize(CommandExecutor instance) {
        if (instance != null) {
            TaskDeadlinesServiceImpl.instance = instance;
            TaskDeadlinesServiceImpl.getInstance().execute(new org.jbpm.services.task.commands.InitDeadlinesCommand());
        } 
    }

    public static synchronized void reset() {
        TaskDeadlinesServiceImpl.dispose();
        TaskDeadlinesServiceImpl.scheduler = new ScheduledThreadPoolExecutor(3);
    }

    public static synchronized void dispose() {
        try {
            if ((TaskDeadlinesServiceImpl.scheduler) != null) {
                TaskDeadlinesServiceImpl.scheduler.shutdownNow();
            } 
            TaskDeadlinesServiceImpl.startScheduledTaskDeadlines.clear();
            TaskDeadlinesServiceImpl.endScheduledTaskDeadlines.clear();
            TaskDeadlinesServiceImpl.jobHandles.clear();
            TaskDeadlinesServiceImpl.notificationListener = null;
            TaskDeadlinesServiceImpl.instance = null;
        } catch (Exception e) {
            TaskDeadlinesServiceImpl.logger.error("Error encountered when disposing TaskDeadlineService", e);
        }
    }
}

