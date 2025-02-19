package com.group8.phase1.TransferRouting;

import java.util.List;

public class TravelResults {
    public List<TravelNode> travelNodes;
    public long timeTaken;

    public TravelResults(List<TravelNode> travelNodes,long timeTaken){
        this.travelNodes = travelNodes;
        this.timeTaken = timeTaken;
    }

}
