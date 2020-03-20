package com.kds.gold.acebird.models;

import java.io.Serializable;

public class ProfileModel implements Serializable{
    String id,name,smane,pass,tv_quality,stb_type,mag_id,user_id,mac,token,username,password,is_trial;
    int expires;

    public String getId() {
        return id;
    }
    public void setId(String id){this.id = id;}

    public String getName() {
        return name;
    }
    public void setName(String name){this.name = name; }
    public String getSmane(){return smane;}
    public void setSmane(String sname){this.smane = sname;}

    public String getPass() {
        return pass;
    }
    public void setPass(String pass){this.pass = pass;}

    public String getTv_quality() {
        return tv_quality;
    }
    public void setTv_quality(String tv_quality){this.tv_quality = tv_quality;}

    public String getStb_type() {
        return stb_type;
    }
    public void setStb_type(String stb_type){this.stb_type = stb_type;}

    public String getMag_id() {
        return mag_id;
    }
    public void setMag_id(String mag_id){this.mag_id = mag_id;}

    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id){this.user_id = user_id;}

    public String getMac() {
        return mac;
    }
    public void setMac(String mac){this.mac = mac;}

    public String getToken() {
        return token;
    }
    public void setToken(String token){this.token = token;}

    public String getUsername() {
        return username;
    }
    public void setUsername(String username){this.username = username;}

    public String getPassword() {
        return password;
    }
    public void setPassword(String password){this.password = password;}

    public String getIs_trial() {
        return is_trial;
    }
    public void setIs_trial(String is_trial){this.is_trial = is_trial;}

    public int getExpires() {
        return expires;
    }
    public void setExpires(int expires){this.expires = expires;}
}
