package com.example.fazhengyun.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;

import com.example.fazhengyun.R;
import com.example.fazhengyun.adapter.FileUploadMyGridAdapter;
import com.example.fazhengyun.databinding.FragmentLocalfileuploadBinding;
import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.ResultMsg;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.ui.view.GlideImageLoader;
import com.example.fazhengyun.ui.view.MyGridView;
import com.example.fazhengyun.xdroid.base.XLazyFragment;
import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.droidlover.xdroidbase.kit.Kits;
import cn.droidlover.xdroidbase.log.XLog;

import static com.example.fazhengyun.net.NetApi.encrypt;

/**
 * Created by fscm-qt on 2017/12/10.
 */

public class LocalFileUploadFragment extends XLazyFragment<FragmentLocalfileuploadBinding> {

    private static int CODE_REQUEST_REQUEST = 0xa0;
    private static final int CODE_IMAGE_REQUEST = 0xa1;
    private static final int CODE_VIDEO_REQUEST = 0xa2;
    private static final int CODE_Audio_REQUEST = 0xa3;
    private static final int CODE_TXT_REQUEST = 0xa4;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    MyGridView gridView;
    Intent intent;
    private ArrayList<ImageItem> imageItems;
    @Override
    public void initData(Bundle savedInstanceState) {
        gridView = getBinding().gridview;
        gridView.setAdapter(new FileUploadMyGridAdapter(context));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0: //本地图片
                        if(autoObtainStoragePermission()){
                            ImagePicker imagePicker = ImagePicker.getInstance();
                            imagePicker.setImageLoader(new GlideImageLoader());
                            imagePicker.setMultiMode(true);   //多选
                            imagePicker.setShowCamera(true);  //显示拍照按钮
                            imagePicker.setSelectLimit(9);    //最多选择9张
                            imagePicker.setCrop(false);       //不进行裁剪
                            intent = new Intent(context, ImageGridActivity.class);//Glide支出多选
                            intent.setType("image/*");
                            CODE_REQUEST_REQUEST = CODE_IMAGE_REQUEST;
                            startActivityForResult(intent, CODE_REQUEST_REQUEST);
                        }
                        break;
                    case 1://本地视频
                        if(autoObtainStoragePermission()){
                            intent = new Intent(Intent.ACTION_GET_CONTENT);//系统自动选择器
                            intent.setType("video/*");
                            CODE_REQUEST_REQUEST = CODE_VIDEO_REQUEST;
                            startActivityForResult(intent, CODE_REQUEST_REQUEST);
                        }
                        break;
                    case 2://本地音频
                        if(autoObtainStoragePermission()){
                            intent = new Intent(Intent.ACTION_GET_CONTENT);//系统自动选择器
                            intent.setType("audio/*");
                            CODE_REQUEST_REQUEST = CODE_Audio_REQUEST;
                            startActivityForResult(intent, CODE_REQUEST_REQUEST);
                        }
                        break;
                    case 3://本地文档
                        if(autoObtainStoragePermission()){
                            intent = new Intent(Intent.ACTION_GET_CONTENT);//系统自动选择器
                            intent.setType("text/*");
                            CODE_REQUEST_REQUEST = CODE_TXT_REQUEST;
                            startActivityForResult(intent, CODE_REQUEST_REQUEST);
                        }
                        break;
                    case 4://全部
                        if(autoObtainStoragePermission()){
                            intent = new Intent(Intent.ACTION_GET_CONTENT);//系统自动选择器
                            intent.setType("file/*");
                            CODE_REQUEST_REQUEST = CODE_TXT_REQUEST;
                            startActivityForResult(intent, CODE_REQUEST_REQUEST);
                        }
                        break;
                }
            }
        });
    }

    /**
     * 自动获取sdk权限
     * 打开本地相册
     */
    private boolean autoObtainStoragePermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(intent,CODE_REQUEST_REQUEST);
                } else {
                    getUiDelegate().toastShort("请允许操作SDCard！！");
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //访问相册完成回调
        if (data != null) {
            switch (requestCode){
                case CODE_IMAGE_REQUEST://图片
                    if(resultCode == ImagePicker.RESULT_CODE_ITEMS){
                        imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (imageItems != null && imageItems.size() > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("您选择了" + imageItems.size() + "张图片\n确定上传吗？");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //上传图片
                                    formUploadFile(0, null);
                                }
                            });
                            builder.setNegativeButton("取消", null);
                            builder.create().show();
                        } else {
                            getUiDelegate().toastShort("您没有选择图片");
                        }
                    }
                    break;
                case CODE_Audio_REQUEST://音频
                    final String audiofilepath = AppKit.getfromUri2Path(context,data.getData());
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("您选择了一段音频\n确定上传吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //上传音频
                            formUploadFile(1,audiofilepath);
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.create().show();
                    break;
                case CODE_VIDEO_REQUEST://视频
                    final String videofilepath = AppKit.getfromUri2Path(context,data.getData());
                    AlertDialog.Builder builderv = new AlertDialog.Builder(context);
                    builderv.setMessage("您选择了一段视频\n确定上传吗？");
                    builderv.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //上传视频
                            formUploadFile(2,videofilepath);
                        }
                    });
                    builderv.setNegativeButton("取消", null);
                    builderv.create().show();
                    break;
                case CODE_TXT_REQUEST:
                    final String filepath = AppKit.getfromUri2Path(context,data.getData());
                    AlertDialog.Builder builde = new AlertDialog.Builder(context);
                    builde.setMessage("您选择了一个文件\n确定上传吗？");
                    builde.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //上传视频
                            formUploadFile(3,filepath);
                        }
                    });
                    builde.setNegativeButton("取消", null);
                    builde.create().show();
                    break;
            }
        }else{
            getUiDelegate().toastShort("您没有选择文件");
        }
        //            if(null == data || null == data.getData()){
//                XLog.e("data==null");
//                return;
//            }
//            Uri newUri = Uri.parse(PhotoUtils.getPath(context, data.getData()));
//            XLog.e("filepath:"+newUri.getPath());
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        newUri = FileProvider.getUriForFile(context, "com.example.fazhengyun.fileprovider", new File(newUri.getPath()));
//                    }
    }

    /**
     * 文件上传
     * Int type（类型 0图片，1音频，2 视频 3 其他）
     Int upType（0 pc文件上传，1 手机录音 2，手机录像 3 手机照片）
     */
    private void formUploadFile(int type,String filepath){
        HashMap<String,String> params = new HashMap<>();
        params.put("userId", SharedPreferenceUtil.getInstance(context).getInt(Constants.USERID,-1)+"");
        params.put("upTime", Kits.Date.getYmdhms(System.currentTimeMillis()));
        params.put("type",type+"");//文件类型
        params.put("upType",0+"");//文件上传
        params.put("desc","Android端选择文件上传");

        ArrayList<File> files = new ArrayList<>();
        if(filepath==null){ //图片类型为可多选，路径存储在list集合里
            if (imageItems != null && imageItems.size() > 0) {
                for (int i = 0; i < imageItems.size(); i++) {
                    files.add(new File(imageItems.get(i).path));
                    XLog.e("filepath",imageItems.get(i).path);
                }
            }
        }else{//音频和视频类型传递单个文件路径
            XLog.e("filepath:"+filepath);
            files.add(new File(filepath));
        }
        //加密start
        long timestamp = System.currentTimeMillis();
        String sign = encrypt(params,"7EECF7A8337C4C8489674C6F3DC794F3",timestamp);
        XLog.e("sha1sign:"+sign);
        params.put("sign",sign);
        params.put("timestamp",timestamp+"");
        //加密end

        OkGo.<String>post(UrlKit.getUrl(UrlKit.ACTION_FileUpload))
                .params(params)
                .addFileParams("file",files)
                .execute(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        XLog.e("Response:","start");
                        getUiDelegate().getProcessDialog().setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        getUiDelegate().showProcessDialog("正在上传");
                    }

                    @Override
                    public void uploadProgress(Progress progress) {
                        super.uploadProgress(progress);
                        XLog.e("uploadProgress: " + progress);
                        String speed = Formatter.formatFileSize(context, progress.speed);
                        XLog.e("speed:"+String.format("%s/s", speed));
                        getUiDelegate().getProcessDialog().setProgress((int) (progress.fraction * 100));
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        XLog.e("Response:","error");
                        getUiDelegate().dimmisProcessDiaglog();
                        getUiDelegate().toastShort("上传失败，请重试");
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        XLog.e("Response:",response.body().toString());
                        getUiDelegate().dimmisProcessDiaglog();
                        ResultMsg resultMsg = new Gson().fromJson(response.body().toString(),ResultMsg.class);
                        getUiDelegate().toastShort(resultMsg.getMsg());
                    }
                });
    }
    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_localfileupload;
    }
    public static LocalFileUploadFragment newInstance(){
        return new LocalFileUploadFragment();
    }
}
