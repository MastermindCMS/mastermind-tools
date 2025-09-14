package com.mastermindcms.modules.filestorage.beans.devexpress;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;

public class DeArguments {
    
    private ArrayList<DePathInfoPart> pathInfo;
    private ArrayList<DePathInfoPart> sourcePathInfo;
    private ArrayList<DePathInfoPart> destinationPathInfo;
    private ArrayList<ArrayList<DePathInfoPart>> pathInfoList;
    private String name;
    private String destinationId;
    @JsonDeserialize(using = DeChunkMetadataDeserializer.class)
    private DeChunkMetadata chunkMetadata;

    public DeArguments() {
    }

    public DeArguments pathInfo(DePathInfoPart pathInfoPart) {
        if (this.pathInfo == null || this.pathInfo.isEmpty()) {
            this.pathInfo = new ArrayList<>();
        }
        this.pathInfo.add(pathInfoPart);
        return this;
    }

    public DeArguments sourcePathInfo(DePathInfoPart pathInfoPart) {
        if (this.sourcePathInfo == null || this.sourcePathInfo.isEmpty()) {
            this.sourcePathInfo = new ArrayList<>();
        }
        this.sourcePathInfo.add(pathInfoPart);
        return this;
    }

    public DeArguments destinationPathInfo(DePathInfoPart pathInfoPart) {
        if (this.destinationPathInfo == null || this.destinationPathInfo.isEmpty()) {
            this.destinationPathInfo = new ArrayList<>();
        }
        this.destinationPathInfo.add(pathInfoPart);
        return this;
    }
    
    public DeArguments pathInfoList(ArrayList<DePathInfoPart> pathInfo) {
        if (this.pathInfoList == null || this.pathInfoList.isEmpty()) {
            this.pathInfoList = new ArrayList<>();
        }
        this.pathInfoList.add(pathInfo);
        return this;
    }

    public DeArguments name(String name) {
        this.name = name;
        return this;
    }

    public DeArguments destinationId(String destinationId) {
        this.destinationId = destinationId;
        return this;
    }

    public DeArguments chunkMetadata(DeChunkMetadata chunkMetadata) {
        this.chunkMetadata = chunkMetadata;
        return this;
    }


    public ArrayList<DePathInfoPart> getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(ArrayList<DePathInfoPart> pathInfo) {
        this.pathInfo = pathInfo;
    }

    public ArrayList<DePathInfoPart> getSourcePathInfo() {
        return sourcePathInfo;
    }

    public void setSourcePathInfo(ArrayList<DePathInfoPart> sourcePathInfo) {
        this.sourcePathInfo = sourcePathInfo;
    }

    public ArrayList<DePathInfoPart> getDestinationPathInfo() {
        return destinationPathInfo;
    }

    public void setDestinationPathInfo(ArrayList<DePathInfoPart> destinationPathInfo) {
        this.destinationPathInfo = destinationPathInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public DeChunkMetadata getChunkMetadata() {
        return chunkMetadata;
    }

    public void setChunkMetadata(DeChunkMetadata chunkMetadata) {
        this.chunkMetadata = chunkMetadata;
    }

    public ArrayList<ArrayList<DePathInfoPart>> getPathInfoList() {
        return pathInfoList;
    }

    public void setPathInfoList(ArrayList<ArrayList<DePathInfoPart>> pathInfoList) {
        this.pathInfoList = pathInfoList;
    }
    
}
