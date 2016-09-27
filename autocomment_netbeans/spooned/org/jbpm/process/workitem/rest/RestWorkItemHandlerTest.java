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


package org.jbpm.process.workitem.rest;

import org.junit.AfterClass;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import java.util.Collection;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import java.util.Map;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.endpoint.Server;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.kie.api.runtime.process.WorkItemManager;

@RunWith(value = Parameterized.class)
public class RestWorkItemHandlerTest {
    @Parameters(name = "Http Client 4.3 api = {0}")
    public static Collection<Object[]> parameters() {
        Object[][] locking = new Object[][]{ new Object[]{ true } , new Object[]{ false } };
        return Arrays.asList(locking);
    }

    private final boolean httpClient43;

    private static final String serverURL = "http://localhost:9998/test";

    private static Server server;

    public RestWorkItemHandlerTest(boolean httpClient43) {
        this.httpClient43 = httpClient43;
    }

    @SuppressWarnings(value = { "rawtypes" })
    @BeforeClass
    public static void initialize() throws Exception {
        SimpleRESTApplication application = new SimpleRESTApplication();
        RuntimeDelegate delegate = RuntimeDelegate.getInstance();
        JAXRSServerFactoryBean bean = delegate.createEndpoint(application, JAXRSServerFactoryBean.class);
        bean.setProvider(new org.apache.cxf.jaxrs.provider.JAXBElementProvider());
        bean.setAddress(("http://localhost:9998" + (bean.getAddress())));
        RestWorkItemHandlerTest.server = bean.create();
        RestWorkItemHandlerTest.server.start();
    }

    @AfterClass
    public static void destroy() throws Exception {
        if ((RestWorkItemHandlerTest.server) != null) {
            RestWorkItemHandlerTest.server.stop();
            RestWorkItemHandlerTest.server.destroy();
        } 
    }

    @Before
    public void setClientApiVersion() {
        RESTWorkItemHandler.HTTP_CLIENT_API_43 = httpClient43;
    }

    @Test
    public void testGETOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", RestWorkItemHandlerTest.serverURL);
        workItem.setParameter("Method", "GET");
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        String result = ((String) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Hello from REST", result);
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testGETOperationWithCustomTimeout() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", RestWorkItemHandlerTest.serverURL);
        workItem.setParameter("Method", "GET");
        workItem.setParameter("ConnectTimeout", "30000");
        workItem.setParameter("ReadTimeout", "25000");
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        String result = ((String) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Hello from REST", result);
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testGETOperationWithInvalidTimeout() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", RestWorkItemHandlerTest.serverURL);
        workItem.setParameter("Method", "GET");
        workItem.setParameter("ConnectTimeout", "");
        workItem.setParameter("ReadTimeout", "");
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        String result = ((String) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Hello from REST", result);
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testGETOperationWithQueryParam() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "?param=test"));
        workItem.setParameter("Method", "GET");
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        String result = ((String) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Hello from REST test", result);
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testPOSTOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<person><age>25</age><name>Post john</name></person>";
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/xml"));
        workItem.setParameter("Method", "POST");
        workItem.setParameter("ContentType", "application/xml");
        workItem.setParameter("Content", "<person><name>john</name><age>25</age></person>");
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        String result = ((String) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals(expected, result);
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testPOSTOperationWithPathParamAndNoContent() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/john"));
        workItem.setParameter("Method", "POST");
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        String result = ((String) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Created resource with name john", result);
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testPUTOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<person><age>25</age><name>Put john</name></person>";
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/xml"));
        workItem.setParameter("Method", "PUT");
        workItem.setParameter("ContentType", "application/xml");
        workItem.setParameter("Content", "<person><name>john</name><age>25</age></person>");
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        String result = ((String) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals(expected, result);
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testDELETEOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<person><age>-1</age><name>deleted john</name></person>";
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/xml/john"));
        workItem.setParameter("Method", "DELETE");
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        String result = ((String) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals(expected, result);
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/xml/john"));
        workItem.setParameter("Method", "HEAD");
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
    }

    @Test
    public void testHandleErrorOnNotSuccessfulResponse() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/notexisting"));
        workItem.setParameter("Method", "GET");
        workItem.setParameter("HandleResponseErrors", "true");
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        try {
            handler.executeWorkItem(workItem, manager);
            Assert.fail("Should throw exception as it was instructed to do so");
        } catch (WorkItemHandlerRuntimeException ex) {
            RESTServiceException e = ((RESTServiceException) (ex.getCause().getCause()));
            Assert.assertEquals(405, e.getStatus());
            Assert.assertEquals(((RestWorkItemHandlerTest.serverURL) + "/notexisting"), e.getEndoint());
            Assert.assertEquals("", e.getResponse());
        }
    }

    @Test
    public void testGETOperationWithXmlTrasformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/xml"));
        workItem.setParameter("Method", "GET");
        workItem.setParameter("ResultClass", Person.class.getName());
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        Person result = ((Person) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Person Xml", result.getName());
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testGETOperationWithJsonTrasformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/json"));
        workItem.setParameter("Method", "GET");
        workItem.setParameter("ResultClass", Person.class.getName());
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        Person result = ((Person) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Person Json", result.getName());
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testPOSTOperationWithXmlTransformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/xml"));
        workItem.setParameter("Method", "POST");
        workItem.setParameter("ContentType", "Application/XML;charset=utf-8");
        workItem.setParameter("Content", "<person><name>john</name><age>25</age></person>");
        workItem.setParameter("ResultClass", Person.class.getName());
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        Person result = ((Person) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Post john", result.getName());
        Assert.assertEquals(25, result.getAge().intValue());
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testPUTOperationWithXmlTransformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/xml"));
        workItem.setParameter("Method", "PUT");
        workItem.setParameter("ContentType", "Application/Xml;charset=utf-8");
        workItem.setParameter("Content", "<person><name>john</name><age>25</age></person>");
        workItem.setParameter("ResultClass", Person.class.getName());
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        Person result = ((Person) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Put john", result.getName());
        Assert.assertEquals(25, result.getAge().intValue());
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testPOSTOperationWithCompleteXmlTransformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        Person request = new Person();
        request.setAge(25);
        request.setName("john");
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/xml"));
        workItem.setParameter("Method", "POST");
        workItem.setParameter("ContentType", "application/xml");
        workItem.setParameter("Content", request);
        workItem.setParameter("ResultClass", Person.class.getName());
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        Person result = ((Person) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Post john", result.getName());
        Assert.assertEquals(25, result.getAge().intValue());
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testGETOperationWithXmlCharsetTrasformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/xml-charset"));
        workItem.setParameter("Method", "GET");
        workItem.setParameter("ResultClass", Person.class.getName());
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        Person result = ((Person) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Person Xml", result.getName());
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testGETOperationWithJsonCharsetTrasformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/json-charset"));
        workItem.setParameter("Method", "GET");
        workItem.setParameter("ResultClass", Person.class.getName());
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        Person result = ((Person) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals("Person Json", result.getName());
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testPUTOperationWithDefaultCharset() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String nonAsciiData = "\u0418\u0432\u0430\u043d";
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<person><age>25</age><name>Put ????</name></person>";
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/xml-charset"));
        workItem.setParameter("Method", "PUT");
        workItem.setParameter("ContentType", "application/xml");
        workItem.setParameter("Content", (("<person><name>" + nonAsciiData) + "</name><age>25</age></person>"));
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        String result = ((String) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals(expected, result);
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    @Test
    public void testPUTOperationWithCharsetSpecified() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String nonAsciiData = "\u0418\u0432\u0430\u043d";
        String expected = (("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<person><age>25</age><name>Put ") + nonAsciiData) + "</name></person>";
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((RestWorkItemHandlerTest.serverURL) + "/xml-charset"));
        workItem.setParameter("Method", "PUT");
        workItem.setParameter("ContentType", "application/xml; charset=utf-8");
        workItem.setParameter("Content", (("<person><name>" + nonAsciiData) + "</name><age>25</age></person>"));
        WorkItemManager manager = new RestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        String result = ((String) (workItem.getResult("Result")));
        Assert.assertNotNull("result cannot be null", result);
        Assert.assertEquals(expected, result);
        int responseCode = ((Integer) (workItem.getResult("Status")));
        Assert.assertNotNull(responseCode);
        Assert.assertEquals(200, responseCode);
        String responseMsg = ((String) (workItem.getResult("StatusMsg")));
        Assert.assertNotNull(responseMsg);
        Assert.assertEquals((("request to endpoint " + (workItem.getParameter("Url"))) + " successfully completed OK"), responseMsg);
    }

    private class TestWorkItemManager implements WorkItemManager {
        private WorkItem workItem;

        TestWorkItemManager(WorkItem workItem) {
            RestWorkItemHandlerTest.TestWorkItemManager.this.workItem = workItem;
        }

        @Override
        public void completeWorkItem(long id, Map<String, Object> results) {
            ((WorkItemImpl) (workItem)).setResults(results);
        }

        @Override
        public void abortWorkItem(long id) {
        }

        @Override
        public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        }
    }
}

