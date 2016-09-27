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


package org.jbpm.process.audit.command;

import org.kie.internal.command.Context;
import java.util.List;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.jbpm.query.jpa.data.QueryWhere;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class AuditProcessInstanceLogQueryCommand extends AuditCommand<List<ProcessInstanceLog>> {
    /**
     * * generated serial version UID
     */
    private static final long serialVersionUID = 7543632015198138915L;

    @XmlElement
    private QueryWhere queryWhere;

    public AuditProcessInstanceLogQueryCommand() {
        // JAXB constructor
    }

    public AuditProcessInstanceLogQueryCommand(QueryWhere queryWhere) {
        AuditProcessInstanceLogQueryCommand.this.queryWhere = queryWhere;
    }

    @Override
    public List<ProcessInstanceLog> execute(Context context) {
        setLogEnvironment(context);
        return auditLogService.queryLogs(queryWhere, ProcessInstanceLog.class, ProcessInstanceLog.class);
    }
}

