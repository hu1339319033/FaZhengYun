package com.example.fazhengyun.model;

/**
 * Created by fscm-qt on 2017/12/9.
 */

public class ResultMsg extends BaseModel {
    private int result;
    private String msg;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
