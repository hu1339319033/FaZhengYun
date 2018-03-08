package com.example.fazhengyun.model;

/**
 * Created by fscm-qt on 2017/12/12.
 */

public class LoginInfo extends ResultMsg {
    private int id;
    private String username;
    private int auth;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }
}
