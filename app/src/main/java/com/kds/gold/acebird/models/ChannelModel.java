package com.kds.gold.acebird.models;

import java.io.Serializable;

public class ChannelModel implements Serializable {
    String id,name,tv_genre_id,cmd,number,logo;
    int tv_archiev_duration,locked,lock,fav,total_items,archive;

    public int getTotal_items() {
        return total_items;
    }
    public void setTotal_items(int total_items){this.total_items = total_items;}

    public String getId() {
        return id;
    }
    public void setId(String id){this.id = id;}

    public String getName() {
        return name;
    }
    public void setName(String name){this.name = name;}

    public String getTv_genre_id() {
        return tv_genre_id;
    }
    public void setTv_genre_id(String tv_genre_id){this.tv_genre_id = tv_genre_id;}

    public String getCmd() {
        return cmd;
    }
    public void setCmd(String cmd){this.cmd = cmd;}

    public int getTv_archiev_duration() {
        return tv_archiev_duration;
    }
    public void setTv_archiev_duration(int tv_archiev_duration){this.tv_archiev_duration = tv_archiev_duration;}

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public int getFav() {
        return fav;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public int getArchive() {
        return archive;
    }

    public void setArchive(int archive) {
        this.archive = archive;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
