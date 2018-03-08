package com.example.fazhengyun.service;

import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.Event;
import com.example.fazhengyun.model.ResourceInfos;
import com.example.fazhengyun.xdroid.event.BusFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.droidlover.xdroidbase.kit.Kits;

import static com.example.fazhengyun.kit.NotifyUtil.context;

/**
 * 服务中所激活的电话状态监听器，在通话状态通过MediaRecorder录音
 */
public class MyPhoneStateListener extends PhoneStateListener {
	private String phoneNumber; // 来电号码
	private static final String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/CallRecords";
	private MediaRecorder mediaRecorder;
	private boolean isRecorder = false;
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
		Log.e("bqt", "state=" + state + "   Number=" + incomingNumber);
		this.phoneNumber = incomingNumber;

		switch (state) {
			case TelephonyManager.CALL_STATE_RINGING://响铃状态，拿到来电号码
//				phoneNumber = incomingNumber;//只有这里能拿到来电号码，在CALL_STATE_OFFHOOK状态是拿不到来电号码的
				Log.e("bqt","phoneNumber:"+phoneNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK://通话状态，开始录音
				record();
				break;
			case TelephonyManager.CALL_STATE_IDLE://空闲状态，释放资源
				if(isRecorder){
					release();
				}
				break;
		}
	}
    String fileName;
	private void record() {
		if (mediaRecorder == null) mediaRecorder = new MediaRecorder();
		if (phoneNumber == null) phoneNumber = "null_";
		File directory = new File(FILE_PATH);
		if (!directory.exists()) directory.mkdir();
		String data = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        fileName = phoneNumber+"_" + data+".mp3";
		File file = new File(FILE_PATH + fileName);
		//if (!file.exists()) file.createNewFile();

		try {
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);//指定录音机的声音源
		}catch (Exception e){
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//指定录音机的声音源
		}
		//MIC只获取自己说话的声音；VOICE_CALL双方的声音都可以录取，但是由于外国法律的限制，某些大牌手机不支持此参数
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//设置录制文件的输出格式，如AMR-NB，MPEG-4等
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//设置音频的编码，如AAC，AMR-NB等
		mediaRecorder.setOutputFile(file.getAbsolutePath());//存储路径
		try {
			mediaRecorder.prepare();//准备，一定要放在设置后、开始前，否则会产生异常
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaRecorder.start();
		isRecorder = true;
		Log.e("bqt", "开始录音！");
	}
	
	private void release() {
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			//mediaRecorder.reset(); //重设
			mediaRecorder.release();
			mediaRecorder = null;
			isRecorder = false;
		}
		Log.e("bqt", "结束录音！");

        List<ResourceInfos.Item> resourcesInfos = (List<ResourceInfos.Item>) SharedPreferenceUtil.getInstance(context).getObject(Constants.LOCALCallRECORDLIST);
        ResourceInfos.Item item = new ResourceInfos.Item();
        item.setName(fileName);
        item.setDatetime(Kits.Date.getYmdhms(System.currentTimeMillis()));
        item.setFilepath(FILE_PATH);
        item.setFilesize(AppKit.GetFileSize(new File(FILE_PATH+fileName)));
        item.setState(0);
        BusFactory.getBus().postSticky(new Event.AddAdapterItem(item));
        //存储到本地
        if(resourcesInfos == null){
            resourcesInfos = new ArrayList<ResourceInfos.Item>();
        }
        resourcesInfos.add(item);
        SharedPreferenceUtil.getInstance(context).putObject(Constants.LOCALCallRECORDLIST,resourcesInfos);
	}
}