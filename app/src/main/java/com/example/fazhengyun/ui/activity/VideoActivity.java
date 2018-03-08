package com.example.fazhengyun.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.fazhengyun.R;
import com.example.fazhengyun.adapter.LocalAdapter;
import com.example.fazhengyun.databinding.ActivityVideoBinding;
import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.kit.PhotoUtils;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.Event;
import com.example.fazhengyun.model.ResourceInfos;
import com.example.fazhengyun.ui.fragment.CloudFragment;
import com.example.fazhengyun.ui.fragment.LocalFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.droidlover.xdroidbase.base.XFragmentAdapter;
import cn.droidlover.xdroidbase.kit.Kits;
import cn.droidlover.xdroidbase.log.XLog;

/**
 * 录像存证
 * Created by fscm-qt on 2017/11/28.
 */

public class VideoActivity extends BaseXActivity<ActivityVideoBinding> implements View.OnClickListener {

    String[] titles = {"本地文件","云端文件"};
    List<Fragment> fragmentList = new ArrayList<>();
    XFragmentAdapter adapter;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private String fileUri =Environment.getExternalStorageDirectory().getPath()+"/Video";
    private String fileName = "video.mp4";
    private Uri imageUri;



    LocalFragment localFragment;
    CloudFragment cloudPhotoFragment;

    @Override
    public void initData(Bundle savedInstanceState) {

        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getBinding().toolbar.toolbarTitle.setText("录像存证");
        getBinding().bottomBtn.btnStart.setText("按下开始录像");

        localFragment = LocalFragment.newInstance(Constants.LocalType.VIDEO,handler);
        cloudPhotoFragment = CloudFragment.newInstance(Constants.LocalType.VIDEO);
        fragmentList.clear();
        fragmentList.add(localFragment);
        fragmentList.add(cloudPhotoFragment);

        if(adapter == null){
            adapter = new XFragmentAdapter(getSupportFragmentManager(),fragmentList,titles);
        }
        getBinding().viewPager.setAdapter(adapter);
        getBinding().tabLayout.setupWithViewPager(getBinding().viewPager);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (getBinding().viewPager.getCurrentItem()){
            case 0:
                menu.findItem(R.id.action_all).setVisible(true);
                menu.findItem(R.id.action_already).setVisible(true);
                menu.findItem(R.id.action_not).setVisible(true);
                break;
            case 1:
                menu.findItem(R.id.action_all).setVisible(false);
                menu.findItem(R.id.action_already).setVisible(false);
                menu.findItem(R.id.action_not).setVisible(false);
                break;

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setIconsVisible(menu,true);
        getMenuInflater().inflate(R.menu.menu_screen,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 解决menu不显示图标问题
     * @param menu
     * @param flag
     */
    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if(menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_all:
                localFragment.showAll();
                break;
            case R.id.action_already:
                localFragment.showAlready(); //有bug
                break;
            case R.id.action_not:
                localFragment.showNotAlready();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setListener() {
        super.setListener();
        getBinding().viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        getBinding().bottomBtn.btnStart.setOnClickListener(this);
        getBinding().toolbar.toolbarActionLeft.setOnClickListener(this);
        getBinding().bottomBtn.btnDelete.setOnClickListener(this);
        getBinding().bottomBtn.btnCommit.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                autoObtainCameraPermission();
                break;
            case R.id.toolbar_action_left:
                handler.sendEmptyMessage(2);
                handler.sendEmptyMessage(5);
                LocalAdapter.visibility = LocalAdapter.Visibility.GONE;
                localFragment.clearTotalCount();
                localFragment.getAdapter().notifyDataSetChanged();
                break;
            case R.id.btn_delete:
                clickListener.click(R.id.btn_delete);
                break;
            case R.id.btn_commit:
                clickListener.click(R.id.btn_commit);
                break;
        }
    }

    /**
     * 自动获取相机权限
     */
    private void autoObtainCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                getUiDelegate().toastShort("您已经拒绝过一次");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {
                if(!Kits.File.isFolderExist(fileUri)){
                    Kits.File.makeFolders(fileUri);
                }
                fileName = "VIDEO-"+AppKit.getYmdhmsString(System.currentTimeMillis())+".mp4";
                imageUri = Uri.fromFile(new File(fileUri+fileName));
                XLog.e("imageUri:"+imageUri.getPath());
                //通过FileProvider创建一个content类型的Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(VideoActivity.this, "com.example.fazhengyun.fileprovider", new File(fileUri+fileName));
                }
                PhotoUtils.takeVideo(this, imageUri, CODE_CAMERA_REQUEST);
            } else {
                getUiDelegate().toastShort("SD卡不可用");
            }
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1://设置标题
                    getBinding().toolbar.toolbarTitle.setText(msg.obj.toString());
                    break;
                case 2:
                    setBtnVisibity();
                    break;
                case 3:
                    setBtnGone();
                    break;
                case 4://左上角action显示
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getBinding().toolbar.toolbarActionLeft.setVisibility(View.VISIBLE);
                    break;
                case 5://左上角action隐藏
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getBinding().toolbar.toolbarActionLeft.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private static final int OUTPUT_X = 480;
    private static final int OUTPUT_Y = 480;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //拍照完成回调
                case CODE_CAMERA_REQUEST:
                    List<ResourceInfos.Item> resourcesInfos = (List<ResourceInfos.Item>) SharedPreferenceUtil.getInstance(context).getObject(Constants.LOCALVIDEOLIST);
                    ResourceInfos.Item item = new ResourceInfos.Item();
                    item.setName(fileName);
                    item.setDatetime(Kits.Date.getYmdhms(System.currentTimeMillis()));
                    item.setFilepath(fileUri);
                    item.setFilesize(AppKit.GetFileSize(new File(fileUri+fileName)));
                    item.setState(0);
                    localFragment.getAdapter().addElement(item);
                    localFragment.getAdapter().notifyDataSetChanged();
                    //存储到本地
                    if(resourcesInfos == null){
                        resourcesInfos = new ArrayList<ResourceInfos.Item>();
                    }
                    resourcesInfos.add(item);
                    SharedPreferenceUtil.getInstance(context).putObject(Constants.LOCALVIDEOLIST,resourcesInfos);
                    break;
                default:
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            //调用系统相机申请拍照权限回调
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        if(!Kits.File.isFolderExist(fileUri)){
                            Kits.File.makeFolders(fileUri);
                        }
                        fileName = "VIDEO-"+AppKit.getYmdhmsString(System.currentTimeMillis())+".mp4";
                        imageUri = Uri.fromFile(new File(fileUri+fileName));
                        XLog.e("imageUri:"+imageUri.getPath());
                        //通过FileProvider创建一个content类型的Uri
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            imageUri = FileProvider.getUriForFile(VideoActivity.this, "com.example.fazhengyun.fileprovider", new File(fileUri+fileName));
                        }
                        PhotoUtils.takeVideo(this, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        getUiDelegate().toastShort("SD卡不可用");
                    }
                } else {

                    getUiDelegate().toastShort("请允许打开相机！！");
                }
                break;


            }
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
                } else {
                    getUiDelegate().toastShort("请允许打操作SDCard！！");
                }
                break;
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.TestEvent event) {
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    /**
     * 设置底部拍照按钮显示
     */
    public void setBtnVisibity(){
        getBinding().bottomBtn.btnStart.setVisibility(View.VISIBLE);
        getBinding().bottomBtn.linDeleteAndcun.setVisibility(View.GONE);
    }
    /**
     * 设置底部拍照按钮隐藏
     */
    public void setBtnGone(){
        getBinding().bottomBtn.btnStart.setVisibility(View.GONE);
        getBinding().bottomBtn.linDeleteAndcun.setVisibility(View.VISIBLE);
    }
    /**
     * 自动获取sdk权限
     * 打开本地相册
     */

    private void autoObtainStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
        }

    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public void setTooobarlTitle(CharSequence tooobarlTitle){
        getBinding().toolbar.toolbarTitle.setText(tooobarlTitle);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.sendEmptyMessage(2);
        handler.sendEmptyMessage(5);
        LocalAdapter.visibility = LocalAdapter.Visibility.GONE;
        localFragment.clearTotalCount();
        localFragment.getAdapter().notifyDataSetChanged();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video;
    }

    private static VideoActivity instance;

    public synchronized static VideoActivity getInstance(){
        if(instance == null)
            instance = new VideoActivity();
        return instance;
    }
}

