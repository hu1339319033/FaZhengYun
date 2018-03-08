package com.example.fazhengyun.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.fazhengyun.R;
import com.example.fazhengyun.adapter.LocalAdapter;
import com.example.fazhengyun.databinding.ActivityAudioBinding;
import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.kit.AudioRecordFunc;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.ResourceInfos;
import com.example.fazhengyun.ui.fragment.CloudFragment;
import com.example.fazhengyun.ui.fragment.LocalFragment;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.droidlover.xdroidbase.base.XFragmentAdapter;
import cn.droidlover.xdroidbase.kit.Kits;
import cn.droidlover.xdroidbase.log.XLog;

/**
 * Created by fscm-qt on 2017/12/6.
 */

public class AudioActivity extends BaseXActivity<ActivityAudioBinding> {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0x123;
    private final static int FLAG_WAV = 0;
    private final static int FLAG_AMR = 1;
    private int mState = -1;    //-1:没再录制，0：录制wav，1：录制amr
    private TextView txt;
    private UIHandler uiHandler;
    private UIThread uiThread;
    String[] titles = {"本地文件","云端文件"};
    List<Fragment> fragmentList = new ArrayList<>();
    XFragmentAdapter adapter;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;

    AudioRecordFunc audioRecordFunc;

    LocalFragment localFragment;
    CloudFragment cloudPhotoFragment;

    @Override
    public void initData(Bundle savedInstanceState) {
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getBinding().toolbar.toolbarTitle.setText("现场录音");

        localFragment = LocalFragment.newInstance(Constants.LocalType.AUDIO,handler);
        cloudPhotoFragment = CloudFragment.newInstance(Constants.LocalType.AUDIO);
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
    public void setListener() {
        super.setListener();
        getBinding().bottomBtn.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uiHandler = new UIHandler();
                txt = getBinding().tvStreamMsg;
                if(mState == -1){ //开始录制
                    getBinding().coordinatorLayout.setVisibility(View.GONE);
                    getBinding().audioRecorder.setVisibility(View.VISIBLE);
                    if ( ContextCompat.checkSelfPermission(context,
                            Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(context,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }else {
                        getUiDelegate().showProcessDialog("请稍等。。。");
                        record(FLAG_WAV);
                    }
                }else{//停止录制
                    getBinding().coordinatorLayout.setVisibility(View.VISIBLE);
                    getBinding().audioRecorder.setVisibility(View.GONE);
                    getUiDelegate().showProcessDialog("请稍等。。。");
                    stop();
                }
            }
        });
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
        getBinding().toolbar.toolbarActionLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.sendEmptyMessage(2);
                handler.sendEmptyMessage(5);
                LocalAdapter.visibility = LocalAdapter.Visibility.GONE;
                localFragment.clearTotalCount();
                localFragment.getAdapter().notifyDataSetChanged();
            }
        });
        getBinding().bottomBtn.btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.click(R.id.btn_commit);
            }
        });
        getBinding().bottomBtn.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.click(R.id.btn_delete);
            }
        });
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
     * 开始录音
     * @param mFlag，0：录制wav格式，1：录音amr格式
     */
    private void record(int mFlag){
        if(mState != -1){
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd",CMD_RECORDFAIL);
            b.putInt("msg", AudioRecordFunc.ErrorCode.E_STATE_RECODING);
            msg.setData(b);
            uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            return;
        }
        int mResult = -1;
        switch(mFlag){
            case FLAG_WAV:
                audioRecordFunc = AudioRecordFunc.getInstance();
                mResult = audioRecordFunc.startRecordAndFile();
                break;
        }
        if(mResult == AudioRecordFunc.ErrorCode.SUCCESS){
            getBinding().bottomBtn.btnStart.setText(R.string.btn_record_stop);
            getUiDelegate().dimmisProcessDiaglog();
            uiThread = new UIThread();
            new Thread(uiThread).start();
            mState = mFlag;
            uiHandler.post(runnable);
        }else{
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd",CMD_RECORDFAIL);
            b.putInt("msg", mResult);
            msg.setData(b);

            uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
        }
    }
    /**
     * 停止录音
     */
    private void stop(){
        if(mState != -1){
            getBinding().bottomBtn.btnStart.setText(R.string.btn_record_start);
            switch(mState){
                case FLAG_WAV:
                    AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
                    mRecord_1.stopRecordAndFile();
                    break;
                case FLAG_AMR:
//                    MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
//                    mRecord_2.stopRecordAndFile();
                    break;
            }
            if(uiThread != null){
                uiThread.stopThread();
            }
            if(uiHandler != null)
                uiHandler.removeCallbacks(uiThread);
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd",CMD_STOP);
            b.putInt("msg", mState);
            msg.setData(b);
            uiHandler.sendMessageDelayed(msg,1000); // 向Handler发送消息,更新UI
            mState = -1;
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            double db = audioRecordFunc.getNoiseLevel();
//            XLog.e("db:"+db);
            getBinding().voicLine.setVolume((int)70);
            uiHandler.postDelayed(this,100);
        }
    };

    private final static int CMD_RECORDING_TIME = 2000;
    private final static int CMD_RECORDFAIL = 2001;
    private final static int CMD_STOP = 2002;
    class UIHandler extends Handler{
        public UIHandler() {
        }
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("MyHandler", "handleMessage......");
            super.handleMessage(msg);
            Bundle b = msg.getData();
            int vCmd = b.getInt("cmd");
            switch(vCmd)
            {
                case CMD_RECORDING_TIME:
                    int vTime = b.getInt("msg");
                    AudioActivity.this.txt.setText("已录制："+vTime+" s");
                    break;
                case CMD_RECORDFAIL:
                    int vErrorCode = b.getInt("msg");
                    String vMsg = AudioRecordFunc.ErrorCode.getErrorInfo(AudioActivity.this, vErrorCode);
                    AudioActivity.this.txt.setText("录音失败："+vMsg);
                    break;
                case CMD_STOP:
                    int vFileType = b.getInt("msg");
                    switch(vFileType){
                        case FLAG_WAV:
                            AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
                            long mSize = mRecord_1.getRecordFileSize();
                            XLog.e("录音已停止.录音文件:"+ AudioRecordFunc.AudioFileFunc.getWavFilePath()+"\n文件大小："+mSize);
                            getUiDelegate().dimmisProcessDiaglog();
                            AudioActivity.this.txt.setText("");
                            //保存文件记录
                            List<ResourceInfos.Item> resourcesInfos = (List<ResourceInfos.Item>) SharedPreferenceUtil.getInstance(context).getObject(Constants.LOCALAUDIOLIST);
                            ResourceInfos.Item item = new ResourceInfos.Item();
                            item.setName(AudioRecordFunc.AudioFileFunc.getWavFileName());
                            item.setDatetime(Kits.Date.getYmdhms(System.currentTimeMillis()));
                            item.setFilepath(AudioRecordFunc.AudioFileFunc.getWavFilePath());
                            item.setFilesize(AppKit.GetFileSize(new File(AudioRecordFunc.AudioFileFunc.getWavFilePath())));
                            item.setState(0);
                            localFragment.getAdapter().addElement(item);
                            localFragment.getAdapter().notifyDataSetChanged();
                            //存储到本地
                            if(resourcesInfos == null){
                                resourcesInfos = new ArrayList<ResourceInfos.Item>();
                            }
                            resourcesInfos.add(item);
                            SharedPreferenceUtil.getInstance(context).putObject(Constants.LOCALAUDIOLIST,resourcesInfos);
                            uiHandler.removeCallbacks(runnable);
                            break;
//                        case FLAG_AMR:
//                            MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
//                            mSize = mRecord_2.getRecordFileSize();
//                            AudioActivity.this.txt.setText("录音已停止.录音文件:"+AudioFileFunc.getAMRFilePath()+"\n文件大小："+mSize);
//                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };
    class UIThread implements Runnable {
        int mTimeMill = 0;
        boolean vRun = true;
        public void stopThread(){
            vRun = false;
        }
        public void run() {
            while(vRun){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mTimeMill ++;
                Log.d("thread", "mThread........"+mTimeMill);
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putInt("cmd",CMD_RECORDING_TIME);
                b.putInt("msg", mTimeMill);
                msg.setData(b);

                AudioActivity.this.uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            }

        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    record(FLAG_WAV);
                } else {
                    getUiDelegate().toastShort("请允许录音操作！！");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_all:
                localFragment.showAll();
                break;
            case R.id.action_already:
                localFragment.showAlready();
                break;
            case R.id.action_not:
                localFragment.showNotAlready();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    protected void onDestroy() {
        super.onDestroy();
        stop();
        if(null == uiHandler){
            return;
        }
        if(!(null == runnable)){
            uiHandler.removeCallbacks(runnable);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_audio;
    }
}
