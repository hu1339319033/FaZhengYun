package com.example.fazhengyun.model;

/**
 * Created by fscm-qt on 2017/7/27.
 */

public class Constants {
    //版本VersionID
    public static final int VERSIONID = 20;
    //是否首次启动
    public static final String FirstSTART = "isfirststart";
    //登录状态
    public static final String LOGIN_STATE = "login_state";
    //用户ID
    public static final String USERID = "userId";
    public static final String USERNAEM = "username";
    public static final String PHONE = "phone";
    public static final String PASSWORD = "password";
    public static final String REALNAME = "realname";
    public static final String MUSIC_FLAG ="music_flag";
    public static final String LOCALPHOTOLIST ="localphotolist";//本地未存证的拍照文件 Resources类
    public static final String LOCALVIDEOLIST ="localvideolist";//本地未存证的录像文件 Resources类
    public static final String LOCALAUDIOLIST ="localaudiolist";//本地未存证的录音文件 Resources类
    public static final String LOCALCallRECORDLIST ="localcallrecordlist";//本地未存证的录音文件 Resources类
    public static final String PHOTOTITLE = "拍照存证";
    public static final String VIDEOTITLE = "录像存证";
    public static final String AUDIOTITLE = "现场录音";
    public static final String CallRECORDTITLE = "通话录音";
    public enum LocalType{
        PHOTO,VIDEO,AUDIO,CALLRECORD,ALL
    }
    /**
     * 验证码发送成功后的本地存储变量名
     */
    public static final String CODE_STRING = "code_string";
    /**
     * 验证码发送成功时间本地存储变量名
     */
    public static final String CODEDATE_STRING = "codedata_string";
    /**
     * 验证码发送的手机号
     */
    public static final String CODEPHONE_STRING = "codephone_string";
}
