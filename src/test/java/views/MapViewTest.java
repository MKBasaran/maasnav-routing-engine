package views;

import static org.assertj.swing.timing.Pause.pause;

import java.awt.Toolkit;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.plaf.DimensionUIResource;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.fixture.JToggleButtonFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.group8.phase1.views.MapView;
import java.awt.Toolkit;

public class MapViewTest {

    private FrameFixture window;

    @Before
    public void setUp() {
        deleteDB();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        MapView frame = GuiActionRunner.execute(MapView::new);
        window = new FrameFixture(frame);
        window.show(new DimensionUIResource((int)toolkit.getScreenSize().getWidth(), (int)toolkit.getScreenSize().getHeight()));
        JPanelFixture loaderPanel = window.panel("loaderPanel");
        pause(50000); // Give the app enough time to populate OSM database
        loaderPanel.requireNotVisible();
    }

    @After
    public void tearDown() {
        deleteDB(); // Delete the database otherwise any normal runs of the application will be broken afterwards
        window.cleanUp();
    }


    public void testRoutingScreen(JPanelFixture sidebarPanel){
        JTextComponentFixture startInputField = sidebarPanel.textBox("startInputField");
        JTextComponentFixture toInputField = sidebarPanel.textBox("toInputField");
        JButtonFixture bikeButton = sidebarPanel.button("bikeButton");
        JButtonFixture walkingButton = sidebarPanel.button("walkingButton");
        JButtonFixture busButton = sidebarPanel.button("busButton");

        startInputField.requireText("Enter starting postal code:");
        toInputField.requireText("Enter destination postal code:");


        // Expected interaction example, entering valid postal codes
        
        startInputField.enterText("6229HD");
        toInputField.enterText("6221CS");

        startInputField.requireText("6229HD");
        toInputField.requireText("6221CS");

        pause(1000);

        bikeButton.click();

        pause(1000);

        walkingButton.click();

        pause(1000);

        busButton.click();

        pause(15000);


        // Invalid interaction example, entering non-existant postal codes

        startInputField.deleteText();
        toInputField.deleteText();

        startInputField.enterText("2002020");
        toInputField.enterText("addawda0232");

        startInputField.requireText("2002020");
        toInputField.requireText("addawda0232");

        pause(1000);

        bikeButton.click();

        // Three possible error messages - API interaction failed, incorrect postal codes
        closeErrorPanel();
        closeErrorPanel();
       //closeErrorPanel();

        pause(1000);

        Robot robot = window.robot();

        robot.rotateMouseWheel(-2);

        pause(1000);

        walkingButton.click();

        closeErrorPanel();
        closeErrorPanel();
        //closeErrorPanel();

        pause(1000);

        busButton.click();

        closeErrorPanel();
        closeErrorPanel();
        //closeErrorPanel();

        robot.rotateMouseWheel(2);
        
    }


    public void testAmenityScreen(JPanelFixture sidebar2Panel){
        JButtonFixture clearBtn = sidebar2Panel.button("clearAmenityInfoButton");
        JTextComponentFixture inputField = sidebar2Panel.textBox("inputField");
        JButtonFixture scoreBtn = sidebar2Panel.button("scoreButton");

        inputField.requireText("Enter postal code:");

        inputField.enterText("6221CS");
        scoreBtn.click();
        pause(1500);

        clearBtn.click();
        inputField.requireText("Enter postal code:");

        inputField.deleteText();
        inputField.enterText("9999AA");
        scoreBtn.click();
        //closeErrorPanel();
        closeErrorPanel();

        inputField.deleteText();
        inputField.enterText("adaadwdlald");
        scoreBtn.click();
        closeErrorPanel();

    }

    @Test
    /**
     * Runs through two example user interactions - expected, and incorrect, checking error handling, for both routing and amenity screens
     */
    public void testInteraction() {

        JPanelFixture sidebarPanel = window.panel("sidebar");
        pause(1000);
        testRoutingScreen(sidebarPanel);

        JToggleButtonFixture switchToMetricButton = sidebarPanel.toggleButton("toMetricButton");

        // Test the socioeconomic metric panel
        switchToMetricButton.click();
        pause(1000);

        JPanelFixture metricSidebarPanel = window.panel("sidebar2");
        metricSidebarPanel.requireVisible();
        testAmenityScreen(metricSidebarPanel);

        //Switch back to routing
        JButtonFixture backBtn = metricSidebarPanel.button("toRoutingButton");
        backBtn.click();

        pause(2000);

    }

    private void deleteDB(){
        File file = new File("./Database/graph_data.db");
        if(file.exists()){
            file.delete();
        }
    }

    private void closeErrorPanel(){
        pause(1000);
        DialogFixture errorDialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
            protected boolean isMatching(JDialog dialog) {
                return "Error".equals(dialog.getTitle()) && dialog.isShowing();
            }
        }).using(window.robot());
        errorDialog.requireVisible();
        errorDialog.close();
        pause(1000);
    }


}
