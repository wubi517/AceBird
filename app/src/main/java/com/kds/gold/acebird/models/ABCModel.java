package com.kds.gold.acebird.models;

import java.io.Serializable;

public class ABCModel implements Serializable {
    String id,name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
