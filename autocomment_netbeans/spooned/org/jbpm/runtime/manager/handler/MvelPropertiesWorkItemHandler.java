

package org.jbpm.runtime.manager.handler;

import org.junit.Assert;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskService;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class MvelPropertiesWorkItemHandler implements WorkItemHandler {
    private KieSession ksession;

    private TaskService taskService;

    private RuntimeManager runtimeManager;

    private ClassLoader classLoader;

    public MvelPropertiesWorkItemHandler(KieSession ksession, TaskService taskService, RuntimeManager runtimeManager, ClassLoader classloader) {
        MvelPropertiesWorkItemHandler.this.ksession = ksession;
        MvelPropertiesWorkItemHandler.this.taskService = taskService;
        MvelPropertiesWorkItemHandler.this.runtimeManager = runtimeManager;
        MvelPropertiesWorkItemHandler.this.classLoader = classloader;
    }

    @Override
    public void executeWorkItem(WorkItem arg0, WorkItemManager arg1) {
        Assert.assertNotNull(MvelPropertiesWorkItemHandler.this.ksession);
        Assert.assertNotNull(MvelPropertiesWorkItemHandler.this.taskService);
        Assert.assertNotNull(MvelPropertiesWorkItemHandler.this.runtimeManager);
        Assert.assertNotNull(MvelPropertiesWorkItemHandler.this.classLoader);
        arg1.completeWorkItem(arg0.getId(), null);
    }

    @Override
    public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
    }
}

