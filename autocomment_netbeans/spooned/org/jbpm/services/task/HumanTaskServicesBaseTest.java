/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package org.jbpm.services.task;

import org.junit.AfterClass;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.BeforeClass;
import java.io.ByteArrayInputStream;
import org.kie.api.task.model.Content;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.services.task.util.CountDownTaskEventListener;
import java.util.Date;
import org.kie.internal.task.api.EventService;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.kie.internal.task.api.InternalTaskService;
import javax.xml.bind.JAXBContext;
import org.jbpm.services.task.impl.model.xml.JaxbContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.jbpm.services.task.utils.MVELUtils;
import java.util.Map;
import javax.xml.bind.Marshaller;
import java.text.ParseException;
import org.jbpm.persistence.util.PersistenceUtil;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.util.Properties;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.io.StringWriter;
import org.kie.api.task.TaskLifeCycleEventListener;
import javax.xml.bind.Unmarshaller;

public abstract class HumanTaskServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(HumanTaskServicesBaseTest.class);

    protected InternalTaskService taskService;

    @BeforeClass
    public static void configure() {
        LoggingPrintStream.interceptSysOutSysErr();
    }

    @AfterClass
    public static void reset() {
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }

    public void tearDown() {
        if ((taskService) != null) {
            int removeAllTasks = taskService.removeAllTasks();
            HumanTaskServicesBaseTest.logger.debug("Number of tasks removed {}", removeAllTasks);
        } 
    }

    @SuppressWarnings(value = { "unchecked" , "rawtypes" })
    public static Map fillUsersOrGroups(String mvelFileName) throws Exception {
        Map<String, Object> vars = new HashMap<String, Object>();
        Reader reader = null;
        Map<String, Object> result = null;
        try {
            reader = new InputStreamReader(HumanTaskServicesBaseTest.class.getResourceAsStream(mvelFileName));
            result = ((Map<String, Object>) (MVELUtils.eval(reader, vars)));
        } finally {
            if (reader != null) {
                reader.close();
            } 
        }
        return result;
    }

    protected static final String mySubject = "My Subject";

    protected static final String myBody = "My Body";

    protected static Map<String, String> fillMarshalSubjectAndBodyParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("subject", HumanTaskServicesBaseTest.mySubject);
        params.put("body", HumanTaskServicesBaseTest.myBody);
        return params;
    }

    protected static void checkContentSubjectAndBody(Object unmarshalledObject) {
        Assert.assertTrue("Content is null.", ((unmarshalledObject != null) && ((unmarshalledObject.toString()) != null)));
        String content = unmarshalledObject.toString();
        boolean match = false;
        if (((((("{body=" + (HumanTaskServicesBaseTest.myBody)) + ", subject=") + (HumanTaskServicesBaseTest.mySubject)) + "}").equals(content)) || ((((("{subject=" + (HumanTaskServicesBaseTest.mySubject)) + ", body=") + (HumanTaskServicesBaseTest.myBody)) + "}").equals(content))) {
            match = true;
        } 
        Assert.assertTrue("Content does not match.", match);
    }

    protected void printTestName() {
        HumanTaskServicesBaseTest.logger.info("Running {}.{} ", HumanTaskServicesBaseTest.this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    /**
     * Creates date using default format - "yyyy-MM-dd"
     */
    protected Date createDate(String dateString) {
        return createDate(dateString, "yyyy-MM-dd");
    }

    protected Date createDate(String dateString, String dateFormat) {
        SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
        try {
            return fmt.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException((((("Can't create date from string '" + dateString) + "' using '") + dateFormat) + "' format!"), e);
        }
    }

    protected JaxbContent xmlRoundTripContent(Content content) {
        JaxbContent xmlContent = new JaxbContent(content);
        JaxbContent xmlCopy = null;
        try {
            Marshaller marshaller = JAXBContext.newInstance(JaxbContent.class).createMarshaller();
            // marshal
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(xmlContent, stringWriter);
            // unmarshal
            Unmarshaller unmarshaller = JAXBContext.newInstance(JaxbContent.class).createUnmarshaller();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(stringWriter.toString().getBytes());
            xmlCopy = ((JaxbContent) (unmarshaller.unmarshal(inputStream)));
            for (Field field : JaxbContent.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object orig = field.get(xmlContent);
                Object roundTrip = field.get(xmlCopy);
                if (orig instanceof byte[]) {
                    Assert.assertTrue(Arrays.equals(((byte[]) (orig)), ((byte[]) (roundTrip))));
                } else {
                    Assert.assertEquals(field.getName(), orig, roundTrip);
                }
            }
        } catch (Exception e) {
            HumanTaskServicesBaseTest.logger.error(("Unable to complete round trip: " + (e.getMessage())), e);
            Assert.fail(("Unable to complete round trip: " + (e.getMessage())));
        }
        Object orig = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        Assert.assertNotNull("Round tripped JaxbContent is null!", xmlCopy);
        Object roundTrip = ContentMarshallerHelper.unmarshall(xmlCopy.getContent(), null);
        Assert.assertEquals(orig, roundTrip);
        return xmlCopy;
    }

    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";

    protected static final String MAX_POOL_SIZE = "maxPoolSize";

    protected static final String ALLOW_LOCAL_TXS = "allowLocalTransactions";

    protected static final String DATASOURCE_CLASS_NAME = "className";

    protected static final String DRIVER_CLASS_NAME = "driverClassName";

    protected static final String USER = "user";

    protected static final String PASSWORD = "password";

    protected static final String JDBC_URL = "url";

    protected static PoolingDataSource setupPoolingDataSource() {
        Properties dsProps = HumanTaskServicesBaseTest.getDatasourceProperties();
        PoolingDataSource pds = PersistenceUtil.setupPoolingDataSource(dsProps, "jdbc/jbpm-ds", false);
        pds.init();
        return pds;
    }

    /**
     * This reads in the (maven filtered) datasource properties from the test
     * resource directory.
     * 
     * @return Properties containing the datasource properties.
     */
    private static Properties getDatasourceProperties() {
        boolean propertiesNotFound = false;
        // Central place to set additional H2 properties
        System.setProperty("h2.lobInDatabase", "true");
        InputStream propsInputStream = HumanTaskServicesBaseTest.class.getResourceAsStream(HumanTaskServicesBaseTest.DATASOURCE_PROPERTIES);
        Properties props = new Properties();
        if (propsInputStream != null) {
            try {
                props.load(propsInputStream);
            } catch (IOException ioe) {
                propertiesNotFound = true;
                HumanTaskServicesBaseTest.logger.warn(("Unable to find properties, using default H2 properties: " + (ioe.getMessage())));
                ioe.printStackTrace();
            }
        } else {
            propertiesNotFound = true;
        }
        String password = props.getProperty("password");
        if (("${maven.jdbc.password}".equals(password)) || propertiesNotFound) {
            HumanTaskServicesBaseTest.logger.warn((("Unable to load datasource properties [" + (HumanTaskServicesBaseTest.DATASOURCE_PROPERTIES)) + "]"));
            // If maven filtering somehow doesn't work the way it should..
            HumanTaskServicesBaseTest.setDefaultProperties(props);
        } 
        return props;
    }

    /**
     * Return the default database/datasource properties - These properties use
     * an in-memory H2 database
     * 
     * This is used when the developer is somehow running the tests but
     * bypassing the maven filtering that's been turned on in the pom.
     * 
     * @return Properties containing the default properties
     */
    private static void setDefaultProperties(Properties props) {
        String[] keyArr = new String[]{ "serverName" , "portNumber" , "databaseName" , HumanTaskServicesBaseTest.JDBC_URL , HumanTaskServicesBaseTest.USER , HumanTaskServicesBaseTest.PASSWORD , HumanTaskServicesBaseTest.DRIVER_CLASS_NAME , HumanTaskServicesBaseTest.DATASOURCE_CLASS_NAME , HumanTaskServicesBaseTest.MAX_POOL_SIZE , HumanTaskServicesBaseTest.ALLOW_LOCAL_TXS };
        String[] defaultPropArr = new String[]{ "" , "" , "" , "jdbc:h2:mem:jbpm-db;MVCC=true" , "sa" , "" , "org.h2.Driver" , "bitronix.tm.resource.jdbc.lrc.LrcXADataSource" , "5" , "true" };
        Assert.assertTrue("Unequal number of keys for default properties", ((keyArr.length) == (defaultPropArr.length)));
        for (int i = 0; i < (keyArr.length); ++i) {
            if (!(props.containsKey(keyArr[i]))) {
                props.put(keyArr[i], defaultPropArr[i]);
            } 
        }
    }

    protected void addCountDownListner(CountDownTaskEventListener countDownListener) {
        if ((taskService) instanceof EventService) {
            ((EventService<TaskLifeCycleEventListener>) (taskService)).registerTaskEventListener(countDownListener);
        } 
    }
}

