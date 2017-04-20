/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.kie.services.impl.store;

import DeploymentSynchronizer.DEPLOY_SYNC_INTERVAL;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DeploymentSyncInvoker {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentSyncInvoker.class);

    private ScheduledExecutorService executor;

    private Future<?> future;

    private final DeploymentSynchronizer synchronizer;

    private Long delay = 2L;

    private Long period = Long.parseLong(DEPLOY_SYNC_INTERVAL);

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    public DeploymentSyncInvoker(DeploymentSynchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    public DeploymentSyncInvoker(DeploymentSynchronizer synchronizer, Long delay, Long period, TimeUnit timeUnit) {
        this.synchronizer = synchronizer;
        this.delay = delay;
        this.period = period;
        this.timeUnit = timeUnit;
    }

    public void start() {
        // info String{"Starting deployment synchronization (delay {}, period {}, timeunit {})"} to Logger{DeploymentSyncInvoker.logger}
        DeploymentSyncInvoker.logger.info("Starting deployment synchronization (delay {}, period {}, timeunit {})", delay, period, timeUnit);
        executor = Executors.newScheduledThreadPool(1);
        future = executor.scheduleAtFixedRate(new DeploymentSyncInvoker.TriggerDeploymentSync(), delay, period, timeUnit);
        // info String{"Deployment synchronization started at {}"} to Logger{DeploymentSyncInvoker.logger}
        DeploymentSyncInvoker.logger.info("Deployment synchronization started at {}", new Date());
    }

    public void stop() {
        // info String{"Shutting down deployment synchronization"} to Logger{DeploymentSyncInvoker.logger}
        DeploymentSyncInvoker.logger.info("Shutting down deployment synchronization");
        if ((this.executor) != null) {
            if ((future) != null) {
                this.future.cancel(false);
            }
            this.executor.shutdown();
            try {
                if (!(this.executor.awaitTermination(10, TimeUnit.SECONDS))) {
                    this.executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                DeploymentSyncInvoker.logger.debug("Interrupted exception while waiting for executor shutdown");
            }
        }
        // info String{"Deployment synchronization stopped"} to Logger{DeploymentSyncInvoker.logger}
        DeploymentSyncInvoker.logger.info("Deployment synchronization stopped");
    }

    private class TriggerDeploymentSync implements Runnable {
        @Override
        public void run() {
            try {
                synchronizer.synchronize();
            } catch (Throwable e) {
                DeploymentSyncInvoker.logger.warn("Exception while triggering deployments synchronization", e);
            }
        }
    }
}

