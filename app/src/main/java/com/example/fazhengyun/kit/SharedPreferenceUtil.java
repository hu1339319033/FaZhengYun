package com.example.fazhengyun.kit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * /**
 * SharedPreferences工具类，可以保存object对象
 * 存储时以object存储到本地，获取时返回的也是object对象，需要自己进行强制转换
 * 也就是说，存的人和取的人要是同一个人才知道取出来的东西到底是个啥 ^_^
 * 作者：SiYiGuo
 链接：http://www.jianshu.com/p/ae0ca6c2d926
 來源：简书
 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 * Created by fscm-qt on 2017/12/1.
 */

public class SharedPreferenceUtil {

    private Context context;
    private static SharedPreferenceUtil instance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static String SP_NAME = "config";

    public SharedPreferenceUtil(Context context){
        this.context = context;
        sharedPreferences = context.getApplicationContext().getSharedPreferences(SP_NAME, 0);
        editor = sharedPreferences.edit();
    }

    public static SharedPreferenceUtil getInstance(Context context) {
        if(instance == null) {
            synchronized(SharedPreferenceUtil.class) {
                if(instance == null) {
                    instance = new SharedPreferenceUtil(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void clear() {
        editor.clear().apply();
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public void putLong(String key, Long value) {
        editor.putLong(key, value.longValue());
        editor.apply();
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public void putBoolean(String key, Boolean value) {
        editor.putBoolean(key, value.booleanValue());
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    /**
     * 使用SharedPreference保存对象
     *
     * @param key        储存对象的key
     * @param saveObject 储存的对象
     */
    public void putObject(String key, Object saveObject) {
        String string = Object2String(saveObject);
        editor.putString(key, string);
        editor.commit();
    }

    /**
     * 获取SharedPreference保存的对象
     *
     * @param key     储存对象的key
     * @return object 返回根据key得到的对象
     */
    public Object getObject(String key) {
        String string = sharedPreferences.getString(key, null);
        if (string != null) {
            Object object = String2Object(string);
            return object;
        } else {
            return null;
        }
    }

    /**
     * writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
     * 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
     *
     * @param object 待加密的转换为String的对象
     * @return String   加密后的String
     */
    private String Object2String(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String string = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            objectOutputStream.close();
            return string;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用Base64解密String，返回Object对象
     *
     * @param objectString 待解密的String
     * @return object      解密后的object
     */
    private Object String2Object(String objectString) {
        byte[] mobileBytes = Base64.decode(objectString.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
