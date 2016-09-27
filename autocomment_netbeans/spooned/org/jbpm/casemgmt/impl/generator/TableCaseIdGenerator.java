/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.casemgmt.impl.generator;

import org.jbpm.casemgmt.api.generator.CaseIdGenerator;
import org.jbpm.casemgmt.api.generator.CasePrefixNotFoundException;
import org.kie.internal.command.Context;
import org.drools.core.command.impl.GenericCommand;
import java.util.HashMap;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import javax.persistence.NoResultException;
import org.jbpm.shared.services.impl.TransactionalCommandService;

/**
 * Data base tabled backed case id generator. The underlying table keeps single entry per case prefix and updates it
 * (by incrementing current value) on each call to generate method.
 * 
 * Generation is done with pessimistic locking to secure correctness and since it's the only operation in transaction it should not
 * cause any performance issues.
 */
public class TableCaseIdGenerator implements CaseIdGenerator {
    private static final Logger logger = LoggerFactory.getLogger(TableCaseIdGenerator.class);

    private boolean removeOnUnregister = Boolean.parseBoolean(System.getProperty("org.jbpm.casemgmt.table.generator.clean", "false"));

    private TransactionalCommandService commandService;

    public TableCaseIdGenerator(TransactionalCommandService commandService) {
        TableCaseIdGenerator.this.commandService = commandService;
    }

    @Override
    public void register(String prefix) {
        CaseIdInfo caseIdInfo = findCaseIdInfoByPrefix(prefix);
        if (caseIdInfo == null) {
            TableCaseIdGenerator.logger.debug("Case id prefix {} not yet registered", prefix);
            caseIdInfo = new CaseIdInfo();
            caseIdInfo.setCaseIdPrefix(prefix);
            caseIdInfo.setCurrentValue(new Long(0));
            commandService.execute(new org.jbpm.shared.services.impl.commands.PersistObjectCommand(caseIdInfo));
        } else {
            TableCaseIdGenerator.logger.debug("Case id prefix {} already registered and it's current value is {}", prefix, caseIdInfo.getCurrentValue());
        }
    }

    @Override
    public void unregister(String prefix) {
        if (removeOnUnregister) {
            CaseIdInfo caseIdInfo = findCaseIdInfoByPrefix(prefix);
            if (caseIdInfo != null) {
                commandService.execute(new org.jbpm.shared.services.impl.commands.RemoveObjectCommand(caseIdInfo));
                TableCaseIdGenerator.logger.debug("Removed permanently case id info for prefix {}", prefix);
            } 
        } else {
            TableCaseIdGenerator.logger.debug("Skipping remove of case id info for prefix {}", prefix);
        }
    }

    @Override
    public String generate(String prefix, Map<String, Object> optionalParameters) throws CasePrefixNotFoundException {
        CaseIdInfo caseIdInfo = commandService.execute(new TableCaseIdGenerator.IncrementAndGetCaseIdCommand(prefix));
        TableCaseIdGenerator.logger.debug("Next sequence value for case id prefix {} is {}", prefix, caseIdInfo.getCurrentValue());
        long nextVal = caseIdInfo.getCurrentValue();
        String paddedNumber = String.format("%010d", nextVal);
        return (prefix + "-") + paddedNumber;
    }

    protected CaseIdInfo findCaseIdInfoByPrefix(String prefix) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("prefix", prefix);
        params.put("firstResult", 0);
        params.put("maxResults", 1);
        List<CaseIdInfo> caseIdInfos = commandService.execute(new org.jbpm.shared.services.impl.commands.QueryNameCommand<List<CaseIdInfo>>("findCaseIdInfoByPrefix", params));
        if (caseIdInfos.isEmpty()) {
            return null;
        } 
        return caseIdInfos.get(0);
    }

    private class IncrementAndGetCaseIdCommand implements GenericCommand<CaseIdInfo> {
        private static final long serialVersionUID = 8670412133363766162L;

        private String prefix;

        public IncrementAndGetCaseIdCommand(String prefix) {
            TableCaseIdGenerator.IncrementAndGetCaseIdCommand.this.prefix = prefix;
        }

        @Override
        public CaseIdInfo execute(Context context) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("prefix", prefix);
            params.put("firstResult", 0);
            params.put("maxResults", 1);
            CaseIdInfo caseIdInfo = null;
            try {
                JpaPersistenceContext ctx = ((JpaPersistenceContext) (context));
                caseIdInfo = ctx.queryAndLockWithParametersInTransaction("findCaseIdInfoByPrefix", params, true, CaseIdInfo.class);
                if (caseIdInfo != null) {
                    caseIdInfo.setCurrentValue(((caseIdInfo.getCurrentValue()) + 1));
                    ctx.merge(caseIdInfo);
                } 
            } catch (NoResultException e) {
            }
            return caseIdInfo;
        }
    }
}

