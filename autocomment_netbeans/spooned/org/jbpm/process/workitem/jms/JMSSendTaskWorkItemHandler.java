/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process.workitem.jms;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import java.io.ByteArrayOutputStream;
import javax.jms.BytesMessage;
import org.kie.internal.runtime.Cacheable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import java.io.ObjectOutputStream;
import javax.jms.Session;
import org.kie.api.runtime.process.WorkItem;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.kie.api.runtime.process.WorkItemManager;

public class JMSSendTaskWorkItemHandler extends AbstractLogOrThrowWorkItemHandler implements Cacheable {
    private static final Logger logger = LoggerFactory.getLogger(JMSSendTaskWorkItemHandler.class);

    private String connectionFactoryName;

    private String destinationName;

    private ConnectionFactory connectionFactory;

    private Destination destination;

    private boolean transacted = false;

    public JMSSendTaskWorkItemHandler() {
        JMSSendTaskWorkItemHandler.this.connectionFactoryName = "java:/JmsXA";
        JMSSendTaskWorkItemHandler.this.destinationName = "queue/KIE.SIGNAL";
        init();
    }

    public JMSSendTaskWorkItemHandler(String connectionFactoryName, String destinationName) {
        JMSSendTaskWorkItemHandler.this.connectionFactoryName = connectionFactoryName;
        JMSSendTaskWorkItemHandler.this.destinationName = destinationName;
        init();
    }

    public JMSSendTaskWorkItemHandler(ConnectionFactory connectionFactory, Destination destination) {
        JMSSendTaskWorkItemHandler.this.connectionFactory = connectionFactory;
        JMSSendTaskWorkItemHandler.this.destination = destination;
        init();
    }

    public JMSSendTaskWorkItemHandler(String connectionFactoryName, String destinationName, boolean transacted) {
        JMSSendTaskWorkItemHandler.this.connectionFactoryName = connectionFactoryName;
        JMSSendTaskWorkItemHandler.this.destinationName = destinationName;
        JMSSendTaskWorkItemHandler.this.transacted = transacted;
        init();
    }

    public JMSSendTaskWorkItemHandler(ConnectionFactory connectionFactory, Destination destination, boolean transacted) {
        JMSSendTaskWorkItemHandler.this.connectionFactory = connectionFactory;
        JMSSendTaskWorkItemHandler.this.destination = destination;
        JMSSendTaskWorkItemHandler.this.transacted = transacted;
        init();
    }

    protected void init() {
        try {
            InitialContext ctx = new InitialContext();
            if ((JMSSendTaskWorkItemHandler.this.connectionFactory) == null) {
                JMSSendTaskWorkItemHandler.this.connectionFactory = ((ConnectionFactory) (ctx.lookup(connectionFactoryName)));
            } 
            if ((JMSSendTaskWorkItemHandler.this.destination) == null) {
                JMSSendTaskWorkItemHandler.this.destination = ((Destination) (ctx.lookup(destinationName)));
            } 
            JMSSendTaskWorkItemHandler.logger.info("JMS based work item handler successfully activated on destination {}", destination);
        } catch (Exception e) {
            JMSSendTaskWorkItemHandler.logger.error("Unable to initialize JMS send work item handler due to {}", e.getMessage(), e);
        }
    }

    protected Message createMessage(WorkItem workItem, Session session) throws JMSException {
        BytesMessage message = session.createBytesMessage();
        // set properties
        addPropertyIfExists("KIE_Signal", workItem.getParameter("Signal"), message);
        addPropertyIfExists("KIE_SignalProcessInstanceId", workItem.getParameter("SignalProcessInstanceId"), message);
        addPropertyIfExists("KIE_SignalWorkItemId", workItem.getParameter("SignalWorkItemId"), message);
        addPropertyIfExists("KIE_SignalDeploymentId", workItem.getParameter("SignalDeploymentId"), message);
        addPropertyIfExists("KIE_ProcessInstanceId", workItem.getProcessInstanceId(), message);
        addPropertyIfExists("KIE_DeploymentId", ((WorkItemImpl) (workItem)).getDeploymentId(), message);
        Object data = workItem.getParameter("Data");
        if (data != null) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(bout);
                oout.writeObject(data);
                message.writeBytes(bout.toByteArray());
            } catch (IOException e) {
                JMSSendTaskWorkItemHandler.logger.warn("Error serializing context data", e);
            }
        } 
        return message;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        if (((connectionFactory) == null) || ((destination) == null)) {
            throw new RuntimeException("Connection factory and destination must be set for JMS send task handler");
        } 
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            Message message = createMessage(workItem, session);
            producer = session.createProducer(destination);
            producer.send(message);
        } catch (Exception e) {
            handleException(e);
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException e) {
                    JMSSendTaskWorkItemHandler.logger.warn("Error when closing producer", e);
                }
            } 
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    JMSSendTaskWorkItemHandler.logger.warn("Error when closing queue session", e);
                }
            } 
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    JMSSendTaskWorkItemHandler.logger.warn("Error when closing queue connection", e);
                }
            } 
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // no-op
    }

    @Override
    public void close() {
        connectionFactory = null;
        destination = null;
    }

    protected void addPropertyIfExists(String propertyName, Object properyValue, Message msg) throws JMSException {
        if (properyValue != null) {
            msg.setObjectProperty(propertyName, properyValue);
        } 
    }
}

