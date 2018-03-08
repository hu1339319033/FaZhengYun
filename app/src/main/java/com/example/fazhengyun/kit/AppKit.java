package com.example.fazhengyun.kit;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.cloopen.rest.sdk.CCPRestSmsSDK;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.droidlover.xdroidbase.log.XLog;
import okhttp3.MediaType;
import okhttp3.Request;
import okio.Buffer;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by wanglei on 2016/12/11.
 */

public class AppKit {

    private static AppKit instance;
    public AppKit(){}

    public synchronized static AppKit getInstance(){
        if(instance == null){
            instance = new AppKit();
        }
        return instance;
    }
    public static void copyToClipBoard(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(
                Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("xdroid_copy", text));
        Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
    }

    public static void openInBrowser(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "打开失败了，没有可打开的应用", Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareText(Context context, String shareText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享"));
    }

    public static void shareImage(Context context, Uri uri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, "分享图片"));
    }

    public static boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }

    public static boolean isNetWorkConnection(Context context) {
        ConnectivityManager manager=(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info=manager.getActiveNetworkInfo();
        return (info!=null&&info.isConnected());
    }

    public static String getVersion(Context context) {
        try {
            //通过包管理者拿到配置文件中的版本号
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return "Version" + info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Version";
        }
    }

    public static class MD5Util {

        public static String encode(String password) {
            try {
                MessageDigest digest = MessageDigest.getInstance("md5");
                byte[] result = digest.digest(password.getBytes());
                StringBuffer sb = new StringBuffer();
                for (byte b : result) {
                    int number = b & 0xff; // - 1; //加盐
                    String hexCode = Integer.toHexString(number);
                    if (hexCode.length() == 1) {
                        sb.append("0");
                    }
                    sb.append(hexCode);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                // can't reach
                return "";
            }
        }
    }

    public static class SHA {

        private final static String KEY_SHA1 = "SHA-1";
        /**
         * 全局数组
         */
        private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
        public SHA() {
        }

        /**
         * SHA 加密
         * @param data 需要加密的字节数组
         * @return 加密之后的字节数组
         * @throws Exception
         */
        public static byte[] encryptSHA(byte[] data) throws Exception {
            // 创建具有指定算法名称的信息摘要
            MessageDigest sha = MessageDigest.getInstance(KEY_SHA1);
            // 使用指定的字节数组对摘要进行最后更新
            sha.update(data);
            // 完成摘要计算并返回
            return sha.digest();
        }

        /**
         * SHA 加密
         * @param data 需要加密的字符串
         * @return 加密之后的字符串
         * @throws Exception
         */
        public static String encryptSHA(String data) throws Exception {
            // 验证传入的字符串
            if (TextUtils.isEmpty(data)) {
                return "";
            }
            // 创建具有指定算法名称的信息摘要
            MessageDigest sha = MessageDigest.getInstance(KEY_SHA1);
            // 使用指定的字节数组对摘要进行最后更新
            sha.update(data.getBytes());
            // 完成摘要计算
            byte[] bytes = sha.digest();
            // 将得到的字节数组变成字符串返回
            return byteArrayToHexString(bytes);
        }


        /**
         * 转换字节数组为十六进制字符串
         * @param bytes 字节数组
         * @return 十六进制字符串
         */
        private static String byteArrayToHexString(byte[] bytes) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(byteToHexString(bytes[i]));
            }
            return sb.toString();
        }

        /**
         * 将一个字节转化成十六进制形式的字符串
         * @param b 字节数组
         * @return 字符串
         */
        private static String byteToHexString(byte b) {
            int ret = b;
            //System.out.println("ret = " + ret);
            if (ret < 0) {
                ret += 256;
            }
            int m = ret / 16;
            int n = ret % 16;
            return hexDigits[m] + hexDigits[n];
        }
    }

    /**
     * 将uri转化成file.getAbsolutePath()的过程。
     * @param uri
     * @return
     */
    public static String getfromUri2Path(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String pathHead = "";
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return pathHead + getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return pathHead + getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 获取文件大小
     * @param file
     * @return
     */
    public static String GetFileSize(File file){
        String size = "";
        if(file.exists() && file.isFile()){
            long fileS = file.length();
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileS < 1024) {
                size = df.format((double) fileS) + "BT";
            } else if (fileS < 1048576) {
                size = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                size = df.format((double) fileS / 1048576) + "MB";
            } else {
                size = df.format((double) fileS / 1073741824) +"GB";
            }
        }else if(file.exists() && file.isDirectory()){
            size = "";
        }else{
            size = "0BT";
        }
        return size;
    }

    /**
     * 获取当前时间的纯数字字符串
     */
    public static String getYmdhmsString(long timeInMills){
        SimpleDateFormat ymdhms = new SimpleDateFormat("yyyyMMddHHmmss");
        return ymdhms.format(new java.util.Date(timeInMills));
    }

    /**
     * 获取两个时间相差几分钟
     * @return
     */
    public static long getTimeDistance(Date dateStart,Date dateEnd){
        XLog.e("dateStart"+dateStart+",dateEnd"+dateEnd);
        Calendar dateOne=Calendar.getInstance(),dateTwo= Calendar.getInstance();
        dateOne.setTime(dateEnd);	//设置为当前系统时间
        dateTwo.setTime(dateStart);		//设置为2015年1月15日
        long timeOne=dateOne.getTimeInMillis();
        long timeTwo=dateTwo.getTimeInMillis();
        XLog.e("timeOne"+timeOne+",timeTwo"+timeTwo);
        long minute=(timeOne-timeTwo)/(1000*60);//转化minute
        System.out.println("相隔"+minute+"分钟");
        return minute;
    }
    public static class OpenFileIntent{

        //android获取一个用于打开图片文件的intent
        public static Intent getImageFileIntent(Context context,String param) {
            XLog.e("path:"+param);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(param));
            //通过FileProvider创建一个content类型的Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, "com.example.fazhengyun.fileprovider", new File(param));
                XLog.e("path:"+uri.getPath());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(uri, "image/*");
            return intent;
        }

        //android获取一个用于打开网络图片文件的intent
        public static Intent getImageFileIntentnetwork(Context context,String url) {
            XLog.e("url:"+url);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            Uri uri = Uri.parse(url);
            intent.setDataAndType(uri, "image/*");
            return intent;
        }
        //android获取一个用于打开音频文件的intent
        public static Intent getAudioFileIntent(Context context,String param) {
            XLog.e("path:"+param);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(param));
            //通过FileProvider创建一个content类型的Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, "com.example.fazhengyun.fileprovider", new File(param));
                XLog.e("path:"+uri.getPath());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(uri, "audio/*");
            return intent;
        }

        //android获取一个用于打开视频文件的intent
        public static Intent getVideoFileIntent(Context context,String param) {
            XLog.e("path:"+param);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(param));
            //通过FileProvider创建一个content类型的Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, "com.example.fazhengyun.fileprovider", new File(param));
                XLog.e("path:"+uri.getPath());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(uri, "video/*");
            return intent;
        }

        //android获取一个用于打开网络视频文件的intent
        public static Intent getVideoFileIntentnetwork(Context context,String url) {
            XLog.e("url:"+url);
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
            mediaIntent.setDataAndType(Uri.parse(url), mimeType);
            return mediaIntent;
        }
    }

    public static boolean isServiceWorked(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> services = (ArrayList<ActivityManager.RunningServiceInfo>) manager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).service.getClassName().equals(serviceName)) return true;
        }
        return false;
    }

    private CCPRestSmsSDK restAPI;

    public CCPRestSmsSDK getRestAPI() {
        return restAPI;
    }

    public void initSmsSdK() {
        // 初始化SDK
        restAPI = new CCPRestSmsSDK();

        // ******************************注释*********************************************
        // *初始化服务器地址和端口 *
        // *沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
        // *生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883"); *
        // *******************************************************************************
        restAPI.init("app.cloopen.com", "8883");

        // ******************************注释*********************************************
        // *初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN *
        // *ACOUNT SID和AUTH TOKEN在登陆官网后，在“应用-管理控制台”中查看开发者主账号获取*
        // *参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。 *
        // *******************************************************************************
        restAPI.setAccount("8a48b55149896cfd0149a7a824c5167c",
                "6ad9de7758664f24b180cca729d6e325");

        // ******************************注释*********************************************
        // *初始化应用ID *
        // *测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID *
        // *应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
        // *******************************************************************************
        restAPI.setAppId("8a48b5514a4d9532014a50b9b1b60091");
    }
}
