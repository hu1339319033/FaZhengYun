package com.example.fazhengyun.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fscm-qt on 2017/12/16.
 */

public class AssestInfos extends BaseModel {

    private int totle;

    private List<Item> applyList;

    public int getTotle() {
        return totle;
    }

    public void setTotle(int totle) {
        this.totle = totle;
    }

    public List<Item> getApplyList() {
        return applyList;
    }

    public void setApplyList(List<Item> applyList) {
        this.applyList = applyList;
    }

    public class Item{
        private int id;
        @SerializedName("upload_id")
        private String uploadId;
        @SerializedName("upload_url")
        private String uploadUrl;
        @SerializedName("clientUserId")
        private int clientuserid;
        @SerializedName("log_status")
        private int logStatus;
        @SerializedName("log_type")
        private int logType;
        @SerializedName("log_time")
        private long logTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUploadId() {
            return uploadId;
        }

        public void setUploadId(String uploadId) {
            this.uploadId = uploadId;
        }

        public String getUploadUrl() {
            return uploadUrl;
        }

        public void setUploadUrl(String uploadUrl) {
            this.uploadUrl = uploadUrl;
        }

        public int getClientuserid() {
            return clientuserid;
        }

        public void setClientuserid(int clientuserid) {
            this.clientuserid = clientuserid;
        }

        public int getLogStatus() {
            return logStatus;
        }

        public void setLogStatus(int logStatus) {
            this.logStatus = logStatus;
        }

        public int getLogType() {
            return logType;
        }

        public void setLogType(int logType) {
            this.logType = logType;
        }

        public long getLogTime() {
            return logTime;
        }

        public void setLogTime(long logTime) {
            this.logTime = logTime;
        }
    }
}
