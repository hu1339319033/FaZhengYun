package com.example.fazhengyun.net;

/**
 * Created by wanglei on 2016/12/9.
 */

public class UrlKit {
    public static final String API_BASE_URL = "http://fazhengyun-api.miaosos.com/";
//    public static final String API_BASE_URL = "http://47.93.123.200:8080/controller/";

    public static final String ACTION_DATA_RESULT = "data/{type}/{number}/{page}";

    public static final String ACTION_Registry = "user/registry";

    //忘记密码及修改密码接口地址
    public static final String ACTION_UpdatePassword = "user/updatePassword";
    //修改绑定手机号
    public static final String ACTION_UpdatePhone = "user/updatePhone";
    //修改用户名
    public static final String ACTION_UpdateName = "user/updateUsername";

    public static final String ACTION_Login = "user/login";
    //实名认证
    public static final String ACTION_AuthRealname = "user/authRealname";

    //上传文件
    public static final String ACTION_FileUpload = "file/upload";
    //已上传文件列表
    public static final String ACTION_UploadList = "file/uploadList";
    //文件下载
    public static final String ACTION_DownLoad = "file/download";

    public static final String ACTION_Delete = "file/deleteFile";
    /*版本更新*/
    public static final String ACTION_Version = "user/getVersion";
    //获取报险信息列表
    public static final String ACTION_GetGPSInfo = "user/getGpsInfo";
    //获取秒帮上传资源列表
    public static final String ACTION_GetGpsUploadList = "user/getGpsUploadList";
    //获取FTP地址
    public static final String ACTION_GetFtpAddr = "user/getFTPAddr";
    //获取最新地址
    public static final String ACTION_GetGPSAddr = "user/getGpsAddr";
    //申请出证
    public static final String ACTION_ApplyAsset = "file/applyAttest";
    //出证列表
    public static final String ACTION_AttestList = "file/applyAttestList";


    public static String getUrl(String action) {
        return new StringBuilder(API_BASE_URL).append(action).toString();
    }
}
