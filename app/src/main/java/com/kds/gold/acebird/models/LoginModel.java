package com.kds.gold.acebird.models;

import java.io.Serializable;

public class LoginModel implements Serializable{
    String mac_address,portal;

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }
}
