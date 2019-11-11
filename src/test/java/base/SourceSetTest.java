package base;

import com.github.geoframecomponents.jswmm.dataStructure.SWMMobject;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SourceSetTest {

    SWMMobject data;

    public SourceSetTest() throws ConfigurationException {
        this.data = new SWMMobject("network12.inp");
    }

    @Before
    public void initialize() {
        data = new SWMMobject();
    }

    @Test
    public void execute() {
        assertNotNull(data);
    }
}
