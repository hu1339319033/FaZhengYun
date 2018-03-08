package com.example.fazhengyun.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityBaidumapBinding;
import com.example.fazhengyun.model.GPSInfo;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.xdroid.base.XActivity;

import java.util.HashMap;

import cn.droidlover.xdroidbase.router.Router;
import okhttp3.Call;

/**
 * Created by fscm-qt on 2017/12/17.
 */

public class LocationMapActivity extends XActivity<ActivityBaidumapBinding> {

    private MapView mapView=null;
    BaiduMap mBaiduMap=null;
    private InfoWindow mInfoWindow;
    Marker marker;

    @Override
    public void initData(Bundle savedInstanceState) {
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getBinding().toolbar.toolbarTitle.setText("位置信息");

        mapView = getBinding().mapview;

        getBinding().imgDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router.newIntent(context).to(LookSwitchActivity.class).putString("gpsInfoId",getIntent().getStringExtra("gpsInfoId")).launch();
            }
        });

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("gpsInfoId",getIntent().getStringExtra("gpsInfoId"));
        NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_GetGPSAddr), hashMap, new JsonCallback<GPSInfo>() {
            @Override
            public void onFail(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(GPSInfo response, int id) {
                setbaiduMap(response.getGps_latitude(),response.getGps_dimension(),response.getGps_addr());
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_baidumap;
    }

    /**
     * 标注地点坐标的方法
     * @param longitude	经度
     * @param latitude	维度
     * @param addr	详细地址
     */
    private void setbaiduMap(double longitude,double latitude,String addr){
        //百度基础地图显示部分代码开始--
        if(mapView == null){
//            mapView = (MapView) findViewById(R.id.bmapview);
        }
        mBaiduMap=mapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //卫星地图
        //				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        //开启交通图
        mBaiduMap.setTrafficEnabled(false);

        //定义Maker坐标点
        LatLng point = new LatLng(longitude, latitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_marka);

        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)  //设置marker所在层级
                .draggable(true);  //设置手势拖拽;
        //在地图上添加Marker，并显示
        marker = (Marker) mBaiduMap.addOverlay(option);
        marker.setTitle(addr);
        //		//设置地图缩放等级
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().target(point).zoom(13).build()));
        //调用BaiduMap对象的setOnMarkerDragListener方法设置marker拖拽的监听
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
                //拖拽中
            }
            public void onMarkerDragEnd(Marker marker) {
                //拖拽结束
            }
            public void onMarkerDragStart(Marker marker) {
                //开始拖拽

            }
        });
        showLocation(marker);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() { //点击覆盖物事件

            @Override
            public boolean onMarkerClick(Marker arg0) {
                showLocation(arg0);
                return false;
            }
        });
        //百度地图显示部分代码结束--
    }

    //在百度地图上显示气泡的方法
    private void showLocation(final Marker marker) {
        // 创建InfoWindow展示的view

        LatLng pt = null;
        double latitude, longitude;
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        View view = LayoutInflater.from(this).inflate(R.layout.map_item, null); //自定义气泡形状
        TextView textView = (TextView) view.findViewById(R.id.my_postion);
        pt = new LatLng(latitude + 0.0004, longitude + 0.00005);
        textView.setText(marker.getTitle());

        // 定义用于显示该InfoWindow的坐标点
        // 创建InfoWindow的点击事件监听者
        InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
            public void onInfoWindowClick() {
                mBaiduMap.hideInfoWindow();//影藏气泡
            }
        };
        // 创建InfoWindow
        mInfoWindow = new InfoWindow(view, pt, -47);
        mBaiduMap.showInfoWindow(mInfoWindow); //显示气泡

    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        mapView.onDestroy();
    }
}
