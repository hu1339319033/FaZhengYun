package com.example.fazhengyun.net;

import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.model.GankResults;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.droidlover.xdroidbase.log.XLog;

/**
 * 网络请求封装类
 */

public class NetApi {

    public static void getGankData(String type, int pageSize, int pageNum, JsonCallback<GankResults> callback) {
        String url = "http://gank.io/api/data/{type}/{number}/{page}";
        url = url.replace("{type}", type)
                .replace("{number}", "" + pageSize)
                .replace("{page}", "" + pageNum);

        invokeGet(url, null, callback);
    }

    /**
     * 调用get请求
     * @param url
     * @param params
     * @param callback
     */
    public static void invokeGet(String url, HashMap<String,String> params, Callback callback) {
        //加密start
        long timestamp = System.currentTimeMillis();
        String sign = encrypt(params,"7EECF7A8337C4C8489674C6F3DC794F3",timestamp);
        XLog.e("sha1sign:"+sign);
        params.put("sign",sign);
        params.put("timestamp",timestamp+"");
        //加密end
        OkHttpUtils.get().url(url)
                .params(params == null ? new HashMap<String, String>() : params)
                .build()
                .execute(callback);
    }

    /**
     * 调用post请求
     * @param url
     * @param params
     * @param callback
     */
    public static void invokePost(String url, HashMap params, Callback callback) {
        OkHttpUtils.post().url(url)
                .params(params == null ? new HashMap<String, String>() : params)
                .build()
                .execute(callback);
    }

    public static void invokeDownLoad(String downloadurl,Callback callback){
        OkHttpUtils.get().url(downloadurl).build().execute(callback);
    }

    /**
     *
     * @param params
     * @param sign 后台给的sign字符串
     * @param timestamp 调用者传的时间戳
     * @return
     */
    public static String encrypt(HashMap<String,String> params,String sign,long timestamp){
        StringBuilder builder = new StringBuilder();
        Collection<String> keyset = params.keySet();
        List<String> list = new ArrayList<String>(keyset);
        list.add("sign");
        list.add("timestamp");
        //对key键值按字典升序排序
        Collections.sort(list);
        XLog.e("keylist:"+list);
        for (int i = 0; i<list.size();i++){
            builder.append(list.get(i));
            //拼接sign和timestamp参数--start
            if(list.get(i).equals("sign")){
                builder.append(sign);
                continue;
            }
            if(list.get(i).equals("timestamp")){
                builder.append(timestamp);
                continue;
            }
            //拼接sign和timestamp参数--end
            builder.append(params.get(list.get(i)));
        }
        XLog.e("builder:"+builder.toString());
        //Md5加密
        String md5str = AppKit.MD5Util.encode(builder.toString());
        XLog.e("md5str:"+md5str);
        String shastr = "";
        try {
            shastr = AppKit.SHA.encryptSHA(md5str);
        } catch (Exception e) {
            e.printStackTrace();
            XLog.e("sha1加密出现异常"+e);
        }

        return shastr;
    }
}
