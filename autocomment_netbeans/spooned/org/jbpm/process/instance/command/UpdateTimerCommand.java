/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.process.instance.command;

import org.kie.internal.command.ProcessInstanceIdCommand;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.kie.internal.command.Context;
import org.drools.core.command.impl.GenericCommand;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.kie.api.runtime.KieSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.api.runtime.process.NodeInstance;
import javax.xml.bind.annotation.XmlAccessType;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import org.jbpm.process.instance.timer.TimerInstance;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlRootElement;
import org.jbpm.process.instance.timer.TimerManager;

@XmlRootElement(name = "update-timer-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class UpdateTimerCommand implements GenericCommand<Void> , ProcessInstanceIdCommand {
    private static final long serialVersionUID = -8252686458877022330L;

    @XmlElement
    @XmlSchemaType(name = "long")
    private long processInstanceId;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String timerName;

    @XmlElement
    @XmlSchemaType(name = "long")
    private long delay;

    @XmlElement
    @XmlSchemaType(name = "long")
    private long period;

    @XmlElement
    @XmlSchemaType(name = "int")
    private int repeatLimit;

    public UpdateTimerCommand(long processInstanceId, String timerName, long delay) {
        this(processInstanceId, timerName, delay, 0, 0);
    }

    public UpdateTimerCommand(long processInstanceId, String timerName, long period, int repeatLimit) {
        this(processInstanceId, timerName, 0, period, repeatLimit);
    }

    public UpdateTimerCommand(long processInstanceId, String timerName, long delay, long period, int repeatLimit) {
        this.processInstanceId = processInstanceId;
        this.timerName = timerName;
        this.delay = delay;
        this.period = period;
        this.repeatLimit = repeatLimit;
    }

    @Override
    public Void execute(Context context) {
        KieSession kieSession = ((KnowledgeCommandContext) (context)).getKieSession();
        TimerManager tm = getTimerManager(kieSession);
        RuleFlowProcessInstance wfp = ((RuleFlowProcessInstance) (kieSession.getProcessInstance(processInstanceId)));
        for (NodeInstance nodeInstance : wfp.getNodeInstances()) {
            if (nodeInstance instanceof TimerNodeInstance) {
                TimerNodeInstance tni = ((TimerNodeInstance) (nodeInstance));
                if (tni.getNodeName().equals(timerName)) {
                    TimerInstance timer = tm.getTimerMap().get(tni.getTimerId());
                    tm.cancelTimer(timer.getTimerId());
                    TimerInstance newTimer = new TimerInstance();
                    if ((delay) != 0) {
                        long diff = (System.currentTimeMillis()) - (timer.getActivated().getTime());
                        newTimer.setDelay((((delay) * 1000) - diff));
                    }
                    newTimer.setPeriod(period);
                    newTimer.setRepeatLimit(repeatLimit);
                    newTimer.setTimerId(timer.getTimerId());
                    tm.registerTimer(newTimer, wfp);
                    tni.internalSetTimerId(newTimer.getId());
                    break;
                }
            }else
                if (nodeInstance instanceof org.jbpm.workflow.instance.node.StateBasedNodeInstance) {
                    org.jbpm.workflow.instance.node.StateBasedNodeInstance sbni = ((org.jbpm.workflow.instance.node.StateBasedNodeInstance) (nodeInstance));
                    if (sbni.getNodeName().equals(timerName)) {
                        java.util.List<Long> timerList = sbni.getTimerInstances();
                        if ((timerList != null) && ((timerList.size()) == 1)) {
                            TimerInstance timer = tm.getTimerMap().get(timerList.get(0));
                            tm.cancelTimer(timer.getTimerId());
                            TimerInstance newTimer = new TimerInstance();
                            if ((delay) != 0) {
                                long diff = (System.currentTimeMillis()) - (timer.getActivated().getTime());
                                newTimer.setDelay((((delay) * 1000) - diff));
                            }
                            newTimer.setPeriod(period);
                            newTimer.setRepeatLimit(repeatLimit);
                            newTimer.setTimerId(timer.getTimerId());
                            tm.registerTimer(newTimer, wfp);
                            timerList.clear();
                            timerList.add(newTimer.getId());
                            sbni.internalSetTimerInstances(timerList);
                        }
                        break;
                    }
                }
            
        }
        return null;
    }

    @Override
    public void setProcessInstanceId(Long procInstId) {
        this.processInstanceId = procInstId;
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    private TimerManager getTimerManager(KieSession ksession) {
        KieSession internal = ksession;
        if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            internal = ((KnowledgeCommandContext) (((CommandBasedStatefulKnowledgeSession) (ksession)).getCommandService().getContext())).getKieSession();
        }
        return ((InternalProcessRuntime) (((StatefulKnowledgeSessionImpl) (internal)).getProcessRuntime())).getTimerManager();
    }

    public String toString() {
        return ((((((("processInstance.updateTimer(" + (timerName)) + ", ") + (delay)) + ", ") + (period)) + ", ") + (repeatLimit)) + ");";
    }
}

