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
        // put String{"URL"} to Map{queryParams}
        queryParams.put("URL", "http://local.yahooapis.com/");
        // put String{"Service"} to Map{queryParams}
        queryParams.put("Service", "MapsService/V1/");
        // put String{"Method"} to Map{queryParams}
        queryParams.put("Method", "geocode?");
        // put String{"appid"} to Map{queryParams}
        queryParams.put("appid", "TIpNDenV34Fwcw_x32k1eX6AlQzq4wajFEFvG501Pwc6w9jKEfy2vGnkIn.r5qSQqVvyhPPaTFo-");
        // Real parameters
        // put String{"street"} to Map{queryParams}
        queryParams.put("street", "701+First+Ave");
        // put String{"city"} to Map{queryParams}
        queryParams.put("city", "Sunnyvale");
        // put String{"state"} to Map{queryParams}
        queryParams.put("state", "CA");
        WorkItemImpl workItem = new WorkItemImpl();
        // set parameters Map{queryParams} to WorkItemImpl{workItem}
        workItem.setParameters(queryParams);
        WorkItemManager manager = new DefaultWorkItemManager(null);
        // execute work WorkItemImpl{workItem} to RestGeoCodeApiCallWorkItemHandler{handler}
        handler.executeWorkItem(workItem, manager);
        // assert equals int{HttpURLConnection.HTTP_OK} to void{Assert}
        Assert.assertEquals(HttpURLConnection.HTTP_OK, handler.getHttpResponseCode());
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, handler.getResults().size());
        // assert equals String{"US"} to void{Assert}
        Assert.assertEquals("US", ((ResultGeoCodeApi) (handler.getResults().get(0))).getCountry());
    }
}

