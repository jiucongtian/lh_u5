package com.longhuapuxin.u5;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

public class MapActivity extends BaseActivity implements BaiduMap.OnMapLoadedCallback {
    MapView mMapView = null;
    BaiduMap mBaiduMap=null;
    MapStatus ms;
    String address,name;
    double longitude,latitude;
    InfoWindow mInfoWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();

          address=intent.getStringExtra("Address");
          name=intent.getStringExtra("ShopName");
        longitude=intent.getDoubleExtra("Longitude", 0);
        latitude=intent.getDoubleExtra("Latitude", 0);


        setContentView(R.layout.activity_map);
        initHeader(name);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setOnMapLoadedCallback(this);
        LatLng  ll = new LatLng(latitude,longitude);
        ms = new MapStatus.Builder()
        .target(ll).zoom(16.0f).build();


        BitmapDescriptor bd1 = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(ll).icon(bd1);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);//添加当前分店信息图层
        showPop();
    }
    public void showPop() {
        // 创建InfoWindow展示的view
        View popup = View.inflate(this, R.layout.pop, null);
        TextView title = (TextView) popup.findViewById(R.id.tv_title);
        TextView content = (TextView) popup.findViewById(R.id.tv_content);
        title.setText(name);
        content.setText(address);

        // 定义用于显示该InfoWindow的坐标点
        LatLng pt = new LatLng(latitude, longitude);
       // Point p = mBaiduMap.getProjection().toScreenLocation(pt);
        // 创建InfoWindow的点击事件监听者
        InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
            public void onInfoWindowClick() {
                // 添加点击后的事件响应代码
                Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        };
        // 创建InfoWindow
        mInfoWindow = new InfoWindow(popup, pt,0);


        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mBaiduMap.hideInfoWindow();
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 显示InfoWindow
                mBaiduMap.showInfoWindow(mInfoWindow);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onMapLoaded() {
        // TODO Auto-generated method stub

        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }
}

