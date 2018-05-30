package com.hasanalpzengin.wifidatacollector;

/**
 * Created by hasalp on 04.04.2018.
 */

public class Wifi {
    private String bssid, ssid;
    private int signal;

    public Wifi(String bssid, String ssid, int signal) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.signal = signal;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }
}
