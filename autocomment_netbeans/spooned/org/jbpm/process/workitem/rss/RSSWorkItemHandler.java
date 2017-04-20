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

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import java.util.ArrayList;
import java.util.List;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import java.net.URL;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * @author salaboy
 */
public class RSSWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
    List<SyndFeed> feeds = new ArrayList<SyndFeed>();

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            List<String> urls = new ArrayList<String>();
            String urlsList = ((String) (workItem.getParameter("URL")));
            for (String s : urlsList.split(";")) {
                if ((s != null) && (!("".equals(s)))) {
                    urls.add(s);
                }
            }
            for (String url : urls) {
                URL feedSource = new URL(url);
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(feedSource));
                feeds.add(feed);
            }
            manager.completeWorkItem(workItem.getId(), null);
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    public List<SyndFeed> getFeeds() {
        return this.feeds;
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

