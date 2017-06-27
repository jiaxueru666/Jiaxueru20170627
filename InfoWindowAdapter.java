package com.example.administrator.jiaxueru20170627;

import android.view.View;

import com.amap.api.maps.model.Marker;

/**
 * date:2017/6/27 0027
 * authom:贾雪茹
 * function:
 */

public interface InfoWindowAdapter {

    View getInfoWindow(Marker marker);
    View getInfoContents(Marker marker);

}
