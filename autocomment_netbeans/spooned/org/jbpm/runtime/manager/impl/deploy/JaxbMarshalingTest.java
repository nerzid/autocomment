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


package org.jbpm.runtime.manager.impl.deploy;

import org.junit.Assert;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import org.junit.Test;

public class JaxbMarshalingTest {
    private static final Logger logger = LoggerFactory.getLogger(JaxbMarshalingTest.class);

    private Class<?>[] jaxbClasses = new Class<?>[]{ DeploymentDescriptorImpl.class };

    @Test
    public void testJaxbDeploymentDescriptorSerialization() throws Exception {
        DeploymentDescriptor descriptor = new DeploymentDescriptorImpl();
        descriptor.getBuilder().addTaskEventListener(new org.kie.internal.runtime.conf.ObjectModel("org.jbpm.task.Listener", new Object[]{ "test" , "another" }));
        String output = convertJaxbObjectToString(descriptor);
        JaxbMarshalingTest.logger.debug(output);
        Assert.assertNotNull(output);
    }

    public String convertJaxbObjectToString(Object object) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(jaxbClasses).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(object, stringWriter);
        String output = stringWriter.toString();
        return output;
    }
}

