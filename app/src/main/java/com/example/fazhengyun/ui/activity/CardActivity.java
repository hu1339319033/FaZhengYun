package com.example.fazhengyun.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityCardBinding;
import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.kit.IdcardUtils;
import com.example.fazhengyun.kit.PhotoUtils;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.ResultMsg;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.xdroid.base.XActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.droidlover.xdroidbase.log.XLog;
import okhttp3.Call;

import static com.example.fazhengyun.net.NetApi.encrypt;

/**
 * Created by fscm-qt on 2017/12/8.
 */

public class CardActivity extends XActivity<ActivityCardBinding> implements View.OnClickListener {

    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private static int CODE_REQUEST= 0xa0;
    private static final int CODE_REQUEST_CARD_FROUT = 0xa1;
    private static final int CODE_REQUEST_CARD_RESVERSE = 0xa2;
    private Bitmap frontBitmap;
    private Bitmap resevrseBitmap;
    int sexpostion = 1;//默认男
    int position = 0;//默认身份证
    private String[] paths = new String[2];

    @Override
    public void initData(Bundle savedInstanceState) {
        getBinding().toolbar.toolbarTitle.setText("实名认证");
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void setListener() {
        super.setListener();
        getBinding().imgCardFront.setOnClickListener(this);
        getBinding().imgCardReverse.setOnClickListener(this);
        getBinding().tvSex.setOnClickListener(this);
        getBinding().tvCardType.setOnClickListener(this);
        getBinding().btnBottom.setOnClickListener(this);
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_card_front:
                CODE_REQUEST = CODE_REQUEST_CARD_FROUT;
                autoObtainStoragePermissionImage();
                break;
            case R.id.img_card_reverse:
                CODE_REQUEST = CODE_REQUEST_CARD_RESVERSE;
                autoObtainStoragePermissionImage();
                break;
            case R.id.tv_sex:
                final String[] sexs = new String[]{"男","女"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("选择证件类型");
                builder.setSingleChoiceItems(sexs,0,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 1){
                            sexpostion = 0;
                        }else{
                            sexpostion = i;
                        }
                        getBinding().tvSex.setText(sexs[i]);
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                    }
                });
                builder.setNegativeButton("取消",null);
                builder.create();
                builder.show();
                break;
            case R.id.tv_card_type:
                final String[] cardtypes = new String[]{"身份证","军官证","护照", "其他"};
                final AlertDialog.Builder buildertype = new AlertDialog.Builder(context);
                buildertype.setTitle("选择证件类型");
                buildertype.setSingleChoiceItems(cardtypes, 0,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        position = i;
                        getBinding().tvCardType.setText(cardtypes[i]);
                    }
                });
                buildertype.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                    }
                });
                buildertype.setNegativeButton("取消",null);
                buildertype.create();
                buildertype.show();
                break;
            case R.id.btn_bottom:
                //校验
                if(TextUtils.isEmpty(getBinding().etName.getText().toString())){
                    getUiDelegate().toastShort("请填写真实姓名");
                    return;
                }
                if(TextUtils.isEmpty(getBinding().tvCardnumber.getText().toString())){
                    getUiDelegate().toastShort("请填写证件号码");
                    return;
                }
                if(IdcardUtils.validateCard(getBinding().tvCardnumber.getText().toString())){
                    getUiDelegate().toastShort("证件号格式不正确,请检查");
                    return;
                }
                if(paths[0]==null||paths[1]==null){
                    getUiDelegate().toastShort("请上传身份证正反面照片");
                    return;
                }
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("userId", SharedPreferenceUtil.getInstance(context).getInt(Constants.USERID,-1)+"");
                hashMap.put("idType",position+"");
                hashMap.put("username",getBinding().etName.getText().toString());
                hashMap.put("idCard",getBinding().tvCardnumber.getText().toString());
                hashMap.put("gender",sexpostion+"");
                NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_AuthRealname), hashMap, new JsonCallback<ResultMsg>() {
                    @Override
                    public void onResponse(ResultMsg response, int id) {
                        if(response.getResult()==0){
                            getUiDelegate().toastShort(response.getMsg());
                            return;
                        }
                        formUploadFile();
                    }

                    @Override
                    public void onFail(Call call, Exception e, int id) {

                    }
                });
                break;
        }
    }

    private void formUploadFile(){
        HashMap<String,String> params = new HashMap<>();
        params.put("userId", SharedPreferenceUtil.getInstance(context).getInt(Constants.USERID,-1)+"");
//        params.put("upTime", Kits.Date.getYmdhms(System.currentTimeMillis()));
        params.put("type",0+"");//文件类型
        params.put("upType",5+"");//文件上传
        params.put("desc","Android端实名认证");

        ArrayList<File> files = new ArrayList<>();
        for (int g = 0; g < paths.length; g++){
            files.add(new File(paths[g]));
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
                        getUiDelegate().showProcessDialog("正在上传。。。");
                    }

                    @Override
                    public void uploadProgress(Progress progress) {
                        super.uploadProgress(progress);
                        XLog.e("uploadProgress: " + progress);

                        String downloadLength = Formatter.formatFileSize(context, progress.currentSize);
                        String totalLength = Formatter.formatFileSize(context, progress.totalSize);
                        XLog.e("downloadLength:"+downloadLength + "/" + totalLength);
                        String speed = Formatter.formatFileSize(context, progress.speed);
                        XLog.e("speed:"+String.format("%s/s", speed));
//
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
                        getUiDelegate().toastShort("提交成功");
                        SharedPreferenceUtil.getInstance(context).putInt(Constants.REALNAME,1);//本地记录为已实名状态
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!hasSdcard()){
            getUiDelegate().toastShort("SD卡不可用");
            return;
        }
        if(null == data||null == data.getData()){
            return;
        }

        switch (requestCode){
            case CODE_REQUEST_CARD_FROUT://身份证正面
                paths[0] = AppKit.getfromUri2Path(context,data.getData());
                frontBitmap = PhotoUtils.getBitmapFromUri(data.getData(),context);
                getBinding().imgCardFront.setImageBitmap(frontBitmap);
                break;
            case CODE_REQUEST_CARD_RESVERSE://身份证反面
                paths[1] = AppKit.getfromUri2Path(context,data.getData());
                resevrseBitmap = PhotoUtils.getBitmapFromUri(data.getData(),context);
                getBinding().imgCardReverse.setImageBitmap(resevrseBitmap);
                break;
        }
    }

    /**
     * 自动获取sdk权限
     * 打开本地相册
     */
    private void autoObtainStoragePermissionImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            PhotoUtils.openPic(this,CODE_REQUEST );
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, CODE_REQUEST);
                } else {
                    getUiDelegate().toastShort("请允许操作SDCard！！");
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_card;
    }

}
