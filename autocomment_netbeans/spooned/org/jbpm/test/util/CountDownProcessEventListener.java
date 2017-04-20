

package org.jbpm.test.util;

import java.util.concurrent.CountDownLatch;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import java.util.concurrent.TimeUnit;

public class CountDownProcessEventListener extends DefaultProcessEventListener {
    private static final Logger logger = LoggerFactory.getLogger(CountDownProcessEventListener.class);

    private String nodeName;

    private CountDownLatch latch;

    public CountDownProcessEventListener(String nodeName, int threads) {
        this.nodeName = nodeName;
        this.latch = new CountDownLatch(threads);
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        if (nodeName.equals(event.getNodeInstance().getNodeName())) {
            latch.countDown();
        }
    }

    public void waitTillCompleted() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            CountDownProcessEventListener.logger.debug("Interrputed thread while waiting for all triggers for node {}", nodeName);
        }
    }

    public void waitTillCompleted(long timeOut) {
        try {
            latch.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            CountDownProcessEventListener.logger.debug("Interrputed thread while waiting for all triggers for node {}", nodeName);
        }
    }
}

