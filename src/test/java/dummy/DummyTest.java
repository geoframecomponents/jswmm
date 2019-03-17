package dummy;

import com.github.geoframecomponents.jswmm.dataStructure.SWMMobject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DummyTest {

    SWMMobject data;

    @Before
    public void initialize() {
        data = new SWMMobject();
    }

    @Test
    public void execute() {
        assertNotNull(data);
    }
}