

package org.jbpm.executor.test;

import org.jbpm.executor.AsynchronousJobEvent;
import org.jbpm.executor.AsynchronousJobListener;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

public class CountDownAsyncJobListener implements AsynchronousJobListener {
    private static final Logger logger = LoggerFactory.getLogger(CountDownAsyncJobListener.class);

    private CountDownLatch latch;

    public CountDownAsyncJobListener(int threads) {
        CountDownAsyncJobListener.this.latch = new CountDownLatch(threads);
    }

    public void waitTillCompleted() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            CountDownAsyncJobListener.logger.debug("Interrputed thread while waiting for all async jobs");
        }
    }

    public void waitTillCompleted(long timeOut) {
        try {
            latch.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            CountDownAsyncJobListener.logger.debug("Interrputed thread while waiting for all async jobs");
        }
    }

    public void reset(int threads) {
        CountDownAsyncJobListener.this.latch = new CountDownLatch(threads);
    }

    @Override
    public void beforeJobScheduled(AsynchronousJobEvent event) {
    }

    @Override
    public void afterJobScheduled(AsynchronousJobEvent event) {
    }

    @Override
    public void beforeJobExecuted(AsynchronousJobEvent event) {
    }

    @Override
    public void afterJobExecuted(AsynchronousJobEvent event) {
        latch.countDown();
    }

    @Override
    public void beforeJobCancelled(AsynchronousJobEvent event) {
    }

    @Override
    public void afterJobCancelled(AsynchronousJobEvent event) {
        latch.countDown();
    }
}

