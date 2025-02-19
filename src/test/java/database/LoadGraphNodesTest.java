package database;

import com.group8.phase1.osm.LoadGraphNodes;
import com.group8.phase1.structures.map.Node;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

public class LoadGraphNodesTest {
    Map<Long,Node> nodeMap;

    public LoadGraphNodesTest(){
        nodeMap = LoadGraphNodes.loadNodes();
    }
    @Test
    public void testNodeMapNullity()  {
        assertNotNull(nodeMap);
    }
    @Test
    public void testNodesValid() {
        for(Node node : nodeMap.values()) {
            assertNotNull(node.getId());
            assertNotNull(node.getLatitude());
            assertNotNull(node.getLongitude());
        }
    }
}
