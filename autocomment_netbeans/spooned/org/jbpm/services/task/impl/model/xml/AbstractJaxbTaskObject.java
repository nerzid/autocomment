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


package org.jbpm.services.task.impl.model.xml;

import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.io.IOException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import java.util.List;
import java.lang.reflect.Method;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.kie.api.task.model.User;
import javax.xml.bind.annotation.XmlTransient;

@JsonIgnoreProperties(value = { "realClass" })
public class AbstractJaxbTaskObject<T> {
    @XmlTransient
    protected Class<?> realClass;

    public AbstractJaxbTaskObject() {
        throw new UnsupportedOperationException("No-arg constructor must be implemented by the concrete class.");
    }

    protected AbstractJaxbTaskObject(Class<?> realClass) {
        AbstractJaxbTaskObject.this.realClass = realClass;
    }

    protected AbstractJaxbTaskObject(T taskObject, Class<?> objectInterface) {
        this(objectInterface);
        initialize(taskObject, objectInterface);
    }

    protected void initialize(T taskObject, Class<?> objectInterface) {
        if (taskObject != null) {
            for (Method getIsMethod : objectInterface.getDeclaredMethods()) {
                String methodName = getIsMethod.getName();
                String fieldName;
                if (getIsMethod.getReturnType().equals(User.class)) {
                    continue;
                } 
                if (methodName.startsWith("set")) {
                    continue;
                } 
                if (methodName.startsWith("get")) {
                    fieldName = methodName.substring(3);
                } else if (methodName.startsWith("is")) {
                    fieldName = methodName.substring(2);
                } else {
                    assert false : ((("Unknown method '" + methodName) + "' in ") + (AbstractJaxbTaskObject.this.getClass().getSimpleName())) + ".";
                    continue;
                }
                // getField -> field (lowercase f)
                fieldName = (fieldName.substring(0, 1).toLowerCase()) + (fieldName.substring(1));
                try {
                    Field field = AbstractJaxbTaskObject.this.getClass().getDeclaredField(fieldName);
                    boolean origAccessStatus = field.isAccessible();
                    field.setAccessible(true);
                    Object setObject = getIsMethod.invoke(taskObject, new Object[0]);
                    field.set(AbstractJaxbTaskObject.this, setObject);
                    field.setAccessible(origAccessStatus);
                } catch (Exception e) {
                    throw new RuntimeException((((("Unable to initialize " + fieldName) + " when creating ") + (AbstractJaxbTaskObject.this.getClass().getSimpleName())) + "."), e);
                }
            }
        } 
    }

    static <T> T unsupported(Class<T> realClass) {
        String methodName = new Throwable().getStackTrace()[1].getMethodName();
        throw new UnsupportedOperationException((((methodName + " is not supported on the JAXB ") + (realClass.getSimpleName())) + " implementation."));
    }

    @SuppressWarnings(value = "unchecked")
    public static <I, J extends I> List<J> convertListFromInterfaceToJaxbImpl(List<I> interfacelList, Class<I> interfaceClass, Class<J> jaxbClass) {
        List<J> jaxbList;
        if (interfacelList != null) {
            jaxbList = new ArrayList<J>(interfacelList.size());
            for (I interfaze : interfacelList) {
                if (jaxbClass.isAssignableFrom(interfaze.getClass())) {
                    jaxbList.add(((J) (interfaze)));
                } else {
                    jaxbList.add(AbstractJaxbTaskObject.jaxbConstructorWithInternalAsArgument(jaxbClass, interfaceClass, interfaze));
                }
            }
        } else {
            jaxbList = new ArrayList<J>();
        }
        return jaxbList;
    }

    private static <J, I> J jaxbConstructorWithInternalAsArgument(Class<J> jaxbClass, Class<I> interfaze, I argument) {
        Class[] cnstrArgs = new Class[]{ interfaze };
        try {
            Constructor<J> cnstr = jaxbClass.getConstructor(cnstrArgs);
            return cnstr.newInstance(argument);
        } catch (Exception e) {
            throw new RuntimeException((((("Unable to create " + (jaxbClass.getName())) + " using constructor with ") + (interfaze.getName())) + " argument."), e);
        }
    }

    @SuppressWarnings(value = "unchecked")
    static <T> T whenNull(Object value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        } 
        return ((T) (value));
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        AbstractJaxbTaskObject.unsupported(realClass);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        AbstractJaxbTaskObject.unsupported(realClass);
    }
}

