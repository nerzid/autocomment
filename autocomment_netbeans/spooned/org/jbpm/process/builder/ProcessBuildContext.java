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


package org.jbpm.process.builder;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.lang.descr.ProcessDescr;

public class ProcessBuildContext extends PackageBuildContext {
    private Process process;

    private ProcessDescr processDescr;

    private DialectCompiletimeRegistry dialectRegistry;

    public ProcessBuildContext(final KnowledgeBuilderImpl pkgBuilder, final InternalKnowledgePackage pkg, final Process process, final BaseDescr processDescr, final DialectCompiletimeRegistry dialectRegistry, final Dialect defaultDialect) {
        ProcessBuildContext.this.process = process;
        ProcessBuildContext.this.processDescr = ((ProcessDescr) (processDescr));
        ProcessBuildContext.this.dialectRegistry = dialectRegistry;
        init(pkgBuilder, pkg, processDescr, dialectRegistry, defaultDialect, null);
    }

    public ProcessDescr getProcessDescr() {
        return processDescr;
    }

    public void setProcessDescr(ProcessDescr processDescr) {
        ProcessBuildContext.this.processDescr = processDescr;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        ProcessBuildContext.this.process = process;
    }

    public DialectCompiletimeRegistry getDialectRegistry() {
        return dialectRegistry;
    }
}

