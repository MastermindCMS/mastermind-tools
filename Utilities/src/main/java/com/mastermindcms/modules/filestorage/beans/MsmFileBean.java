package com.mastermindcms.modules.filestorage.beans;

import org.springframework.data.annotation.Id;
import org.springframework.http.MediaType;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class MsmFileBean {
    @Id
    private String id;
    private String key;
    @NotNull
    private String name;
    @NotNull
    private String path;
    private Date dateModified;
    private boolean isDirectory;
    private long size;
    private String mimeType = MediaType.ALL_VALUE;
    private boolean hasSubDirectories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public boolean getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean directory) {
        isDirectory = directory;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isHasSubDirectories() {
        return hasSubDirectories;
    }

    public void setHasSubDirectories(boolean hasSubDirectories) {
        this.hasSubDirectories = hasSubDirectories;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
