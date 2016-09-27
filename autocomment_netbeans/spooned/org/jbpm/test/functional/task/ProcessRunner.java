

package org.jbpm.test.functional.task;

import java.util.concurrent.CountDownLatch;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.kie.internal.task.api.UserGroupCallback;

class ProcessRunner implements Runnable {
    private int i;

    private EntityManagerFactory emf;

    private CountDownLatch latch;

    public ProcessRunner(int i, EntityManagerFactory emf, CountDownLatch latch) {
        ProcessRunner.this.i = i;
        ProcessRunner.this.emf = emf;
        ProcessRunner.this.latch = latch;
    }

    private RuntimeManager getRuntimeManager(String process, int i) {
        Properties properties = new Properties();
        properties.setProperty("krisv", "");
        properties.setProperty("sales-rep", "sales");
        properties.setProperty("john", "PM");
        KnowledgeBuilder knowledgeBuilder = createKBuilder(process, ResourceType.BPMN2);
        KieBase kieBase = knowledgeBuilder.newKnowledgeBase();
        UserGroupCallback userGroupCallback = new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(properties);
        // load up the knowledge base
        TimerServiceRegistry.getInstance();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).persistence(true).entityManagerFactory(emf).knowledgeBase(kieBase).get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, ("id-" + i));
    }

    private KnowledgeBuilder createKBuilder(String resource, ResourceType resourceType) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(resource), resourceType);
        if (kbuilder.hasErrors()) {
            int errors = kbuilder.getErrors().size();
            if (errors > 0) {
                System.out.println((("Found " + errors) + " errors"));
                for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                    System.out.println(error.getMessage());
                }
            } 
            throw new IllegalArgumentException("Application process definition has errors, see log for more details");
        } 
        return kbuilder;
    }

    @Override
    public void run() {
        System.out.println((" building runtime: " + (i)));
        RuntimeManager manager = getRuntimeManager("org/jbpm/test/functional/task/ConcurrentHumanTask.bpmn", i);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        // start a new process instance
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", "krisv");
        params.put("description", "Need a new laptop computer");
        ProcessInstance pi = ksession.startProcess("com.sample.humantask.concurrent", params);
        System.out.println((" starting runtime: " + (i)));
        HumanTaskResolver htr = new HumanTaskResolver(pi.getId(), manager, ProcessRunner.this.latch);
        Thread t = new Thread(htr, ((i) + "-ht-resolver"));
        t.start();
    }
}

