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


package org.jbpm.bpmn2.emfextmodel;

import org.omg.spec.bpmn.non.normative.color.DocumentRoot;
import org.eclipse.emf.ecore.resource.ResourceSet;
import junit.framework.TestCase;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLResource;

public class BPMN2ColorTest extends TestCase {
    private ResourceSet resourceSet;

    @Override
    protected void setUp() throws Exception {
        resourceSet = new org.eclipse.emf.ecore.resource.impl.ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new org.omg.spec.bpmn.non.normative.color.util.ColorResourceFactoryImpl());
        resourceSet.getPackageRegistry().put(ColorPackage.eNS_URI, ColorPackage.eINSTANCE);
    }

    @Override
    protected void tearDown() throws Exception {
    }

    @SuppressWarnings(value = "unchecked")
    public void testColorAttributes() throws Exception {
        XMLResource inResource = ((XMLResource) (resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"))));
        inResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        inResource.setEncoding("UTF-8");
        DocumentRoot documentRoot = ColorFactory.eINSTANCE.createDocumentRoot();
        documentRoot.setBackgroundColor("#FFFFFF");
        documentRoot.setBorderColor("#000000");
        documentRoot.setColor("#AAAAAA");
        inResource.getContents().add(documentRoot);
        DocumentRoot outRoot = ((DocumentRoot) (inResource.getContents().get(0)));
        assertNotNull(outRoot);
        assertEquals(outRoot.getBackgroundColor(), "#FFFFFF");
        assertEquals(outRoot.getBorderColor(), "#000000");
        assertEquals(outRoot.getColor(), "#AAAAAA");
    }
}

