

package org.jbpm.test.listener;

import java.util.Iterator;
import org.kie.internal.runtime.KnowledgeRuntime;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;

/**
 * Simple listener that saves all the events into list and then allows to
 * iterate over them
 */
public class IterableProcessEventListener implements Iterator<IterableProcessEventListener.TrackedEvent> , ProcessEventListener {
    public static final String BEFORE_STARTED = "beforeProcessStarted";

    public static final String AFTER_STARTED = "afterProcessStarted";

    public static final String BEFORE_COMPLETED = "beforeProcessCompleted";

    public static final String AFTER_COMPLETED = "afterProcessCompleted";

    public static final String BEFORE_TRIGGERED = "beforeNodeTriggered";

    public static final String AFTER_TRIGGERED = "afterNodeTriggered";

    public static final String BEFORE_LEFT = "beforeNodeLeft";

    public static final String AFTER_LEFT = "afterNodeLeft";

    public static final String BEFORE_VARIABLE = "beforeVariableChanged";

    public static final String AFTER_VARIABLE = "afterVariableChanged";

    private final List<IterableProcessEventListener.TrackedEvent> events = new LinkedList<IterableProcessEventListener.TrackedEvent>();

    private int position = 0;

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        events.add(new IterableProcessEventListener.TrackedEvent(new IterableProcessEventListener.CachedProcessStartedEvent(event), IterableProcessEventListener.BEFORE_STARTED));
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        // events.add(new TrackedEvent(new CachedProcessStartedEvent(event),
        // AFTER_STARTED));
    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        events.add(new IterableProcessEventListener.TrackedEvent(new IterableProcessEventListener.CachedProcessCompletedEvent(event), IterableProcessEventListener.BEFORE_COMPLETED));
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        // events.add(new TrackedEvent(new CachedProcessCompletedEvent(event),
        // AFTER_COMPLETED));
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        events.add(new IterableProcessEventListener.TrackedEvent(new IterableProcessEventListener.CachedProcessNodeTriggeredEvent(event), IterableProcessEventListener.BEFORE_TRIGGERED));
    }

    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        // events.add(new TrackedEvent(new
        // CachedProcessNodeTriggeredEvent(event), AFTER_TRIGGERED));
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        events.add(new IterableProcessEventListener.TrackedEvent(new IterableProcessEventListener.CachedProcessNodeLeftEvent(event), IterableProcessEventListener.BEFORE_LEFT));
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        // events.add(new TrackedEvent(new CachedProcessNodeLeftEvent(event),
        // AFTER_LEFT));
    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        events.add(new IterableProcessEventListener.TrackedEvent(new IterableProcessEventListener.CachedProcessVariableChangedEvent(event), IterableProcessEventListener.BEFORE_VARIABLE));
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        events.add(new IterableProcessEventListener.TrackedEvent(new IterableProcessEventListener.CachedProcessVariableChangedEvent(event), IterableProcessEventListener.AFTER_VARIABLE));
    }

    @Override
    public boolean hasNext() {
        return (position) < (events.size());
    }

    @Override
    public IterableProcessEventListener.TrackedEvent next() {
        return events.get(((position)++));
    }

    public IterableProcessEventListener.TrackedEvent current() {
        return events.get(((position) == 0 ? 0 : (position) - 1));
    }

    @Override
    public void remove() {
    }

    public void reset() {
        position = 0;
    }

    public void clear() {
        reset();
        events.clear();
    }

    public void printCurrentState() {
        printState(null, true);
    }

    public void printCurrentState(Logger logger) {
        printState(logger, true);
    }

    public void printRemainingEvents() {
        printState(null, false);
    }

    public void printRemainingEvents(Logger logger) {
        printState(logger, false);
    }

    private void printState(Logger logger, boolean reset) {
        int mark = position;
        if (reset) {
            reset();
        } 
        if (logger == null) {
            printToStdOut();
        } else {
            printToLogger(logger);
        }
        position = mark;
    }

    private void printToStdOut() {
        while (hasNext()) {
            System.out.println(next().toString());
        }
    }

    private void printToLogger(Logger logger) {
        while (hasNext()) {
            logger.debug(next().toString());
        }
    }

    public static class TrackedEvent {
        private final IterableProcessEventListener.NoopProcessEvent event;

        private final String method;

        public TrackedEvent(IterableProcessEventListener.NoopProcessEvent event, String method) {
            this.event = event;
            this.method = method;
        }

        @SuppressWarnings(value = "unchecked")
        public <T extends ProcessEvent> T getEvent() {
            return ((T) (event));
        }

        public String getMethod() {
            return method;
        }

        public String getProcessId() {
            return event.getProcessId();
        }

        public long getProcessInstanceId() {
            return event.getProcessInstanceId();
        }

        @Override
        public String toString() {
            return (((method) + "(") + (event.toString())) + ")";
        }
    }

    private static class NoopProcessEvent implements ProcessEvent {
        protected final String processId;

        protected final long processInstanceId;

        public NoopProcessEvent(ProcessEvent event) {
            processId = event.getProcessInstance().getProcessId();
            processInstanceId = event.getProcessInstance().getId();
        }

        @Override
        public KnowledgeRuntime getKieRuntime() {
            return null;
        }

        @Override
        public ProcessInstance getProcessInstance() {
            return null;
        }

        public String getProcessId() {
            return processId;
        }

        public long getProcessInstanceId() {
            return processInstanceId;
        }
    }

    public static class CachedProcessStartedEvent extends IterableProcessEventListener.NoopProcessEvent implements ProcessStartedEvent {
        public CachedProcessStartedEvent(ProcessStartedEvent event) {
            super(event);
        }

        @Override
        public String getProcessId() {
            return processId;
        }

        @Override
        public String toString() {
            return (processId) + " started";
        }
    }

    public static class CachedProcessNodeTriggeredEvent extends IterableProcessEventListener.NoopProcessEvent implements ProcessNodeTriggeredEvent {
        private final String nodeName;

        public CachedProcessNodeTriggeredEvent(ProcessNodeTriggeredEvent event) {
            super(event);
            nodeName = event.getNodeInstance().getNodeName();
        }

        @Override
        public NodeInstance getNodeInstance() {
            return null;
        }

        public String getNodeName() {
            return nodeName;
        }

        @Override
        public String toString() {
            return (nodeName) + " triggered";
        }
    }

    public static class CachedProcessNodeLeftEvent extends IterableProcessEventListener.NoopProcessEvent implements ProcessNodeLeftEvent {
        private final String nodeName;

        public CachedProcessNodeLeftEvent(ProcessNodeLeftEvent event) {
            super(event);
            nodeName = event.getNodeInstance().getNodeName();
        }

        @Override
        public NodeInstance getNodeInstance() {
            return null;
        }

        public String getNodeName() {
            return nodeName;
        }

        @Override
        public String toString() {
            return (nodeName) + " left";
        }
    }

    public static class CachedProcessCompletedEvent extends IterableProcessEventListener.NoopProcessEvent implements ProcessCompletedEvent {
        public CachedProcessCompletedEvent(ProcessCompletedEvent event) {
            super(event);
        }

        @Override
        public String getProcessId() {
            return processId;
        }

        @Override
        public String toString() {
            return (processId) + " completed";
        }
    }

    public static class CachedProcessVariableChangedEvent extends IterableProcessEventListener.NoopProcessEvent implements ProcessVariableChangedEvent {
        private final String variableId;

        private final String variableInstanceId;

        private final Object oldValue;

        private final Object newValue;

        public CachedProcessVariableChangedEvent(ProcessVariableChangedEvent event) {
            super(event);
            variableId = event.getVariableId();
            variableInstanceId = event.getVariableInstanceId();
            oldValue = event.getOldValue();
            newValue = event.getNewValue();
        }

        @Override
        public String getVariableId() {
            return variableId;
        }

        @Override
        public String getVariableInstanceId() {
            return variableInstanceId;
        }

        @Override
        public Object getOldValue() {
            return oldValue;
        }

        @Override
        public Object getNewValue() {
            return newValue;
        }

        @Override
        public String toString() {
            return ((((variableId) + ":") + ((oldValue) == null ? "null" : oldValue.toString())) + " -> ") + ((newValue) == null ? "null" : newValue.toString());
        }
    }
}

