package org.delcom.starter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationUnitTest {

    @Test
    void applicationCanBeInstantiatedForCoverage() {
        // Menutupi coverage kelas Application
        Application application = new Application();
        assertNotNull(application);
    }
}