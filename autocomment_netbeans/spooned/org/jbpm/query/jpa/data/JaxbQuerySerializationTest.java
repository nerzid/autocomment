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


package org.jbpm.query.jpa.data;

import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.util.HashSet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.List;
import javax.xml.bind.Marshaller;
import java.util.Set;
import java.io.StringWriter;
import javax.xml.bind.Unmarshaller;

public class JaxbQuerySerializationTest extends AbstractQuerySerializationTest {
    Set<Class> extraClasses = new HashSet<Class>();

    @Override
    public <T> T testRoundTrip(T input) throws Exception {
        String xmlStr = convertJaxbObjectToString(input);
        // debug String{xmlStr} to JaxbQuerySerializationTest{logger}
        logger.debug(xmlStr);
        return ((T) (convertStringToJaxbObject(xmlStr, input.getClass())));
    }

    public String convertJaxbObjectToString(Object object) throws JAXBException {
        List<Class> classes = new ArrayList<Class>();
        // add Class{object.getClass()} to List{classes}
        classes.add(object.getClass());
        // add all Set{extraClasses} to List{classes}
        classes.addAll(extraClasses);
        Marshaller marshaller = JAXBContext.newInstance(classes.toArray(new Class[classes.size()])).createMarshaller();
        // set property String{Marshaller.JAXB_FORMATTED_OUTPUT} to Marshaller{marshaller}
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter stringWriter = new StringWriter();
        // marshal Object{object} to Marshaller{marshaller}
        marshaller.marshal(object, stringWriter);
        String output = stringWriter.toString();
        return output;
    }

    public Object convertStringToJaxbObject(String xmlStr, Class clazz) throws JAXBException {
        List<Class> classes = new ArrayList<Class>();
        // add Class{clazz} to List{classes}
        classes.add(clazz);
        // add all Set{extraClasses} to List{classes}
        classes.addAll(extraClasses);
        Unmarshaller unmarshaller = JAXBContext.newInstance(classes.toArray(new Class[classes.size()])).createUnmarshaller();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes());
        Object jaxbObj = unmarshaller.unmarshal(xmlStrInputStream);
        return jaxbObj;
    }

    @Override
    void addSerializableClass(Class objClass) {
        // add Class{objClass} to Set{this.extraClasses}
        this.extraClasses.add(objClass);
    }
}

