

package org.jbpm.test.functional.task;

import org.junit.Assert;
import org.kie.api.task.model.Content;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import java.util.concurrent.CountDownLatch;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.model.Task;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

class HumanTaskResolver implements Runnable {
    private final long pid;

    private final RuntimeManager runtime;

    private CountDownLatch latch;

    public HumanTaskResolver(long pid, RuntimeManager runtime, CountDownLatch latch) {
        this.pid = pid;
        this.runtime = runtime;
        HumanTaskResolver.this.latch = latch;
        System.out.println((">>>>>>>>>>>>>>>>>>>>>>>>>>" + pid));
    }

    @Override
    public void run() {
        System.out.println(((pid) + " running tasks"));
        // "sales-rep" reviews request
        TaskService taskService1 = getTaskService();
        List<TaskSummary> tasks1 = taskService1.getTasksAssignedAsPotentialOwner("sales", "en-UK");
        TaskSummary task1 = selectTaskForProcessInstance(tasks1);
        System.out.println((((((("Sales-rep executing task " + (task1.getName())) + "(") + (task1.getId())) + ": ") + (task1.getDescription())) + ")"));
        taskService1.claim(task1.getId(), "sales-rep");
        taskService1.start(task1.getId(), "sales-rep");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("comment", "Agreed, existing laptop needs replacing");
        results.put("outcome", "Accept");
        taskService1.complete(task1.getId(), "sales-rep", results);
        TaskService taskService2 = getTaskService();
        // "krisv" approves result
        List<TaskSummary> tasks2 = taskService2.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
        TaskSummary task2 = selectTaskForProcessInstance(tasks2);
        System.out.println((((((("krisv executing task " + (task2.getName())) + "(") + (task2.getId())) + ": ") + (task2.getDescription())) + ")"));
        taskService2.start(task2.getId(), "krisv");
        results = new HashMap<String, Object>();
        results.put("outcome", "Agree");
        taskService2.complete(task2.getId(), "krisv", results);
        TaskService taskService3 = getTaskService();
        // "john" as manager reviews request
        List<TaskSummary> tasks3 = taskService3.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task3 = selectTaskForProcessInstance(tasks3);
        System.out.println((((((("john executing task " + (task3.getName())) + "(") + (task3.getId())) + ": ") + (task3.getDescription())) + ")"));
        taskService3.claim(task3.getId(), "john");
        taskService3.start(task3.getId(), "john");
        results = new HashMap<String, Object>();
        results.put("outcome", "Agree");
        taskService3.complete(task3.getId(), "john", results);
        TaskService taskService4 = getTaskService();
        // "sales-rep" gets notification
        List<TaskSummary> tasks4 = taskService4.getTasksAssignedAsPotentialOwner("sales-rep", "en-UK");
        TaskSummary task4 = selectTaskForProcessInstance(tasks4);
        System.out.println((((((("sales-rep executing task " + (task4.getName())) + "(") + (task4.getId())) + ": ") + (task4.getDescription())) + ")"));
        taskService4.start(task4.getId(), "sales-rep");
        Task task = taskService4.getTaskById(task4.getId());
        Content content = taskService4.getContentById(task.getTaskData().getDocumentContentId());
        Object result = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        Assert.assertNotNull(result);
        taskService4.complete(task4.getId(), "sales-rep", null);
        System.out.println("Process instance completed");
        runtime.close();
        latch.countDown();
    }

    public TaskService getTaskService() {
        return runtime.getRuntimeEngine(ProcessInstanceIdContext.get(pid)).getTaskService();
    }

    protected TaskSummary selectTaskForProcessInstance(List<TaskSummary> tasks) {
        for (TaskSummary ts : tasks) {
            if ((ts.getProcessInstanceId().longValue()) == (pid)) {
                return ts;
            } 
        }
        return null;
    }
}

