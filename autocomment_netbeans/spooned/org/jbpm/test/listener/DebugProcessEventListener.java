

package org.jbpm.test.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.event.process.ProcessNodeEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;

/**
 * Simple listener for watching process flow
 */
public class DebugProcessEventListener implements ProcessEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugProcessEventListener.class);

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        DebugProcessEventListener.LOGGER.debug(formatNodeMessage("afterNodeLeft", event));
    }

    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        DebugProcessEventListener.LOGGER.debug(formatNodeMessage("afterNodeTriggered", event));
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        DebugProcessEventListener.LOGGER.debug(formatProcessMessage("afterProcessCompleted", event));
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        DebugProcessEventListener.LOGGER.debug(formatProcessMessage("afterProcessStarted", event));
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        DebugProcessEventListener.LOGGER.debug(formatVariableChangedMessage("afterVariableChanged", event));
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        DebugProcessEventListener.LOGGER.debug(formatNodeMessage("beforeNodeLeft", event));
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        DebugProcessEventListener.LOGGER.debug(formatNodeMessage("beforeNodeTriggered", event));
    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        DebugProcessEventListener.LOGGER.debug(formatProcessMessage("beforeProcessCompleted", event));
    }

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        DebugProcessEventListener.LOGGER.debug(formatProcessMessage("beforeProcessStarted", event));
    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        DebugProcessEventListener.LOGGER.debug(formatVariableChangedMessage("beforeVariableChanged", event));
    }

    private String formatNodeMessage(String when, ProcessNodeEvent event) {
        NodeInstance ni = event.getNodeInstance();
        return String.format("<%s> name:%s, id:%s", when, ni.getNodeName(), ni.getNodeId());
    }

    private String formatProcessMessage(String when, ProcessEvent event) {
        ProcessInstance pi = event.getProcessInstance();
        return String.format("<%s> name:%s, id:%s, state:%s", when, pi.getProcessName(), pi.getProcessId(), pi.getState());
    }

    private String formatVariableChangedMessage(String when, ProcessVariableChangedEvent event) {
        return String.format("<%s> id:%s, old:%s, new:%s", when, event.getVariableId(), event.getOldValue(), event.getNewValue());
    }
}

