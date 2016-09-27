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


package org.jbpm.process.workitem.rest;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Assert;
import java.util.HashMap;
import java.net.HttpURLConnection;
import org.junit.Ignore;
import java.util.Map;
import org.junit.Test;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.drools.core.process.instance.WorkItemManager;

public class RestGeoCodeApiCallWorkItemHandlerTest extends AbstractBaseTest {
    @Test
    @Ignore
    public void FIXMEtestYahooGeoCode() throws Exception {
        RestGeoCodeApiCallWorkItemHandler handler = new RestGeoCodeApiCallWorkItemHandler();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("URL", "http://local.yahooapis.com/");
        queryParams.put("Service", "MapsService/V1/");
        queryParams.put("Method", "geocode?");
        queryParams.put("appid", "TIpNDenV34Fwcw_x32k1eX6AlQzq4wajFEFvG501Pwc6w9jKEfy2vGnkIn.r5qSQqVvyhPPaTFo-");
        // Real parameters
        queryParams.put("street", "701+First+Ave");
        queryParams.put("city", "Sunnyvale");
        queryParams.put("state", "CA");
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameters(queryParams);
        WorkItemManager manager = new org.drools.core.process.instance.impl.DefaultWorkItemManager(null);
        handler.executeWorkItem(workItem, manager);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, handler.getHttpResponseCode());
        Assert.assertEquals(1, handler.getResults().size());
        Assert.assertEquals("US", ((ResultGeoCodeApi) (handler.getResults().get(0))).getCountry());
    }
}

