package com.kds.gold.acebird.models;

import java.io.Serializable;

public class PlayModel implements Serializable {
    int page, sub_pos;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSub_pos() {
        return sub_pos;
    }

    public void setSub_pos(int sub_pos) {
        this.sub_pos = sub_pos;
    }
}
