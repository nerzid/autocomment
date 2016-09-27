/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.runtime.manager.impl.migration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import MigrationEntry.Type;

/**
 * Represents complete (might be unfinished in case of an error)
 * process instance migration. It contains all migration entries
 * that correspond to individual operations performed during migration.
 */
public class MigrationReport implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(MigrationReport.class);

    private static final long serialVersionUID = -5992169359542031146L;

    private MigrationSpec migrationSpec;

    private boolean successful;

    private Date startDate;

    private Date endDate;

    private List<MigrationEntry> entries = new ArrayList<MigrationEntry>();

    public MigrationReport(MigrationSpec migrationSpec) {
        MigrationReport.this.migrationSpec = migrationSpec;
        MigrationReport.this.startDate = new Date();
    }

    public MigrationSpec getMigrationSpec() {
        return migrationSpec;
    }

    public void setMigrationSpec(MigrationSpec processData) {
        MigrationReport.this.migrationSpec = processData;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        MigrationReport.this.successful = successful;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        MigrationReport.this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        MigrationReport.this.endDate = endDate;
    }

    public List<MigrationEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<MigrationEntry> entries) {
        MigrationReport.this.entries = entries;
    }

    public void addEntry(Type type, String message) throws MigrationException {
        MigrationReport.this.entries.add(new MigrationEntry(type, message));
        switch (type) {
            case INFO :
                MigrationReport.logger.debug(message);
                break;
            case WARN :
                MigrationReport.logger.warn(message);
                break;
            case ERROR :
                MigrationReport.logger.error(message);
                MigrationReport.this.setSuccessful(false);
                setEndDate(new Date());
                throw new MigrationException(message, MigrationReport.this);
            default :
                break;
        }
    }
}

