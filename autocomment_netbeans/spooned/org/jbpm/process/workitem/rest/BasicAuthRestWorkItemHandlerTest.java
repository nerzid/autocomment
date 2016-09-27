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
import java.util.Base64;
import org.junit.Before;
import org.junit.BeforeClass;
import java.util.Collection;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import java.util.Map;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import javax.ws.rs.core.Response;
import org.junit.runner.RunWith;
import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.endpoint.Server;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.kie.api.runtime.process.WorkItemManager;

@RunWith(value = Parameterized.class)
public class BasicAuthRestWorkItemHandlerTest {
    @Parameters(name = "Http Client 4.3 api = {0}")
    public static Collection<Object[]> parameters() {
        Object[][] locking = new Object[][]{ new Object[]{ true } , new Object[]{ false } };
        return Arrays.asList(locking);
    }

    private static final String SERVER_URL = "http://localhost:9998/test";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    private static Server server;

    private final boolean httpClient43;

    public BasicAuthRestWorkItemHandlerTest(boolean httpClient43) {
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
        // disabled logging interceptor by default but proves to be useful
        // bean.getInInterceptors().add(new LoggingInInterceptor(new PrintWriter(System.out, true)));
        bean.setProvider(new BasicAuthRestWorkItemHandlerTest.AuthenticationFilter());
        BasicAuthRestWorkItemHandlerTest.server = bean.create();
        BasicAuthRestWorkItemHandlerTest.server.start();
    }

    @AfterClass
    public static void destroy() throws Exception {
        if ((BasicAuthRestWorkItemHandlerTest.server) != null) {
            BasicAuthRestWorkItemHandlerTest.server.stop();
            BasicAuthRestWorkItemHandlerTest.server.destroy();
        } 
    }

    @Before
    public void setClientApiVersion() {
        RESTWorkItemHandler.HTTP_CLIENT_API_43 = httpClient43;
    }

    @Test
    public void testGETOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler(BasicAuthRestWorkItemHandlerTest.USERNAME, BasicAuthRestWorkItemHandlerTest.PASSWORD);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", BasicAuthRestWorkItemHandlerTest.SERVER_URL);
        workItem.setParameter("Method", "GET");
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(BasicAuthRestWorkItemHandlerTest.USERNAME, BasicAuthRestWorkItemHandlerTest.PASSWORD);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", BasicAuthRestWorkItemHandlerTest.SERVER_URL);
        workItem.setParameter("Method", "GET");
        workItem.setParameter("ConnectTimeout", "30000");
        workItem.setParameter("ReadTimeout", "25000");
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(BasicAuthRestWorkItemHandlerTest.USERNAME, BasicAuthRestWorkItemHandlerTest.PASSWORD);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", BasicAuthRestWorkItemHandlerTest.SERVER_URL);
        workItem.setParameter("Method", "GET");
        workItem.setParameter("ConnectTimeout", "");
        workItem.setParameter("ReadTimeout", "");
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(BasicAuthRestWorkItemHandlerTest.USERNAME, BasicAuthRestWorkItemHandlerTest.PASSWORD);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((BasicAuthRestWorkItemHandlerTest.SERVER_URL) + "?param=test"));
        workItem.setParameter("Method", "GET");
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(BasicAuthRestWorkItemHandlerTest.USERNAME, BasicAuthRestWorkItemHandlerTest.PASSWORD);
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<person><age>25</age><name>Post john</name></person>";
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((BasicAuthRestWorkItemHandlerTest.SERVER_URL) + "/xml"));
        workItem.setParameter("Method", "POST");
        workItem.setParameter("ContentType", "application/xml");
        workItem.setParameter("Content", "<person><name>john</name><age>25</age></person>");
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(BasicAuthRestWorkItemHandlerTest.USERNAME, BasicAuthRestWorkItemHandlerTest.PASSWORD);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((BasicAuthRestWorkItemHandlerTest.SERVER_URL) + "/john"));
        workItem.setParameter("Method", "POST");
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(BasicAuthRestWorkItemHandlerTest.USERNAME, BasicAuthRestWorkItemHandlerTest.PASSWORD);
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<person><age>25</age><name>Put john</name></person>";
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((BasicAuthRestWorkItemHandlerTest.SERVER_URL) + "/xml"));
        workItem.setParameter("Method", "PUT");
        workItem.setParameter("ContentType", "application/xml");
        workItem.setParameter("Content", "<person><name>john</name><age>25</age></person>");
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(BasicAuthRestWorkItemHandlerTest.USERNAME, BasicAuthRestWorkItemHandlerTest.PASSWORD);
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<person><age>-1</age><name>deleted john</name></person>";
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((BasicAuthRestWorkItemHandlerTest.SERVER_URL) + "/xml/john"));
        workItem.setParameter("Method", "DELETE");
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(BasicAuthRestWorkItemHandlerTest.USERNAME, BasicAuthRestWorkItemHandlerTest.PASSWORD);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((BasicAuthRestWorkItemHandlerTest.SERVER_URL) + "/xml/john"));
        workItem.setParameter("Method", "HEAD");
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
    }

    @Test
    public void testHandleErrorOnNotSuccessfulResponse() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler(BasicAuthRestWorkItemHandlerTest.USERNAME, BasicAuthRestWorkItemHandlerTest.PASSWORD);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", ((BasicAuthRestWorkItemHandlerTest.SERVER_URL) + "/notexisting"));
        workItem.setParameter("Method", "GET");
        workItem.setParameter("HandleResponseErrors", "true");
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
        try {
            handler.executeWorkItem(workItem, manager);
            Assert.fail("Should throw exception as it was instructed to do so");
        } catch (WorkItemHandlerRuntimeException ex) {
            RESTServiceException e = ((RESTServiceException) (ex.getCause().getCause()));
            Assert.assertEquals(405, e.getStatus());
            Assert.assertEquals(((BasicAuthRestWorkItemHandlerTest.SERVER_URL) + "/notexisting"), e.getEndoint());
            Assert.assertEquals("", e.getResponse());
        }
    }

    @Test
    public void testHandleErrorOnNotSuccessfulResponseWrongCredentials() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler(BasicAuthRestWorkItemHandlerTest.USERNAME, "wrongpassword");
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", BasicAuthRestWorkItemHandlerTest.SERVER_URL);
        workItem.setParameter("Method", "GET");
        workItem.setParameter("HandleResponseErrors", "true");
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
        try {
            handler.executeWorkItem(workItem, manager);
            Assert.fail("Should throw exception as it was instructed to do so");
        } catch (WorkItemHandlerRuntimeException ex) {
            RESTServiceException e = ((RESTServiceException) (ex.getCause().getCause()));
            Assert.assertEquals(401, e.getStatus());
            Assert.assertEquals(BasicAuthRestWorkItemHandlerTest.SERVER_URL, e.getEndoint());
            Assert.assertEquals("", e.getResponse());
        }
    }

    @Test
    public void testGETOperationAuthTypeAsParam() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url", BasicAuthRestWorkItemHandlerTest.SERVER_URL);
        workItem.setParameter("Method", "GET");
        workItem.setParameter("AuthType", "BASIC");
        workItem.setParameter("Username", BasicAuthRestWorkItemHandlerTest.USERNAME);
        workItem.setParameter("Password", BasicAuthRestWorkItemHandlerTest.PASSWORD);
        WorkItemManager manager = new BasicAuthRestWorkItemHandlerTest.TestWorkItemManager(workItem);
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

    private class TestWorkItemManager implements WorkItemManager {
        private WorkItem workItem;

        TestWorkItemManager(WorkItem workItem) {
            BasicAuthRestWorkItemHandlerTest.TestWorkItemManager.this.workItem = workItem;
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

    /**
     * Intercepts the request and checks whether the HTTP Basic authentication header was correctly set (e.g. has
     * correct username+password).
     * 
     * For test purposes only.
     */
    private static class AuthenticationFilter implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext containerRequestContext) throws IOException {
            String[] usernamePassword = decodeBase64UsernameAndPassword(containerRequestContext.getHeaderString("Authorization"));
            String username = usernamePassword[0];
            String password = usernamePassword[1];
            if (!(isAuthenticated(username, password))) {
                containerRequestContext.abortWith(Response.status(401).header("WWW-Authenticate", "Basic").build());
            } 
        }

        private String[] decodeBase64UsernameAndPassword(String base64authzHeader) {
            // extract just the username:password part (removing the "Basic " prefix)
            String usernamePasswordBase64 = base64authzHeader.substring("Basic ".length());
            String usernamePassword = new String(Base64.getDecoder().decode(usernamePasswordBase64), StandardCharsets.UTF_8);
            return usernamePassword.split(":");
        }

        private boolean isAuthenticated(String username, String password) {
            return (BasicAuthRestWorkItemHandlerTest.USERNAME.equals(username)) && (BasicAuthRestWorkItemHandlerTest.PASSWORD.equals(password));
        }
    }
}

