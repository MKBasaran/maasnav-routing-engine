package database;

import com.group8.phase1.osm.LoadRoutingGraph;
import com.group8.phase1.structures.map.Graph;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class LoadRoutingGraphTest {
    Graph graph;
    public LoadRoutingGraphTest(){
        graph = LoadRoutingGraph.loadGraph();
    }

    @Test
    public void testGraphNullity(){
        assertNotNull(graph);
    }
}
