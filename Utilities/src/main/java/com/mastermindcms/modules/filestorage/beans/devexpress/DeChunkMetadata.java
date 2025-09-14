package com.mastermindcms.modules.filestorage.beans.devexpress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeChunkMetadata {

    @JsonProperty("UploadId")
    private String uploadId;
    @JsonProperty("FileName")
    private String fileName;
    @JsonProperty("FileSize")
    private long fileSize;
    @JsonProperty("Index")
    private int index;
    @JsonProperty("TotalCount")
    private int totalCount;
    
    public DeChunkMetadata() {}
    
    public DeChunkMetadata(
            String uploadId,
            String fileName,
            long fileSize,
            int index,
            int totalCount
    ) {
        this.uploadId = uploadId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.index = index;
        this.totalCount = totalCount;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public DeChunkMetadata uploadId(String uploadId) {
        this.uploadId = uploadId;
        return this;
    }

    public DeChunkMetadata fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public DeChunkMetadata fileSize(long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public DeChunkMetadata index(int index) {
        this.index = index;
        return this;
    }

    public DeChunkMetadata totalCount(int totalCount) {
        this.totalCount = totalCount;
        return this;
    }

}
