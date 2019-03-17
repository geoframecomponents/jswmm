/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package runoff;

import com.github.geoframecomponents.jswmm.dataStructure.SWMMobject;
import com.github.geoframecomponents.jswmm.runoff.PreRunoff;
import com.github.geoframecomponents.jswmm.runoff.Runoff;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class RunoffMain {

    SWMMobject data;
    PreRunoff preRunoff_1;
    PreRunoff preRunoff_2;
    Runoff runoff_1;
    Runoff runoff_2;

    @Before
    public void initialize() {
        data = new SWMMobject();

        preRunoff_1 = new PreRunoff();
        preRunoff_2 = new PreRunoff();

        preRunoff_1.dataStructure = data;
        preRunoff_2.dataStructure = data;

        preRunoff_1.areaName = "RG1";
        preRunoff_2.areaName = "RG2";

        preRunoff_1.initialize();
        preRunoff_2.initialize();

        preRunoff_1.run();
        preRunoff_2.run();
    }

    @Test
    public void execute() {
        runoff_1 = new Runoff();
        runoff_2 = new Runoff();

        runoff_1.dataStructure = data;
        runoff_1.areaName = "Sub1";
        runoff_1.nodeName = "N1";
        runoff_1.adaptedRainfallData = preRunoff_1.getAdaptedRainfallData();

        runoff_2.dataStructure = data;
        runoff_2.areaName = "Sub2";
        runoff_2.nodeName = "N1";
        runoff_2.adaptedRainfallData = preRunoff_2.getAdaptedRainfallData();

        //runoff_1.run();
        //runoff_2.run();

        //runoff_1.test("./data/testingData/runoffTesting/N1");
        //runoff_2.test("./data/testingData/runoffTesting/N2");

        //System.out.println(data.getAreas().get("Sub1").getTotalAreaFlowRate().get(Instant.parse("2018-01-01T00:02:00Z")));
        //System.out.println(data.getAreas().get("Sub2").getTotalAreaFlowRate().get(Instant.parse("2018-01-01T00:02:00Z")));

        //System.out.println(data.getJunctions().get("N1").getNodeFlowRate().get(Instant.parse("2018-01-01T00:02:00Z")));
    }
}