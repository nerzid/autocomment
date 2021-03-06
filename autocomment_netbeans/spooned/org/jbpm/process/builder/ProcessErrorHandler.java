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
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.builder.impl.errors.ErrorHandler;

public class ProcessErrorHandler extends ErrorHandler {
    private BaseDescr descr;

    private Process process;

    public ProcessErrorHandler(final BaseDescr ruleDescr, final Process process, final String message) {
        ProcessErrorHandler.this.descr = ruleDescr;
        ProcessErrorHandler.this.process = process;
        this.message = message;
    }

    public DroolsError getError() {
        return new org.jbpm.compiler.ProcessBuildError(ProcessErrorHandler.this.process, ProcessErrorHandler.this.descr, collectCompilerProblems(), ProcessErrorHandler.this.message);
    }
}

