package com.example.fazhengyun.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by fscm-qt on 2017/12/16.
 */

public class DangerInfos extends BaseModel {

    private int totle;

    private Map<String,List<SubItem>> gpsInfoList;

    public Map<String, List<SubItem>> getGpsInfoList() {
        return gpsInfoList;
    }

    public void setGpsInfoList(Map<String, List<SubItem>> gpsInfoList) {
        this.gpsInfoList = gpsInfoList;
    }

    public int getTotle() {
        return totle;
    }

    public void setTotle(int totle) {
        this.totle = totle;
    }

    public class SubItem{
        @SerializedName("call_time")
        private long callTime;
        private String phone;
        @SerializedName("gps_addr")
        private String gpsAddr;
        @SerializedName("original_call_upload")
        private String originalCallUpload;
        private int status;
        @SerializedName("province_id")
        private int provinceId;
        @SerializedName("device_type")
        private String deviceType;
        @SerializedName("phoneContect")
        private String phonecontect;
        private int id;
        private String ipconfig;
        @SerializedName("all_phone_nums")
        private String allPhoneNums;
        @SerializedName("all_sms_nums")
        private String allSmsNums;
        @SerializedName("map_status")
        private int mapStatus;
        @SerializedName("call_upload")
        private String callUpload;
        @SerializedName("dealPeople")
        private String dealpeople;
        @SerializedName("client_user_id")
        private int clientUserId;
        @SerializedName("dealStatus")
        private String dealstatus;
        @SerializedName("sms_content")
        private String smsContent;

        public long getCallTime() {
            return callTime;
        }

        public void setCallTime(long callTime) {
            this.callTime = callTime;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getGpsAddr() {
            return gpsAddr;
        }

        public void setGpsAddr(String gpsAddr) {
            this.gpsAddr = gpsAddr;
        }

        public String getOriginalCallUpload() {
            return originalCallUpload;
        }

        public void setOriginalCallUpload(String originalCallUpload) {
            this.originalCallUpload = originalCallUpload;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getProvinceId() {
            return provinceId;
        }

        public void setProvinceId(int provinceId) {
            this.provinceId = provinceId;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getPhonecontect() {
            return phonecontect;
        }

        public void setPhonecontect(String phonecontect) {
            this.phonecontect = phonecontect;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIpconfig() {
            return ipconfig;
        }

        public void setIpconfig(String ipconfig) {
            this.ipconfig = ipconfig;
        }

        public String getAllPhoneNums() {
            return allPhoneNums;
        }

        public void setAllPhoneNums(String allPhoneNums) {
            this.allPhoneNums = allPhoneNums;
        }

        public String getAllSmsNums() {
            return allSmsNums;
        }

        public void setAllSmsNums(String allSmsNums) {
            this.allSmsNums = allSmsNums;
        }

        public int getMapStatus() {
            return mapStatus;
        }

        public void setMapStatus(int mapStatus) {
            this.mapStatus = mapStatus;
        }

        public String getCallUpload() {
            return callUpload;
        }

        public void setCallUpload(String callUpload) {
            this.callUpload = callUpload;
        }

        public String getDealpeople() {
            return dealpeople;
        }

        public void setDealpeople(String dealpeople) {
            this.dealpeople = dealpeople;
        }

        public int getClientUserId() {
            return clientUserId;
        }

        public void setClientUserId(int clientUserId) {
            this.clientUserId = clientUserId;
        }

        public String getDealstatus() {
            return dealstatus;
        }

        public void setDealstatus(String dealstatus) {
            this.dealstatus = dealstatus;
        }

        public String getSmsContent() {
            return smsContent;
        }

        public void setSmsContent(String smsContent) {
            this.smsContent = smsContent;
        }
    }

}
