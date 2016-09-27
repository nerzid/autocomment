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


package org.jbpm.test.functional.timer;

import org.jbpm.test.AbstractBaseTest;
import org.junit.AfterClass;
import org.kie.api.event.rule.AgendaEventListener;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.BeforeClass;
import java.sql.Connection;
import javax.sql.DataSource;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import java.io.IOException;
import javax.naming.InitialContext;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.persistence.util.PersistenceUtil;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.event.process.ProcessEventListener;
import java.util.Properties;
import bitronix.tm.resource.ResourceRegistrar;
import org.kie.api.runtime.manager.RuntimeEngine;
import java.util.Scanner;
import java.util.Set;
import java.sql.Statement;
import org.kie.api.task.TaskLifeCycleEventListener;
import bitronix.tm.resource.common.XAResourceProducer;

public abstract class TimerBaseTest extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(TimerBaseTest.class);

    private static PoolingDataSource pds;

    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";

    protected static final String MAX_POOL_SIZE = "maxPoolSize";

    protected static final String ALLOW_LOCAL_TXS = "allowLocalTransactions";

    protected static final String DATASOURCE_CLASS_NAME = "className";

    protected static final String DRIVER_CLASS_NAME = "driverClassName";

    protected static final String USER = "user";

    protected static final String PASSWORD = "password";

    protected static final String JDBC_URL = "url";

    public static PoolingDataSource setupPoolingDataSource() {
        Properties dsProps = TimerBaseTest.getDatasourceProperties();
        PoolingDataSource pds = PersistenceUtil.setupPoolingDataSource(dsProps, "jdbc/jbpm-ds", false);
        try {
            pds.init();
        } catch (Exception e) {
            TimerBaseTest.logger.warn((("DBPOOL_MGR:Looks like there is an issue with creating db pool because of " + (e.getMessage())) + " cleaing up..."));
            Set<String> resources = ResourceRegistrar.getResourcesUniqueNames();
            for (String resource : resources) {
                XAResourceProducer producer = ResourceRegistrar.get(resource);
                producer.close();
                ResourceRegistrar.unregister(producer);
                TimerBaseTest.logger.info(("DBPOOL_MGR:Removed resource " + resource));
            }
            TimerBaseTest.logger.info("DBPOOL_MGR: attempting to create db pool again...");
            pds = PersistenceUtil.setupPoolingDataSource(dsProps, "jdbc/jbpm-ds", false);
            pds.init();
            TimerBaseTest.logger.info("DBPOOL_MGR:Pool created after cleanup of leftover resources");
        }
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
        InputStream propsInputStream = TimerBaseTest.class.getResourceAsStream(TimerBaseTest.DATASOURCE_PROPERTIES);
        Properties props = new Properties();
        if (propsInputStream != null) {
            try {
                props.load(propsInputStream);
            } catch (IOException ioe) {
                propertiesNotFound = true;
                TimerBaseTest.logger.warn(("Unable to find properties, using default H2 properties: " + (ioe.getMessage())));
                ioe.printStackTrace();
            }
        } else {
            propertiesNotFound = true;
        }
        String password = props.getProperty("password");
        if (("${maven.jdbc.password}".equals(password)) || propertiesNotFound) {
            TimerBaseTest.logger.warn((("Unable to load datasource properties [" + (TimerBaseTest.DATASOURCE_PROPERTIES)) + "]"));
        } 
        // If maven filtering somehow doesn't work the way it should..
        TimerBaseTest.setDefaultProperties(props);
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
        String[] keyArr = new String[]{ "serverName" , "portNumber" , "databaseName" , TimerBaseTest.JDBC_URL , TimerBaseTest.USER , TimerBaseTest.PASSWORD , TimerBaseTest.DRIVER_CLASS_NAME , TimerBaseTest.DATASOURCE_CLASS_NAME , TimerBaseTest.MAX_POOL_SIZE , TimerBaseTest.ALLOW_LOCAL_TXS };
        String[] defaultPropArr = new String[]{ "" , "" , "" , "jdbc:h2:mem:jbpm-db;MVCC=true" , "sa" , "" , "org.h2.Driver" , "bitronix.tm.resource.jdbc.lrc.LrcXADataSource" , "5" , "true" };
        Assert.assertTrue("Unequal number of keys for default properties", ((keyArr.length) == (defaultPropArr.length)));
        for (int i = 0; i < (keyArr.length); ++i) {
            if (!(props.containsKey(keyArr[i]))) {
                props.put(keyArr[i], defaultPropArr[i]);
            } 
        }
    }

    @BeforeClass
    public static void setUpOnce() {
        if ((TimerBaseTest.pds) == null) {
            TimerBaseTest.pds = TimerBaseTest.setupPoolingDataSource();
        } 
    }

    @AfterClass
    public static void tearDownOnce() {
        if ((TimerBaseTest.pds) != null) {
            TimerBaseTest.pds.close();
            TimerBaseTest.pds = null;
        } 
    }

    protected void testCreateQuartzSchema() {
        Scanner scanner = new Scanner(TimerBaseTest.this.getClass().getResourceAsStream("/quartz_tables_h2.sql")).useDelimiter(";");
        try {
            Connection connection = ((DataSource) (InitialContext.doLookup("jdbc/jbpm-ds"))).getConnection();
            Statement stmt = connection.createStatement();
            while (scanner.hasNext()) {
                String sql = scanner.next();
                stmt.executeUpdate(sql);
            }
            stmt.close();
            connection.close();
        } catch (Exception e) {
        }
    }

    protected class TestRegisterableItemsFactory extends DefaultRegisterableItemsFactory {
        private ProcessEventListener[] plistener;

        private AgendaEventListener[] alistener;

        private TaskLifeCycleEventListener[] tlistener;

        public TestRegisterableItemsFactory(ProcessEventListener... listener) {
            TimerBaseTest.TestRegisterableItemsFactory.this.plistener = listener;
        }

        public TestRegisterableItemsFactory(AgendaEventListener... listener) {
            TimerBaseTest.TestRegisterableItemsFactory.this.alistener = listener;
        }

        public TestRegisterableItemsFactory(TaskLifeCycleEventListener... tlistener) {
            TimerBaseTest.TestRegisterableItemsFactory.this.tlistener = tlistener;
        }

        @Override
        public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
            List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
            if ((plistener) != null) {
                listeners.addAll(Arrays.asList(plistener));
            } 
            return listeners;
        }

        @Override
        public List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime) {
            List<AgendaEventListener> listeners = super.getAgendaEventListeners(runtime);
            if ((alistener) != null) {
                listeners.addAll(Arrays.asList(alistener));
            } 
            return listeners;
        }

        @Override
        public List<TaskLifeCycleEventListener> getTaskListeners() {
            List<TaskLifeCycleEventListener> listeners = super.getTaskListeners();
            if ((tlistener) != null) {
                listeners.addAll(Arrays.asList(tlistener));
            } 
            return listeners;
        }
    }
}

