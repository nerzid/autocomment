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
import org.jbpm.runtime.manager.impl.deploy.testobject.EmbedingCustomObject;
import java.util.HashMap;
import java.util.Map;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.ObjectModelResolver;
import org.jbpm.runtime.manager.impl.deploy.testobject.SimpleCustomObject;
import org.junit.Test;
import org.jbpm.runtime.manager.impl.deploy.testobject.ThirdLevelCustomObject;

public class ReflectionObjectModelResolverTest {
    @Test
    public void testSimpleNoArgObjectModel() {
        ObjectModel model = new ObjectModel("org.jbpm.runtime.manager.impl.deploy.testobject.SimpleCustomObject");
        ObjectModelResolver resolver = new ReflectionObjectModelResolver();
        Object result = resolver.getInstance(model, ReflectionObjectModelResolverTest.this.getClass().getClassLoader(), new HashMap<String, Object>());
        Assert.assertNotNull(result);
        Assert.assertTrue((result instanceof SimpleCustomObject));
        Assert.assertEquals("default", ((SimpleCustomObject) (result)).getName());
    }

    @Test
    public void testSimpleSingleStringArgObjectModel() {
        ObjectModel model = new ObjectModel("org.jbpm.runtime.manager.impl.deploy.testobject.SimpleCustomObject", new Object[]{ "john" });
        ObjectModelResolver resolver = new ReflectionObjectModelResolver();
        Object result = resolver.getInstance(model, ReflectionObjectModelResolverTest.this.getClass().getClassLoader(), new HashMap<String, Object>());
        Assert.assertNotNull(result);
        Assert.assertTrue((result instanceof SimpleCustomObject));
        Assert.assertEquals("john", ((SimpleCustomObject) (result)).getName());
    }

    @Test
    public void testSimpleSingleObjectArgObjectModel() {
        ObjectModel model = new ObjectModel("org.jbpm.runtime.manager.impl.deploy.testobject.EmbedingCustomObject", new Object[]{ new ObjectModel("org.jbpm.runtime.manager.impl.deploy.testobject.SimpleCustomObject", new Object[]{ "john" }) , "testing object model" });
        ObjectModelResolver resolver = new ReflectionObjectModelResolver();
        Object result = resolver.getInstance(model, ReflectionObjectModelResolverTest.this.getClass().getClassLoader(), new HashMap<String, Object>());
        Assert.assertNotNull(result);
        Assert.assertTrue((result instanceof EmbedingCustomObject));
        Assert.assertEquals("testing object model", ((EmbedingCustomObject) (result)).getDescription());
        SimpleCustomObject customObject = ((EmbedingCustomObject) (result)).getCustomObject();
        Assert.assertNotNull(customObject);
        Assert.assertEquals("john", customObject.getName());
    }

    @Test
    public void testSimpleNestedObjectArgObjectModel() {
        ObjectModel model = new ObjectModel("org.jbpm.runtime.manager.impl.deploy.testobject.ThirdLevelCustomObject", new Object[]{ new ObjectModel("org.jbpm.runtime.manager.impl.deploy.testobject.EmbedingCustomObject", new Object[]{ new ObjectModel("org.jbpm.runtime.manager.impl.deploy.testobject.SimpleCustomObject", new Object[]{ "john" }) , "testing object model" }) });
        ObjectModelResolver resolver = new ReflectionObjectModelResolver();
        Object result = resolver.getInstance(model, ReflectionObjectModelResolverTest.this.getClass().getClassLoader(), new HashMap<String, Object>());
        Assert.assertNotNull(result);
        Assert.assertTrue((result instanceof ThirdLevelCustomObject));
        Assert.assertEquals("testing object model", ((ThirdLevelCustomObject) (result)).getEmbeddedObject().getDescription());
        SimpleCustomObject customObject = ((ThirdLevelCustomObject) (result)).getEmbeddedObject().getCustomObject();
        Assert.assertNotNull(customObject);
        Assert.assertEquals("john", customObject.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSimpleNotExistingObjectModel() {
        ObjectModel model = new ObjectModel("org.jbpm.runtime.manager.impl.deploy.testobject.NotExistingObject");
        ObjectModelResolver resolver = new ReflectionObjectModelResolver();
        resolver.getInstance(model, ReflectionObjectModelResolverTest.this.getClass().getClassLoader(), new HashMap<String, Object>());
    }

    @Test
    public void testSimpleContextValueObjectModel() {
        ObjectModel model = new ObjectModel("org.jbpm.runtime.manager.impl.deploy.testobject.SimpleCustomObject", new Object[]{ "context" });
        ObjectModelResolver resolver = new ReflectionObjectModelResolver();
        Map<String, Object> contextParam = new HashMap<String, Object>();
        contextParam.put("context", "value from the context");
        Object result = resolver.getInstance(model, ReflectionObjectModelResolverTest.this.getClass().getClassLoader(), contextParam);
        Assert.assertNotNull(result);
        Assert.assertTrue((result instanceof SimpleCustomObject));
        Assert.assertEquals("value from the context", ((SimpleCustomObject) (result)).getName());
    }

    @Test
    public void testSimpleNoArgNamedObjectModel() {
        NamedObjectModel model = new NamedObjectModel("CustomObject", "org.jbpm.runtime.manager.impl.deploy.testobject.SimpleCustomObject");
        Assert.assertEquals("CustomObject", model.getName());
        ObjectModelResolver resolver = new ReflectionObjectModelResolver();
        Object result = resolver.getInstance(model, ReflectionObjectModelResolverTest.this.getClass().getClassLoader(), new HashMap<String, Object>());
        Assert.assertNotNull(result);
        Assert.assertTrue((result instanceof SimpleCustomObject));
        Assert.assertEquals("default", ((SimpleCustomObject) (result)).getName());
    }
}

