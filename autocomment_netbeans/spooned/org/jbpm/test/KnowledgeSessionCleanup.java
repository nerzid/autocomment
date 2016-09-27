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


package org.jbpm.test;

import org.junit.runners.model.FrameworkMethod;
import java.util.HashSet;
import java.util.Iterator;
import org.junit.rules.MethodRule;
import java.util.Set;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.runners.model.Statement;

public class KnowledgeSessionCleanup implements MethodRule {
    protected static ThreadLocal<Set<StatefulKnowledgeSession>> knowledgeSessionSetLocal = new ThreadLocal<Set<StatefulKnowledgeSession>>();

    static {
        KnowledgeSessionCleanup.knowledgeSessionSetLocal.set(new HashSet<StatefulKnowledgeSession>());
    }

    public static void addKnowledgeSessionForCleanup(StatefulKnowledgeSession ksession) {
        KnowledgeSessionCleanup.knowledgeSessionSetLocal.get().add(ksession);
    }

    public Statement apply(final Statement base, FrameworkMethod method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } finally {
                    Set<StatefulKnowledgeSession> ksessionSet = KnowledgeSessionCleanup.knowledgeSessionSetLocal.get();
                    if (!(ksessionSet.isEmpty())) {
                        // return'ing here will keep throwables (above) from being thrown!
                        Iterator<StatefulKnowledgeSession> iter = ksessionSet.iterator();
                        while (iter.hasNext()) {
                            StatefulKnowledgeSession ksession = iter.next();
                            if (ksession != null) {
                                try {
                                    ksession.dispose();
                                } catch (Throwable t) {
                                    // Don't log anything if dispose() fails
                                    // Maybe the test already disposed of the ksession, who knows -- it doesn't matter
                                }
                            } 
                            iter.remove();
                        }
                    } 
                }
            }
        };
    }
}

