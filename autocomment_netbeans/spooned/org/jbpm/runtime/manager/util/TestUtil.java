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


package org.jbpm.runtime.manager.util;

import org.junit.Assert;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.persistence.util.PersistenceUtil;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.util.Properties;

public class TestUtil {
    private static final Logger logger = LoggerFactory.getLogger(TestUtil.class);

    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";

    protected static final String MAX_POOL_SIZE = "maxPoolSize";

    protected static final String ALLOW_LOCAL_TXS = "allowLocalTransactions";

    protected static final String DATASOURCE_CLASS_NAME = "className";

    protected static final String DRIVER_CLASS_NAME = "driverClassName";

    protected static final String USER = "user";

    protected static final String PASSWORD = "password";

    protected static final String JDBC_URL = "url";

    public static PoolingDataSource setupPoolingDataSource() {
        Properties dsProps = TestUtil.getDatasourceProperties();
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
        InputStream propsInputStream = TestUtil.class.getResourceAsStream(TestUtil.DATASOURCE_PROPERTIES);
        Properties props = new Properties();
        if (propsInputStream != null) {
            try {
                props.load(propsInputStream);
            } catch (IOException ioe) {
                propertiesNotFound = true;
                TestUtil.logger.warn(("Unable to find properties, using default H2 properties: " + (ioe.getMessage())));
                ioe.printStackTrace();
            }
        } else {
            propertiesNotFound = true;
        }
        String password = props.getProperty("password");
        if (("${maven.jdbc.password}".equals(password)) || propertiesNotFound) {
            TestUtil.logger.warn((("Unable to load datasource properties [" + (TestUtil.DATASOURCE_PROPERTIES)) + "]"));
        } 
        // If maven filtering somehow doesn't work the way it should..
        TestUtil.setDefaultProperties(props);
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
        String[] keyArr = new String[]{ "serverName" , "portNumber" , "databaseName" , TestUtil.JDBC_URL , TestUtil.USER , TestUtil.PASSWORD , TestUtil.DRIVER_CLASS_NAME , TestUtil.DATASOURCE_CLASS_NAME , TestUtil.MAX_POOL_SIZE , TestUtil.ALLOW_LOCAL_TXS };
        String[] defaultPropArr = new String[]{ "" , "" , "" , "jdbc:h2:mem:jbpm-db;MVCC=true" , "sa" , "" , "org.h2.Driver" , "bitronix.tm.resource.jdbc.lrc.LrcXADataSource" , "5" , "true" };
        Assert.assertTrue("Unequal number of keys for default properties", ((keyArr.length) == (defaultPropArr.length)));
        for (int i = 0; i < (keyArr.length); ++i) {
            if (!(props.containsKey(keyArr[i]))) {
                props.put(keyArr[i], defaultPropArr[i]);
            } 
        }
    }

    public static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {
            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                TestUtil.logger.debug("Temp dir to be removed {} file {}", tempDir, file);
                new File(tempDir, file).delete();
            }
        } 
    }

    public static void main(String[] args) {
        TestUtil.cleanupSingletonSessionId();
    }

    public static void checkDisposedSessionException(Throwable e) {
        Throwable rootCause = e.getCause();
        while (rootCause != null) {
            if ((rootCause.getCause()) != null) {
                rootCause = rootCause.getCause();
            } else {
                break;
            }
        }
        if (!(rootCause instanceof IllegalStateException)) {
            Assert.fail(("Unexpected exception caught " + (rootCause.getMessage())));
        } 
    }
}

