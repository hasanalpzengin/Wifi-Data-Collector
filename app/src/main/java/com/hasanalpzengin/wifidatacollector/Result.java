package com.hasanalpzengin.wifidatacollector;

import java.util.ArrayList;

/**
 * Created by hasalp on 04.04.2018.
 */

public class Result {
    private float coordinate_x, coordinate_y, coordinate_z;
    private ArrayList<Wifi> wifiList = new ArrayList<>();

    public Result(float coordinate_x, float coordinate_y, float coordinate_z) {
        this.coordinate_x = coordinate_x;
        this.coordinate_y = coordinate_y;
        this.coordinate_z = coordinate_z;
    }

    public float getCoordinate_x() {
        return coordinate_x;
    }

    public void setCoordinate_x(float coordinate_x) {
        this.coordinate_x = coordinate_x;
    }

    public float getCoordinate_z() {
        return coordinate_z;
    }

    public void setCoordinate_z(float coordinate_z) {
        this.coordinate_z = coordinate_z;
    }

    public float getCoordinate_y() {
        return coordinate_y;
    }

    public void setCoordinate_y(float coordinate_y) {
        this.coordinate_y = coordinate_y;
    }

    public void addToList(Wifi wifi){
        wifiList.add(wifi);
    }

    public ArrayList<Wifi> getWifiList() {
        return wifiList;
    }
}
