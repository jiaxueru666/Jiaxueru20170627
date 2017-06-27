package com.example.administrator.jiaxueru20170627;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

public class MainActivity extends Activity implements LocationSource, AMapLocationListener,AMap.InfoWindowAdapter{
    MapView mMapView = null;
    private AMap mAMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        //初始化地图控制器对象
        mAMap = null;
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }

        getDingwei();
        getMar();

        // 设置定位监听
        mAMap.setLocationSource(this);
// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mAMap.setMyLocationEnabled(true);
// 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

      //  getMar();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if(null != mlocationClient) {
            mlocationClient.onDestroy();

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public void getDingwei() {
//声明定位回调监听器
         AMapLocationListener mLocationListener = new AMapLocationListener() {
             @Override
             public void onLocationChanged(AMapLocation aMapLocation) {

                 if (aMapLocation != null) {
                     if (aMapLocation.getErrorCode() == 0) {
//可在其中解析amapLocation获取相应内容。
                     }else {
                         //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                         Log.e("AmapError","location Error, ErrCode:"
                                 + aMapLocation.getErrorCode() + ", errInfo:"
                                 + aMapLocation.getErrorInfo());
                     }
                 }
             }
         };
//初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
//设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

//设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

//设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);

//给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
//启动定位
        mLocationClient.startLocation();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。

    }

    OnLocationChangedListener mListener;
    AMapLocationClient mlocationClient;
    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }
/**
 * 停止定位
 */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null&&aMapLocation != null) {
            if (aMapLocation!= null
                    &&aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    public void getMar() {
        LatLng latLng = new LatLng(39.906901,116.397972);
        final Marker marker = mAMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("DefaultMarker"));
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");

        markerOption.draggable(true);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.mipmap.ic_launcher)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果

        // 定义 Marker 点击事件监听
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {

                return true;
            }
        };
// 绑定 Marker 被点击事件
        mAMap.setOnMarkerClickListener(markerClickListener);
    }



    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
    View infoWindow = null;
    @Override
    public View getInfoContents(Marker marker) {

        if(infoWindow == null) {
            infoWindow = LayoutInflater.from(this).inflate(
                    R.layout.custom_info_window, null);
        }
        render(marker, infoWindow);
        return infoWindow;
//加载custom_info_window.xml布局文件作为InfoWindow的样式
//该布局可在官方Demo布局文件夹下找到
    }
    /**
     * 自定义infowinfow窗口
     */
    public void render(Marker marker, View view) {
//如果想修改自定义Infow中内容，请通过view找到它并修改
    }
}
