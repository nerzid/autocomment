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


package org.jbpm.process.workitem.jabber;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import java.util.ArrayList;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import Presence.Type;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.jivesoftware.smack.XMPPConnection;

/**
 * @author salaboy
 */
public class JabberWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
    private static final Logger logger = LoggerFactory.getLogger(JabberWorkItemHandler.class);

    private String user;

    private String password;

    private String server;

    private int port;

    private String service;

    private String text;

    private List<String> toUsers = new ArrayList<String>();

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        JabberWorkItemHandler.this.user = ((String) (workItem.getParameter("User")));
        JabberWorkItemHandler.this.password = ((String) (workItem.getParameter("Password")));
        JabberWorkItemHandler.this.server = ((String) (workItem.getParameter("Server")));
        String portString = ((String) (workItem.getParameter("Port")));
        if ((portString != null) && (!(portString.equals("")))) {
            JabberWorkItemHandler.this.port = Integer.valueOf(((String) (workItem.getParameter("Port"))));
        } 
        JabberWorkItemHandler.this.service = ((String) (workItem.getParameter("Service")));
        JabberWorkItemHandler.this.text = ((String) (workItem.getParameter("Text")));
        String to = ((String) (workItem.getParameter("To")));
        if ((to == null) || ((to.trim().length()) == 0)) {
            throw new RuntimeException("IM must have one or more to adresses");
        } 
        for (String s : to.split(";")) {
            if ((s != null) && (!("".equals(s)))) {
                JabberWorkItemHandler.this.toUsers.add(s);
            } 
        }
        ConnectionConfiguration conf = new ConnectionConfiguration(server, port, service);
        XMPPConnection connection = null;
        try {
            if ((((server) != null) && (!(server.equals("")))) && ((port) != 0)) {
                connection = new XMPPConnection(conf);
            } else {
                connection = new XMPPConnection(service);
            }
            connection.connect();
            JabberWorkItemHandler.logger.info("Connected to {}", connection.getHost());
            connection.login(user, password);
            JabberWorkItemHandler.logger.info("Logged in as {}", connection.getUser());
            Presence presence = new Presence(Type.available);
            connection.sendPacket(presence);
            for (String toUser : toUsers) {
                ChatManager chatmanager = connection.getChatManager();
                Chat chat = chatmanager.createChat(toUser, null);
                // google bounces back the default message types, you must use chat
                Message msg = new Message(toUser, Message.Type.chat);
                msg.setBody(text);
                chat.sendMessage(msg);
                JabberWorkItemHandler.logger.info("Message Sent {}", msg);
            }
            connection.disconnect();
            manager.completeWorkItem(workItem.getId(), null);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

