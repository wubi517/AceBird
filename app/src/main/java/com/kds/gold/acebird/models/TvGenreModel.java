package com.kds.gold.acebird.models;

import java.io.Serializable;

public class TvGenreModel implements Serializable {
    String id,title,alias;
    int censored;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getCensored() {
        return censored;
    }

    public void setCensored(int censored) {
        this.censored = censored;
    }
}
