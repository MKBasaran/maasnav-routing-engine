package api;

import org.junit.jupiter.api.Test;

import com.group8.phase1.structures.map.Node;

import static com.group8.phase1.api.WebInteraction.retrievePostalCodeData;
import static org.junit.jupiter.api.Assertions.*;

class WebInteractionTest {

    @Test
    void retrieveData() {
        Node retrievedNode = retrievePostalCodeData("6229HD");
        assertNotNull(retrievedNode);
    }
}