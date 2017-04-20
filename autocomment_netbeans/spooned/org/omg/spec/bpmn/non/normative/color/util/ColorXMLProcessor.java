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
/**
 *
 */
/**
 * ColorXMLProcessor
 */


package org.omg.spec.bpmn.non.normative.color.util;

import EPackage.Registry.INSTANCE;
import java.util.Map;
import org.eclipse.emf.ecore.xmi.util.XMLProcessor;
import ColorPackage.eINSTANCE;

/**
 * This class contains helper methods to serialize and deserialize XML documents
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ColorXMLProcessor extends XMLProcessor {
    /**
     * Public constructor to instantiate the helper.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ColorXMLProcessor() {
        super(INSTANCE);
        eINSTANCE.eClass();
    }

    /**
     * Register for "*" and "xml" file extensions the ColorResourceFactoryImpl factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected Map<String, Resource.Factory> getRegistrations() {
        if ((registrations) == null) {
            super.getRegistrations();
            registrations.put(XML_EXTENSION, new ColorResourceFactoryImpl());
            registrations.put(STAR_EXTENSION, new ColorResourceFactoryImpl());
        }
        return registrations;
    }
}

