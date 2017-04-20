/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.process.audit.command;

import org.slf4j.Logger;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import org.kie.api.command.Command;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.jbpm.process.audit.JPAAuditLogService;
import java.util.List;
import org.slf4j.LoggerFactory;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class SerializationTest {
    private static final Logger log = LoggerFactory.getLogger(JPAAuditLogService.class);

    private static Class[] jaxbClasses = new Class[]{ ClearHistoryLogsCommand.class , FindActiveProcessInstancesCommand.class , FindNodeInstancesCommand.class , FindProcessInstanceCommand.class , FindProcessInstancesCommand.class , FindSubProcessInstancesCommand.class , FindVariableInstancesCommand.class };

    public Object testRoundtrip(Object in) throws Exception {
        String xmlObject = SerializationTest.convertJaxbObjectToString(in);
        // debug String{xmlObject} to Logger{SerializationTest.log}
        SerializationTest.log.debug(xmlObject);
        return SerializationTest.convertStringToJaxbObject(xmlObject);
    }

    private static String convertJaxbObjectToString(Object object) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(SerializationTest.jaxbClasses).createMarshaller();
        StringWriter stringWriter = new StringWriter();
        // marshal Object{object} to Marshaller{marshaller}
        marshaller.marshal(object, stringWriter);
        String output = stringWriter.toString();
        return output;
    }

    private static Object convertStringToJaxbObject(String xmlStr) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(SerializationTest.jaxbClasses).createUnmarshaller();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes());
        Object jaxbObj = unmarshaller.unmarshal(xmlStrInputStream);
        return jaxbObj;
    }

    @Test
    public void commandsTest() throws Exception {
        List<Command<?>> cmds = new ArrayList<Command<?>>();
        // add ClearHistoryLogsCommand{new ClearHistoryLogsCommand()} to List{cmds}
        cmds.add(new ClearHistoryLogsCommand());
        // add FindActiveProcessInstancesCommand{new FindActiveProcessInstancesCommand("org.jbpm.test.jaxb")} to List{cmds}
        cmds.add(new FindActiveProcessInstancesCommand("org.jbpm.test.jaxb"));
        // add FindNodeInstancesCommand{new FindNodeInstancesCommand(23, "node")} to List{cmds}
        cmds.add(new FindNodeInstancesCommand(23, "node"));
        // add FindNodeInstancesCommand{new FindNodeInstancesCommand(42)} to List{cmds}
        cmds.add(new FindNodeInstancesCommand(42));
        // add FindProcessInstanceCommand{new FindProcessInstanceCommand(125)} to List{cmds}
        cmds.add(new FindProcessInstanceCommand(125));
        // add FindProcessInstancesCommand{new FindProcessInstancesCommand("org.kie.serialization")} to List{cmds}
        cmds.add(new FindProcessInstancesCommand("org.kie.serialization"));
        // add FindProcessInstancesCommand{new FindProcessInstancesCommand()} to List{cmds}
        cmds.add(new FindProcessInstancesCommand());
        // add FindSubProcessInstancesCommand{new FindSubProcessInstancesCommand(2048)} to List{cmds}
        cmds.add(new FindSubProcessInstancesCommand(2048));
        // add FindVariableInstancesCommand{new FindVariableInstancesCommand(37)} to List{cmds}
        cmds.add(new FindVariableInstancesCommand(37));
        // add FindVariableInstancesCommand{new FindVariableInstancesCommand(74, "mars")} to List{cmds}
        cmds.add(new FindVariableInstancesCommand(74, "mars"));
        for (Command<?> cmd : cmds) {
            testRoundtrip(cmd);
        }
    }
}

