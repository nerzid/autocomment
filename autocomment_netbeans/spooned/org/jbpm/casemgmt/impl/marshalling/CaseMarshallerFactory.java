/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.casemgmt.impl.marshalling;

import java.util.ArrayList;
import java.util.List;
import org.kie.api.marshalling.ObjectMarshallingStrategy;

/**
 * Utility class to help with creating properly setup CaseFileInstanceMarshallerStrategy
 */
public class CaseMarshallerFactory {
    private List<ObjectMarshallingStrategy> marshallers = new ArrayList<ObjectMarshallingStrategy>();

    private ClassLoader classLoader;

    private StringBuilder toString = new StringBuilder();

    private CaseMarshallerFactory() {
        CaseMarshallerFactory.this.classLoader = Thread.currentThread().getContextClassLoader();
        CaseMarshallerFactory.this.toString.append(getClass().getName()).append(".").append("builder()");
    }

    private CaseMarshallerFactory(ClassLoader classLoader) {
        CaseMarshallerFactory.this.classLoader = classLoader;
        CaseMarshallerFactory.this.toString.append(getClass().getName()).append(".").append("builder(classLoader)");
    }

    /**
     * Adds document marshalling strategy to be used by CaseFileMarshaller
     * @return this factory instance
     */
    public CaseMarshallerFactory withDoc() {
        marshallers.add(new org.jbpm.document.marshalling.DocumentMarshallingStrategy());
        CaseMarshallerFactory.this.toString.append(".withDoc()");
        return CaseMarshallerFactory.this;
    }

    /**
     * Add JPA marshalling strategy to be used by CaseFileMarshaller
     * @param puName persistence unit name to be used
     * @return this factory instance
     */
    public CaseMarshallerFactory withJpa(String puName) {
        marshallers.add(new org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy(puName, classLoader));
        CaseMarshallerFactory.this.toString.append(((".withJpa(\"" + puName) + "\")"));
        return CaseMarshallerFactory.this;
    }

    /**
     * Adds given custom marshalling strategy to be used by CaseFileMarshaller
     * @param custom any marshalling strategy fully configured
     * @return this factory instance
     */
    public CaseMarshallerFactory with(ObjectMarshallingStrategy custom) {
        marshallers.add(custom);
        CaseMarshallerFactory.this.toString.append(((".with(new " + (custom.getClass().getName())) + "())"));
        return CaseMarshallerFactory.this;
    }

    /**
     * Returns fully configured CaseFileMarshaller with previously set child marshallers
     */
    public ObjectMarshallingStrategy get() {
        return new CaseFileInstanceMarshallingStrategy(marshallers.toArray(new ObjectMarshallingStrategy[marshallers.size()]));
    }

    /**
     * Retruns string representation (as mvel expression) of the configuration
     */
    @Override
    public String toString() {
        String complete = toString.toString();
        complete += ".get();";
        return complete;
    }

    /**
     * Builds new instance of the factory to append marshalling strategies to
     * @param classLoader custom class loader to be used
     */
    public static CaseMarshallerFactory builder(ClassLoader classLoader) {
        return new CaseMarshallerFactory(classLoader);
    }

    /**
     * Builds new instance of the factory to append marshalling strategies to
     * with thread context classloader
     */
    public static CaseMarshallerFactory builder() {
        return new CaseMarshallerFactory();
    }
}

