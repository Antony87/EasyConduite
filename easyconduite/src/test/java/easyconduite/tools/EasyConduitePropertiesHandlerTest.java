package easyconduite.tools;

import easyconduite.exception.EasyconduiteException;
import easyconduite.util.EasyConduitePropertiesHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void getInstanceTest() throws EasyconduiteException {
        handler = EasyConduitePropertiesHandler.getInstance();
        assertNotNull(handler);
        handler.getApplicationProperties().setWindowWith(200);
        System.out.println(handler.getApplicationProperties().getWindowWith());
    }
}