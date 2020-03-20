package com.kds.gold.acebird.models;

import java.io.Serializable;

public class PositonModel implements Serializable {
    long position;
    String title;
    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}

    public long getPosition(){return position;}
    public void setPosition(long position){this.position = position;}
}
