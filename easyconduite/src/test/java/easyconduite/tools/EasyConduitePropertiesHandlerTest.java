package easyconduite.tools;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class EasyConduitePropertiesHandlerTest {

    EasyConduitePropertiesHandler handler;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getInstanceTest() throws IOException, ClassNotFoundException {
        handler = EasyConduitePropertiesHandler.getInstance();
        assertNotNull(handler);
        handler.getProperties().setWindowWith(200);
    }
}