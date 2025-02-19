package pathfinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.group8.phase1.osm.LoadRoutingGraph;
import com.group8.phase1.pathfinding.Path;
import com.group8.phase1.pathfinding.RoutingEngine;
import com.group8.phase1.structures.map.Graph;

class RoutingEngineTest {

    RoutingEngine routingEngine;
    List<Long> randomNodeIds= new ArrayList<>();

    @Test
    void getShortestPath() {
        Graph routingGraph = LoadRoutingGraph.load(null);
        routingEngine = new RoutingEngine(routingGraph);
        randomNodeIds.add(20908098L);
        randomNodeIds.add(25340973L);
        randomNodeIds.add(41904671L);
        randomNodeIds.add(41913753L);
        randomNodeIds.add(41929691L);

        for(Long sourceNode : randomNodeIds){
             for(Long targetNode : randomNodeIds){
                 if(!Objects.equals(sourceNode, targetNode)){
                     Path path = routingEngine.getShortestPath(sourceNode,targetNode);
                     Assertions.assertNotNull(path, "Path shouldn't be null for source: " + sourceNode + "and target: " + targetNode);
                 }
             }
        }

    }
}