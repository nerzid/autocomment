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


package org.jbpm.process.workitem.rss;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.kie.api.runtime.process.WorkItemManager;

// @author: salaboy
public class RSSWorkItemHandlerTest extends AbstractBaseTest {
    @Test
    @Ignore
    public void FIXMEtestReadRSSFeed() throws Exception {
        RSSWorkItemHandler handler = new RSSWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        // set parameter String{"URL"} to WorkItemImpl{workItem}
        workItem.setParameter("URL", "http://salaboy.wordpress.com/feed/;http://salaboy.wordpress.com/feed/");
        WorkItemManager manager = new DefaultWorkItemManager(null);
        // execute work WorkItemImpl{workItem} to RSSWorkItemHandler{handler}
        handler.executeWorkItem(workItem, manager);
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, handler.getFeeds().size());
        // En el caso real deberia registrar el workitem handler en el workitemmanager
        // workingMemory.getWorkItemManager()
        // .registerWorkItemHandler("Notification", new NotificationWorkItemHandler());
    }
}

