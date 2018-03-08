package com.example.fazhengyun.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fscm-qt on 2017/12/12.
 */

public class UploadListInfos extends BaseModel{

    private int totle;
    private List<Item> uploadList;

    public int getTotle() {
        return totle;
    }

    public void setTotle(int totle) {
        this.totle = totle;
    }

    public List<Item> getUploadList() {
        return uploadList;
    }

    public void setUploadList(List<Item> uploadList) {
        this.uploadList = uploadList;
    }

    public static class Item {
        private int id;
        @SerializedName("original_name")
        private String originalName;
        @SerializedName("upload_url_min")
        private String uploadUrlmin;
        @SerializedName("upload_date")
        private long uploadDate;
        private int type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOriginalName() {
            return originalName;
        }

        public void setOriginalName(String originalName) {
            this.originalName = originalName;
        }

        public long getUploadDate() {
            return uploadDate;
        }

        public void setUploadDate(long uploadDate) {
            this.uploadDate = uploadDate;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUploadUrlmin() {
            return uploadUrlmin;
        }

        public void setUploadUrlmin(String uploadUrlmin) {
            this.uploadUrlmin = uploadUrlmin;
        }
    }
}
