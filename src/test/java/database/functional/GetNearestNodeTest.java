package database.functional;

import static org.assertj.swing.timing.Pause.pause;
import static org.junit.Assert.assertNotNull;

import java.awt.Toolkit;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.DimensionUIResource;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.group8.phase1.database.functional.GetNearestNode;
import com.group8.phase1.views.MapView;

public class GetNearestNodeTest {

    @Before
    public void setUp() {
        deleteDB();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        MapView frame = GuiActionRunner.execute(MapView::new);
        FrameFixture window = new FrameFixture(frame);
        window.show(new DimensionUIResource((int)toolkit.getScreenSize().getWidth(), (int)toolkit.getScreenSize().getHeight()));
        JPanelFixture loaderPanel = window.panel("loaderPanel");
        pause(50000); // Give the app enough time to populate OSM database
        loaderPanel.requireNotVisible();
        window.cleanUp();
    }

    private void deleteDB(){
        File file = new File("./Database/graph_data.db");
        if(file.exists()){
            file.delete();
        }
    }

    @Test
    public void testGetNearestNode() {

        // PostalCodes List {6229XM, 6223AE, 6215JP}
        Map<String, double[]> targetNodes = new HashMap<>();

        double[] firstTarget ={50.82388577, 5.706758262};
        targetNodes.put("6229XM",firstTarget);
        double[] secondTarget ={50.87468334, 5.687856429};
        targetNodes.put("6223AE",secondTarget);
        double[] thirdTarget ={50.84645969, 5.642129667};
        targetNodes.put("6215JP",thirdTarget);

        for(String entryName : targetNodes.keySet()) {
            double[] values = targetNodes.get(entryName);
            Long nodeId = GetNearestNode.get(values[0], values[1]);

            assertNotNull("Node id should not be null for postal code: " + entryName, nodeId);
        }

    }

    @After
    public void tearDown() {
        deleteDB();
    }
}
