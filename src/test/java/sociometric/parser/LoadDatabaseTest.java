package sociometric.parser;

import static org.assertj.swing.timing.Pause.pause;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;

import javax.swing.plaf.DimensionUIResource;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.group8.phase1.initialdataloader.ProgressPanel;
import com.group8.phase1.osm.checker.CheckDatabase;
import com.group8.phase1.sociometric.calculation.AmenityCalculator;
import com.group8.phase1.structures.map.Node;
import com.group8.phase1.views.MapView;
import java.awt.Toolkit;

public class LoadDatabaseTest {
    

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
        File file = new File("./Database/JSON.db");
        if(file.exists()){
            file.delete();
        }
    }

    @Test
    public void testLoadDatabase(){
        CheckDatabase.check(new ProgressPanel());

        AmenityCalculator calculator = new AmenityCalculator(new Node("6212GM", 50.8396439499999, 5.68409535));
        double amenities = calculator.getAmenityScore();

        assertNotEquals(null, amenities, "Amenity score for postal code 6211GM should not be null");

    }

    @After
    public void tearDown() {
        deleteDB();
    }
}
