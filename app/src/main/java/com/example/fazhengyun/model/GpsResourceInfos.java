package com.example.fazhengyun.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fscm-qt on 2017/12/18.
 */

public class GpsResourceInfos extends BaseModel{
    private int totlel;
    private List<Item> gpsUploadList;

    public int getTotlel() {
        return totlel;
    }

    public void setTotlel(int totlel) {
        this.totlel = totlel;
    }

    public List<Item> getGpsUploadList() {
        return gpsUploadList;
    }

    public void setGpsUploadList(List<Item> gpsUploadList) {
        this.gpsUploadList = gpsUploadList;
    }

    public class Item{
        private int id;
        @SerializedName("upload_type")
        private int uploadType;
        @SerializedName("original_upload_url")
        private String originalUploadUrl;
        @SerializedName("ftp_status")
        private int ftpStatus;
        @SerializedName("upload_date")
        private long uploadDate;
        @SerializedName("upload_url")
        private String uploadUrl;
        @SerializedName("gps_id")
        private int gpsId;
        @SerializedName("upload_title")
        private String uploadTitle;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUploadType() {
            return uploadType;
        }

        public void setUploadType(int uploadType) {
            this.uploadType = uploadType;
        }

        public String getOriginalUploadUrl() {
            return originalUploadUrl;
        }

        public void setOriginalUploadUrl(String originalUploadUrl) {
            this.originalUploadUrl = originalUploadUrl;
        }

        public int getFtpStatus() {
            return ftpStatus;
        }

        public void setFtpStatus(int ftpStatus) {
            this.ftpStatus = ftpStatus;
        }

        public long getUploadDate() {
            return uploadDate;
        }

        public void setUploadDate(long uploadDate) {
            this.uploadDate = uploadDate;
        }

        public String getUploadUrl() {
            return uploadUrl;
        }

        public void setUploadUrl(String uploadUrl) {
            this.uploadUrl = uploadUrl;
        }

        public int getGpsId() {
            return gpsId;
        }

        public void setGpsId(int gpsId) {
            this.gpsId = gpsId;
        }

        public String getUploadTitle() {
            return uploadTitle;
        }

        public void setUploadTitle(String uploadTitle) {
            this.uploadTitle = uploadTitle;
        }
    }

}
