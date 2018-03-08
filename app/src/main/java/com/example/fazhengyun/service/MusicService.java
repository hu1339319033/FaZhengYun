package com.example.fazhengyun.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.fazhengyun.kit.ToastShow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

	public final IBinder binder = new MyBinder();
	ToastShow toastShow;
	public class MyBinder extends Binder{
		public MusicService getService() {
			return MusicService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public static List<String> musicDir=new ArrayList<String>();
	public static int musicIndex = 0;

	public static MediaPlayer mp = new MediaPlayer();
	public MusicService() {
		toastShow=new ToastShow(MusicService.this);
		if(musicDir.size()>0){
			mp.reset();
			try {
				musicIndex = 0;
				mp.setDataSource(musicDir.get(musicIndex));
				mp.prepare();
				
			} catch (Exception e) {
				Log.d("hint","can't get to the song");
				e.printStackTrace();
			}
		}
	}

	public String playOrPause() {
		if(mp.isPlaying()){
			mp.pause();
			return "播放";
		} else {
			if(musicDir.size()>0&&musicIndex<musicDir.size()){
				mp.reset();
				try {
					mp.setDataSource(musicDir.get(musicIndex));
					mp.prepare();
					mp.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				toastShow.toastShowByShort("第"+(musicIndex+1)+"段");
				return "暂停";	
			}else{
				toastShow.toastShowByShort("没有可播放的音频");
				return "播放";
			}
			
		}
		
	}
	public void stop() {
		if(mp != null) {
			mp.stop();
			try {
				mp.prepare();
				mp.seekTo(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public String nextMusic() {
		if(mp != null && musicIndex >= musicDir.size()-1){
			toastShow.toastShowByShort("没有下一段了");
			if(mp.isPlaying()){
				return "暂停";
			}else{
				return "播放";
			}
			
		}else if(mp != null && musicIndex < musicDir.size()-1){
			mp.stop();
			try {
				mp.reset();
				mp.setDataSource(musicDir.get(musicIndex+1));
				musicIndex++;
				mp.prepare();
				mp.seekTo(0);
				mp.start();
//				toastShow.toastShowByShort("第"+(musicIndex+1)+"段");
				return "暂停";
			} catch (Exception e) {
				Log.d("hint", "can't jump next music");
				toastShow.toastShowByShort("没有下一段了");
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String preMusic() {
		if(mp != null && musicIndex > 0) {
			mp.stop();
			try {
				mp.reset();
				mp.setDataSource(musicDir.get(musicIndex-1));
				musicIndex--;
				mp.prepare();
				mp.seekTo(0);
				mp.start();
//				toastShow.toastShowByShort("第"+(musicIndex+1)+"段");
				return "暂停";
			} catch (Exception e) {
				Log.d("hint", "can't jump pre music");
				toastShow.toastShowByShort("没有上一段了");
				e.printStackTrace();
			}
		}else{
			toastShow.toastShowByShort("没有上一段了");
			if(mp.isPlaying()){
				return "暂停";
			}else{
				return "播放";
			}
		}
		return null;
	}
	
	public String PlaySelectMusic(int index) {
		if(mp != null){
			if(musicDir.size()>0&&musicIndex<musicDir.size()){
				mp.stop();
				mp.reset();
				try {
					mp.setDataSource(musicDir.get(index));
					musicIndex=index;
					mp.prepare();
					mp.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				toastShow.toastShowByShort("第"+(index+1)+"段");
				return "暂停";	
			}
		}
		return null;
	}
}
