package com.mastermindcms.modules.filestorage.beans.devexpress;

public class DePathInfoPart {
    
    public String key;
    public String name;

    public DePathInfoPart() {
    }

    public DePathInfoPart(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public DePathInfoPart key(String key) {
        this.key = key;
        return this;
    }

    public DePathInfoPart name(String name) {
        this.name = name;
        return this;
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

    public void setName(String name) {
        this.name = name;
    }
    
}
