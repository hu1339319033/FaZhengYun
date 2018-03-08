package com.example.fazhengyun.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityVideoplayBinding;
import com.example.fazhengyun.xdroid.base.XActivity;

/**
 * Created by fscm-qt on 2017/12/15.
 */

public class VideoPlayActivity extends XActivity<ActivityVideoplayBinding> {
    protected static final String TAG = "VideoActivity";
    private VideoView video_view;
    private Uri uri = null;
    private LinearLayout mLinearLayout;
    private VideoView mVideoView;
    @Override
    public void initData(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        String uriStr = getIntent().getStringExtra("url");
        uri = Uri.parse(uriStr);
        System.out.println("uri=" + uri);

        mLinearLayout = (LinearLayout)findViewById(R.id.player_loading);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        if (uri != null) {

            if (mVideoView != null) {
                //加载播放地址
                mVideoView.setVideoURI(uri);
            }
        }
        if (mVideoView != null)
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mVideoView != null){
                        //开始播放
                        mVideoView.start();
                        mLinearLayout.setVisibility(View.GONE);
                    }
                }
            });
        if (mVideoView != null){
            //显示控制栏
            mVideoView.setMediaController(new MediaController(this));
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_videoplay;
    }
}
