package areda;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainAppTest {
    @Test
    void appInstanceTest() {
        MainApp app = new MainApp();
        assertNotNull(app, "MainApp instance should be created");
    }
}
