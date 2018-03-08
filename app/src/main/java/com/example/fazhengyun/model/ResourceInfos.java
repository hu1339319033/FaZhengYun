package com.example.fazhengyun.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fscm-qt on 2017/11/29.
 */

public class ResourceInfos extends BaseModel{

    private List<Item> results;

    public List<Item> getResults() {
        if(results == null){
            results = new ArrayList<Item>();
        }
        return results;
    }

    public void setResults(List<Item> results) {
        this.results = results;
    }

    public static class Item implements Serializable {
        private String name;
        private String datetime;
        private String filepath;
        private String url;
        private String filesize;   //文件大小
        private int state;  //存证状态

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFilesize() {
            return filesize;
        }

        public void setFilesize(String filesize) {
            this.filesize = filesize;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getFilepath() {
            return filepath;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }
    }
}
