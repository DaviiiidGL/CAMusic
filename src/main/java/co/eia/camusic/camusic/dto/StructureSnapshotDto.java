package co.eia.camusic.camusic.dto;

import co.eia.camusic.camusic.model.PlaybackMode;
import co.eia.camusic.camusic.util.IdGenerator;
import co.eia.camusic.camusic.util.JsonUtil;

import java.util.List;

public class StructureSnapshotDto {
    private List<String> nodesInOrder;
    private String currentNodeId;
    private PlaybackMode mode;

    public StructureSnapshotDto(List<String> nodesInOrder, String currentNodeId, PlaybackMode mode) {
        this.nodesInOrder = nodesInOrder;
        this.currentNodeId = currentNodeId;
        this.mode = mode;
    }

    public List<String> getNodesInOrder() {return nodesInOrder;}
    public void setNodesInOrder(List<String> nodesInOrder) {this.nodesInOrder = nodesInOrder;}

    public String getCurrentNodeId() {return currentNodeId;}
    public void setCurrentNodeId(String currentNodeId) {this.currentNodeId = currentNodeId;}

    public PlaybackMode getMode() {return mode;}
    public void setMode(PlaybackMode mode) {this.mode = mode;}


}