

package org.jbpm.persistence.scripts;

import org.junit.Assert;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.Test;
import org.jbpm.persistence.scripts.util.TestsUtil;

/**
 * Contains tests that test DDL scripts.
 */
public class DDLScriptsTest {
    private static final String DB_DDL_SCRIPTS_RESOURCE_PATH = "/db/ddl-scripts";

    /**
     * Tests that DB schema is created properly using DDL scripts.
     * @throws IOException
     */
    @Test
    public void testCreateSchema() throws IOException, SQLException {
        // Clear schema.
        TestsUtil.clearSchema();
        final TestPersistenceContext scriptRunnerContext = new TestPersistenceContext();
        scriptRunnerContext.init(PersistenceUnit.SCRIPT_RUNNER);
        try {
            scriptRunnerContext.executeScripts(new File(getClass().getResource(DDLScriptsTest.DB_DDL_SCRIPTS_RESOURCE_PATH).getFile()));
        } finally {
            scriptRunnerContext.clean();
        }
        final TestPersistenceContext dbTestingContext = new TestPersistenceContext();
        dbTestingContext.init(PersistenceUnit.DB_TESTING);
        try {
            dbTestingContext.startAndPersistSomeProcess("minimalProcess");
            Assert.assertTrue(((dbTestingContext.getStoredProcessesCount()) == 1));
        } finally {
            dbTestingContext.clean();
        }
    }
}

