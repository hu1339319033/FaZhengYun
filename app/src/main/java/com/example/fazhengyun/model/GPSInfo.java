package com.example.fazhengyun.model;

/**
 * Created by fscm-qt on 2017/12/18.
 */

public class GPSInfo {
//    {"id":787603,"gps_latitude":"39.82694894661208","gps_info_id":12739,"gps_addr":"北京市丰台区六圈路靠近总部基地18区","gps_dimension":"116.29805616613257","gps_time":1510800376000}
    private int id;
    private int gps_info_id;
    private float gps_latitude;
    private float gps_dimension;
    private long gps_time;
    private String gps_addr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGps_info_id() {
        return gps_info_id;
    }

    public void setGps_info_id(int gps_info_id) {
        this.gps_info_id = gps_info_id;
    }

    public float getGps_latitude() {
        return gps_latitude;
    }

    public void setGps_latitude(float gps_latitude) {
        this.gps_latitude = gps_latitude;
    }

    public float getGps_dimension() {
        return gps_dimension;
    }

    public void setGps_dimension(float gps_dimension) {
        this.gps_dimension = gps_dimension;
    }

    public long getGps_time() {
        return gps_time;
    }

    public void setGps_time(long gps_time) {
        this.gps_time = gps_time;
    }

    public String getGps_addr() {
        return gps_addr;
    }

    public void setGps_addr(String gps_addr) {
        this.gps_addr = gps_addr;
    }
}
