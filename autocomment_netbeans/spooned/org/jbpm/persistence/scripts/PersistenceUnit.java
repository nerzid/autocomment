

package org.jbpm.persistence.scripts;


/**
 * Persistence units that are supported and used in tests.
 */
public enum PersistenceUnit {
SCRIPT_RUNNER("scriptRunner","jdbc/testDS1"), DB_TESTING("dbTesting","jdbc/testDS2"), CLEAR_SCHEMA("clearSchema","jdbc/testDS3");
    /**
     * Name of persistence unit. Must correspond to persistence unit names in persistence.xml.
     */
    private final String name;

    /**
     * Name of data source bound to persistence unit. Must correspond to data source name in persistence.xml.
     */
    private final String dataSourceName;

    PersistenceUnit(final String name, final String dataSourceName) {
        this.name = name;
        this.dataSourceName = dataSourceName;
    }

    public String getName() {
        return name;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }
}

