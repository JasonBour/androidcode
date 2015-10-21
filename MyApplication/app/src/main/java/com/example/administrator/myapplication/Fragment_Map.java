package com.example.administrator.myapplication;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.github.mrengineer13.snackbar.SnackBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/6.
 */
public class Fragment_Map extends Fragment implements LocationSource,
        AMapLocationListener, RadioGroup.OnCheckedChangeListener, AMap.InfoWindowAdapter, AMap.OnInfoWindowClickListener, AMap.OnMarkerClickListener, SwipeRefreshLayout.OnRefreshListener {
    public String path = "http://1.universities.sinaapp.com/application.php"; //服务器位置
    public URL url ;
    public HttpURLConnection httpsURLConnection ;
    public  String Stringdata = "";

    public Handler handler ;
    public SharedPreferences sharedPreferences ;
    public  SharedPreferences.Editor editor ;
    boolean expand_status  = false ;
    public View view  ;
    public ImageView image ;
    private LatLng person1  = new LatLng(30.421321 ,114.224161);
    private ListView list ;
    private map_adapter adapter ;
    private  List<Map<String,Object>> data ;
    public final List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();
    private Marker person ;
    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    public SwipeRefreshLayout swipeRefreshLayout ;



    private String latitude = "30.421321" ;  //用户的纬度
    private String longitude = "114.224161";//用户的经度
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_map, container, false);


        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_swipe_ly);
        swipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light,   android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        sharedPreferences =getContext().getSharedPreferences("user_state", getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();
        initView();



    }


    final Thread thread = new Thread(new Runnable(){

        @Override
        public void run() {

            try {
                url = new URL(path);

                httpsURLConnection = (HttpURLConnection)url.openConnection();
                InputStreamReader inputStreamReader = new InputStreamReader(httpsURLConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(inputStreamReader);
                String readLine = null ;
                while((readLine=buffer.readLine())!=null){
                    Stringdata += readLine ;

                }
                inputStreamReader.close();
                httpsURLConnection.disconnect();
            } catch (IOException e) {
                Log.i("jason", "something is wrong");
                e.printStackTrace();
            }

            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }});
    public void initView(){



        View expan_map = getActivity().getLayoutInflater().inflate(R.layout.expand_map, null);

        mapView.addView(expan_map);
        image = (ImageView)view.findViewById(R.id.expand);
        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!expand_status) {
                    mapView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f));
                    expand_status = !expand_status;
                } else {
                    mapView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
                    expand_status = !expand_status;
                }


            }


        });



        list = (ListView)view.findViewById(R.id.list);
        getInfoAround();

       // list.setAdapter(adapter);
        //点击不同的item 应该能够定位到地图上
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Snackbar.make(view,"Do you want add him as a friend?",Snackbar.LENGTH_INDEFINITE).show();
          new SnackBar.Builder(getActivity())
        .withOnClickListener(new SnackBar.OnMessageClickListener() {

            /**
             * snackbar的点击监听事件
             * @param parcelable
             */
            @Override
            public void onMessageClick(Parcelable parcelable) {

            }
        })
        .withMessage("Do you want to contact with him?")
        .withDuration(SnackBar.LONG_SNACK)
        .withActionMessage("Add")
                  .withStyle(SnackBar.Style.CONFIRM)
        .withTextColorId(R.color.colorAccent).show();

            }
        });









        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }



    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //我的位置的图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_position));
        myLocationStyle.strokeColor(Color.WHITE);
        myLocationStyle.strokeWidth(5);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        person = aMap.addMarker(new MarkerOptions()
                        .position(person1)
                        .title("单初想吃什么水果呢？")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_room_grey600_48dp))
                //.perspective(true)
                //.draggable(true)
        );

        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);


    }



    /**
     * 以下方法必须重写
     */


    @Override
    public void onPause(){
        mapView.setVisibility(View.INVISIBLE);
        mapView.onPause();
        super.onPause();
        deactivate();
    }



    @Override
    public void onResume(){
        mapView.setVisibility(View.VISIBLE);
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    /**
     * 此方法已经废弃
     */

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        //Toast.makeText(getApplicationContext(), aMap.getMyLocation().getLatitude()+"all"+aMap.getMyLocation().getLongitude(), Toast.LENGTH_LONG).show();
        //在这里加东西会导致不能定位
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getAMapException().getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                Log.e("AmapErr", "Location ERR:" + amapLocation.getAMapException().getErrorCode());
            }
        }
         latitude = aMap.getMyLocation().getLatitude()+"";
        longitude = aMap.getMyLocation().getLongitude() + "";
        //	Toast.makeText(getApplicationContext(), amapLocation.getLatitude()+"all"+amapLocation.getLongitude(), Toast.LENGTH_LONG).show();
        //getInfoAround();
    }

    /** 提交我的位置信息  通过sharedPreferences保存在本地
     * 向服务器请求获得周围一定范围内其他人的需求信息
     * 并且获取他们距离我的数据
     */
    private void getInfoAround() {
              /**
                editor.putString("latitude",latitude);
                editor.putString("latitude", longitude);
                editor.commit();
               **/
        thread.start() ;

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {

                    try {


                        //json解析 // 数据形式：{"data":[{"id":1,"name":"张三"},{"id":2,"name":"李斯"}]}
                        JSONObject jsonObject = new JSONObject(Stringdata);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            final  Map<String ,Object> map =new HashMap<String ,Object>();
                            JSONObject item = (JSONObject)jsonArray.get(i);
                            map.put("name", item.getString("name"));
                            map.put("ImageUrl", item.getString("ImageUrl"));
                            map.put("content", item.getString("content"));
                            map.put("distance", item.getString("distance"));
                            map.put("time", item.getString("time"));
                            Log.i("jason", item.getString("name"));
                            datalist.add(map);
                        }
                        adapter = new map_adapter(getContext(), datalist);
                        list.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.e("jason", Log.getStackTraceString(e));
                    }
                }

            }
        };




    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用destroy()方法
            // 其中如果间隔时间为-1，则定位只定一次
            // 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
    }







    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }





    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }





    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }




    /**
     * 显示特定信息  可以通过inflate  xml文件
     */
    @Override
    public View getInfoWindow(Marker arg0) {
        View view  = getActivity().getLayoutInflater().inflate(R.layout.marker, null);
        ImageView image = (ImageView)view.findViewById(R.id.image);
        TextView info = (TextView)view.findViewById(R.id.info);

        info.setText(person.getTitle());


        return view;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    /**
     * 下拉刷新事件
     * 应该是再次请求服务器 获取最新数据
     */
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 6000);
    }

}