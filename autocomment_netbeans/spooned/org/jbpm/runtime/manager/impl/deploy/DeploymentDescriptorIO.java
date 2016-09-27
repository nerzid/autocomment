/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.runtime.manager.impl.deploy;

import org.kie.internal.runtime.conf.DeploymentDescriptor;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.xml.sax.SAXException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringWriter;
import java.net.URL;
import javax.xml.bind.Unmarshaller;
import javax.xml.XMLConstants;

/**
 * XML based deployment descriptor IO manager to read and write descriptors.
 * Underlying uses <code>XStream</code> for serialization with special class
 * and field mapping for more readability of the produced XML output.
 */
public class DeploymentDescriptorIO {
    private static JAXBContext context = null;

    private static Schema schema = null;

    /**
     * Reads XML data from given input stream and produces valid instance of
     * <code>DeploymentDescriptor</code>
     * @param inputStream input stream that comes with xml data of the descriptor
     * @return instance of the descriptor after deserialization
     */
    public static DeploymentDescriptor fromXml(InputStream inputStream) {
        try {
            Unmarshaller unmarshaller = DeploymentDescriptorIO.getContext().createUnmarshaller();
            unmarshaller.setSchema(DeploymentDescriptorIO.schema);
            DeploymentDescriptor descriptor = ((DeploymentDescriptor) (unmarshaller.unmarshal(inputStream)));
            return descriptor;
        } catch (Exception e) {
            throw new RuntimeException("Unable to read deployment descriptor from xml", e);
        }
    }

    /**
     * Serializes descriptor instance to XML
     * @param descriptor descriptor to be serialized
     * @return xml representation of descriptor as string
     */
    public static String toXml(DeploymentDescriptor descriptor) {
        try {
            Marshaller marshaller = DeploymentDescriptorIO.getContext().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.jboss.org/jbpm deployment-descriptor.xsd");
            marshaller.setSchema(DeploymentDescriptorIO.schema);
            StringWriter stringWriter = new StringWriter();
            // clone the object and cleanup transients
            DeploymentDescriptor clone = ((DeploymentDescriptorImpl) (descriptor)).clearClone();
            marshaller.marshal(clone, stringWriter);
            String output = stringWriter.toString();
            return output;
        } catch (Exception e) {
            throw new RuntimeException("Unable to generate xml from deployment descriptor", e);
        }
    }

    public static JAXBContext getContext() throws JAXBException, SAXException {
        if ((DeploymentDescriptorIO.context) == null) {
            Class<?>[] jaxbClasses = new Class<?>[]{ DeploymentDescriptorImpl.class };
            DeploymentDescriptorIO.context = JAXBContext.newInstance(jaxbClasses);
            // load schema for validation
            URL schemaLocation = DeploymentDescriptorIO.class.getResource("/deployment-descriptor.xsd");
            DeploymentDescriptorIO.schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaLocation);
        } 
        return DeploymentDescriptorIO.context;
    }
}

