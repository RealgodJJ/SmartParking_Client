package com.example.realgodjj.parking_system;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.*;
import android.widget.*;
import com.baidu.location.*;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.*;
import com.baidu.mapapi.search.poi.*;
import com.baidu.mapapi.search.route.PlanNode;
import com.example.realgodjj.parking_system.baidu.PoiOverlay;
import com.example.realgodjj.parking_system.baidu.RoutLinePlanots;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.baidu.MapStateView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnGetPoiSearchResultListener, OnGetGeoCoderResultListener, View.OnClickListener {

    private MapView baiduMapView;
    private BaiduMap baiduMap;

    private String destination;//选择的目的地
    private GeoCoder search = null; // 搜索模块，也可去掉地图模块独立使用
    private PoiSearch poiSearch = null;// 搜索模块，用于搜索停车场

    private TextView pleaseUserChoose, point_parkingLot;
    private MapStateView location, trafficCondition, searchDestination, searchParkingLot;
    private Button sureParkingLots;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();

    //start point
    private double lat;//latitude
    private double lon;//longitude
    private LatLng currLocation, desLocation = null;

    //获取点击停车场的信息
//    private BDLocation currBDLocation;
    private PoiInfo currClickPoi[] = new PoiInfo[3];
    private int parkingLotId[] = new int[3];
    private String parkingLotUid[] = new String[3];
    private double parkingLotLatitude[] = new double[3];
    private double parkingLotLongitude[] = new double[3];
    private int i = 0;
    private int firstParkingLot = 0, secondParkingLot = 0;

    private int currClickId;
    private RoutLinePlanots routLinePlanots;
    private StringBuffer str1, str2;
    private PopupWindow optionBelow;

    private long mExitTime;//The time application exit
    private boolean isCreateMenu;
    private Menu thisMenu;
    private boolean isFirstLocation = true;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initView();
        initLocation();
        setMapStateListener();
        // 初始化搜索目的地模块，注册事件监听
        search = GeoCoder.newInstance();
        search.setOnGetGeoCodeResultListener(this);
        //初始化搜索停车场模块，注册事件监听
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(this);

        sureParkingLots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 3){
                    point_parkingLot.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(MainActivity.this, UserChooseActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("endLatitude", desLocation.latitude);
                    bundle.putDouble("endLongitude", desLocation.longitude);
                    bundle.putSerializable("parkingLotUid", parkingLotUid);
                    bundle.putSerializable("parkingLotId", parkingLotId);
                    bundle.putSerializable("parkingLotLatitude", parkingLotLatitude);
                    bundle.putSerializable("parkingLotLongitude", parkingLotLongitude);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    for(int a = 0;a < 2;a++) {
                        currClickPoi[a] = null;
                        parkingLotUid[a] = "";
                        parkingLotId[a] = -1;
                        i = 0;
                        firstParkingLot = 0;
                        secondParkingLot = 0;
                        str1 = new StringBuffer("");
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.parking_lot_not_choose, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baiduMapView.onDestroy();
        poiSearch.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        baiduMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        baiduMapView.onResume();
        if (isCreateMenu) {
            resumeMenu(thisMenu);
//            setMarker(currLocation);
        }
    }

    //Init the view
    private void initView() {
        baiduMapView = (MapView) findViewById(R.id.baidu_map);
        trafficCondition = (MapStateView) findViewById(R.id.real_time_traffic_button);
        location = (MapStateView) findViewById(R.id.location_button);
        searchDestination = (MapStateView) findViewById(R.id.search_destination_button);
        searchParkingLot = (MapStateView) findViewById(R.id.search_parkingLot_button);
        pleaseUserChoose = (TextView) findViewById(R.id.please_user_choose);
        sureParkingLots = (Button) findViewById(R.id.sure_parkingLot_button);
        point_parkingLot = (TextView) findViewById(R.id.point_parkingLot_text_view);

        baiduMap = baiduMapView.getMap();
        //Open real time traffic
        baiduMap.setTrafficEnabled(true);
        //Set my location
        baiduMap.setMyLocationEnabled(true);
        //Open Compass
        baiduMap.getUiSettings().setCompassEnabled(true);
    }

    //Init the location
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    //创建菜单选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        isCreateMenu = true;
        thisMenu = menu;
        resumeMenu(menu);
        return true;
    }

    //选择菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;

            case R.id.user_info:
                if (MyApp.isLogin()) {
                    startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    Toast.makeText(MainActivity.this, R.string.sys_no_login, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.checkout:
                checkout();
                break;

            case R.id.admin_mode:
                checkoutAdmin();


        }
        return super.onOptionsItemSelected(menuItem);
    }

    //刷新菜单
    private void resumeMenu(Menu menu) {
        if (!MyApp.isLogin() && !MyApp.isAdmin()) {
            menu.findItem(R.id.login).setVisible(true);
            menu.findItem(R.id.user_info).setVisible(false);
            menu.findItem(R.id.checkout).setVisible(false);
            menu.findItem(R.id.admin_mode).setVisible(false);
        } else if (MyApp.isLogin() && !MyApp.isSureDestination()) {
            menu.findItem(R.id.login).setVisible(false);
            menu.findItem(R.id.user_info).setVisible(true);
            menu.findItem(R.id.checkout).setVisible(true);
            menu.findItem(R.id.admin_mode).setVisible(false);
        } else if (MyApp.isLogin() && MyApp.isSureDestination()) {
            menu.findItem(R.id.login).setVisible(false);
            menu.findItem(R.id.user_info).setVisible(true);
            menu.findItem(R.id.checkout).setVisible(true);
            menu.findItem(R.id.admin_mode).setVisible(false);
        } else if (MyApp.isAdmin()) {
            menu.findItem(R.id.login).setVisible(false);
            menu.findItem(R.id.user_info).setVisible(false);
            menu.findItem(R.id.checkout).setVisible(false);
            menu.findItem(R.id.admin_mode).setVisible(true);
        }

    }

    //自定义对话框
    private void showDialogLayout(Context context) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.dialog_main, null);
        final EditText inputDestination = (EditText) view.findViewById(R.id.dialog_main_input_destination_edit_text);

        AlertDialog.Builder build = new AlertDialog.Builder(context);
        build.setIcon(R.mipmap.map_destination_48);
        build.setTitle(R.string.destination1);
        build.setCancelable(false);
        build.setView(view);
        build.setNegativeButton(R.string.search, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Geo搜索
                baiduMap.clear();
                destination = inputDestination.getText().toString().trim();
                search.geocode(new GeoCodeOption().city(String.valueOf(R.string.beijing)).address(destination));
//                MyApp.setSureDestination(isSureDestination);
                //刷新菜单
                resumeMenu(thisMenu);
                setMarker(currLocation);
            }
        });
        build.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        build.show();
    }

    //搜索周边停车场
    private void searchParkingLotsByRadius(LatLng desLocation) {
        if (MyApp.isSureDestination()) {
            poiSearch.searchNearby(new PoiNearbySearchOption()
                    .keyword("停车场")
                    .location(desLocation).pageCapacity(10).radius(10000)
                    .sortType(PoiSortType.distance_from_near_to_far));
        } else
            Toast.makeText(MainActivity.this, R.string.no_parking_lots, Toast.LENGTH_SHORT).show();
    }

    //用户注销
    private void checkout() {
        AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
        build.setIcon(R.drawable.user);
        build.setTitle(R.string.sure_checkout);
        build.setCancelable(false);
        build.setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyApp.setLogin(false);
                MyApp.setSureDestination(false);
                Toast.makeText(MainActivity.this, R.string.checkout_success, Toast.LENGTH_SHORT).show();
                //刷新菜单
                baiduMapView.onResume();
                resumeMenu(thisMenu);
                MyApp.setReceive(false);
                baiduMap.clear();
                setMarker(currLocation);
                setUserMapCenter(currLocation);
            }
        });
        build.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        build.show();
    }

    private void checkoutAdmin() {
        AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
        build.setIcon(R.drawable.administrator);
        build.setTitle(R.string.sure_checkout_admin_mode);
        build.setCancelable(false);
        build.setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyApp.setAdmin(false);
                MyApp.setSureDestination(false);
                Toast.makeText(MainActivity.this, R.string.checkout_success, Toast.LENGTH_SHORT).show();
                //刷新菜单
                baiduMapView.onResume();
                resumeMenu(thisMenu);
                MyApp.setReceive(false);
                baiduMap.clear();
                setMarker(currLocation);
                setUserMapCenter(currLocation);
            }
        });
        build.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        build.show();
    }

    //set two MapStateView Button Listener
    private void setMapStateListener() {

        location.setmOnMapStateViewClickListener(new MapStateView.OnMapStateViewClickListener() {
            @Override
            public void mapStateViewClick(int currentState) {
                if (currentState == MapStateView.MAP_STATE.NORMAL) {
                    modifyMapOverLay(currLocation, baiduMap, -60.0f);
                    MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.COMPASS;
                    baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
                    baiduMap.getUiSettings().setCompassEnabled(true);
                    location.setmCurrentState(MapStateView.MAP_STATE.STEREO);
                } else {
                    modifyMapOverLay(currLocation, baiduMap, 0);
                    MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.NORMAL;
                    baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
                    baiduMap.getUiSettings().setCompassEnabled(false);
                    location.setmCurrentState(MapStateView.MAP_STATE.NORMAL);
                }
            }
        });

        trafficCondition.setmOnMapIconAndTextClickListener(new MapStateView.OnMapIconAndTextClickListener() {
            @Override
            public void mapIconAndTextClick(int iconAndTextSate) {
                if (iconAndTextSate == MapStateView.MAP_TEXT_STATE.MAP_ICON_ON) {
                    baiduMap.setTrafficEnabled(false);
                    trafficCondition.setmCurrentIconAndTextState(MapStateView.MAP_TEXT_STATE.MAP_ICON_OFF);
                    Toast.makeText(MainActivity.this, R.string.traffic_off, Toast.LENGTH_SHORT).show();
                } else {
                    baiduMap.setTrafficEnabled(true);
                    trafficCondition.setmCurrentIconAndTextState(MapStateView.MAP_TEXT_STATE.MAP_ICON_ON);
                    Toast.makeText(MainActivity.this, R.string.traffic_on, Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchDestination.setmOnMapStateViewClickListener(new MapStateView.OnMapStateViewClickListener() {
            @Override
            public void mapStateViewClick(int currentState) {
                showDialogLayout(MainActivity.this);
//                startActivity(new Intent(MainActivity.this, SearchDestinationActivity.class));
//                finish();
            }
        });

        searchParkingLot.setmOnMapStateViewClickListener(new MapStateView.OnMapStateViewClickListener() {
            @Override
            public void mapStateViewClick(int currentState) {
                searchParkingLotsByRadius(desLocation);
            }
        });
    }


    private void modifyMapOverLay(LatLng mCurrentCenpt, BaiduMap baiduMap, float overLay) {
        MapStatus mMapStatus = new MapStatus.Builder().target(mCurrentCenpt).overlook(overLay).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        baiduMap.animateMapStatus(mMapStatusUpdate);
    }

    //Geo搜索目的地
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, R.string.no_result, Toast.LENGTH_LONG).show();
            MyApp.setSureDestination(false);
            return;
        }
        desLocation = result.getLocation();
        baiduMap.addOverlay(new MarkerOptions().position(desLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
//        Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, R.string.get_destination, Toast.LENGTH_SHORT).show();
        MyApp.setSureDestination(true);
        resumeMenu(thisMenu);
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(MainActivity.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
//            baiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(baiduMap);
            baiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(poiResult);
            overlay.addToMap();
            overlay.zoomToSpan();
            return;
        }
        if (poiResult.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : poiResult.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private class MyPoiOverlay extends PoiOverlay {

        private MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            MyApp.setCurrClickPoi(getPoiResult().getAllPoi().get(index));
            currClickId = index + 1;
            if (MyApp.isBestChoice()) {
                //TODO
                //不录入重复的parkingLotUid
//                if (i >= 1) {

//                } else {
                if (i == 3) {
                    Toast.makeText(MainActivity.this, "您已选择了三个停车场!", Toast.LENGTH_SHORT).show();
                } else {
                    if (index + 1 != secondParkingLot && index + 1 != firstParkingLot) {
                        currClickPoi[i] = getPoiResult().getAllPoi().get(index);
                        parkingLotUid[i] = currClickPoi[i].uid;
                        parkingLotId[i] = index + 1;
                        if (i == 0) {
                            firstParkingLot = index + 1;
                        } else if (i == 1) {
                            secondParkingLot = index + 1;
                        }
                        parkingLotLatitude[i] = currClickPoi[i].location.latitude;
                        parkingLotLongitude[i] = currClickPoi[i].location.longitude;
                        System.out.println("currClickPoi(" + (i + 1) + ") : " + currClickPoi[i].uid);
                        System.out.println("parkingLotId(" + (i + 1) + ") : " + parkingLotId[i]);
                        System.out.println("parkingLotLatitude(" + (i + 1) + ") : " + currClickPoi[i].location.latitude);
                        System.out.println("parkingLotLongitude(" + (i + 1) + ") : " + currClickPoi[i].location.longitude);

                        //TODO
                        if (i != 0) {
                            str2 = new StringBuffer("、" + String.valueOf(index + 1));
                            str1.append(str2);
                        } else {
                            str1 = new StringBuffer("");
                            str2 = new StringBuffer(String.valueOf(index + 1));
                            str1.append(str2);
                        }
                        point_parkingLot.setText("您已选择了" + str1 + "号停车场!");
//                    Toast.makeText(MainActivity.this, "您已选择" + (index + 1) + "号停车场!", Toast.LENGTH_SHORT).show();
                        i++;
                    } else {
                        Toast.makeText(MainActivity.this, R.string.parking_lot_repeat, Toast.LENGTH_SHORT).show();
                    }
                }
//                }
            } else if (MyApp.isLogin()) {
                showTabBelow();
            } else if (MyApp.isAdmin()) {
                showParkInfo();
            } else {
                Toast.makeText(MainActivity.this, R.string.sys_no_login, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
            return true;
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
    }

    //展示选项栏
    private void showTabBelow() {
        View belowPopupView = View.inflate(this, R.layout.route_planning_table_below, null);
        Button parkInfo = (Button) belowPopupView.findViewById(R.id.route_planning_table_below_park_info_button);
        Button bestChoice = (Button) belowPopupView.findViewById(R.id.route_planning_table_below_best_choice_button);
        Button reserve = (Button) belowPopupView.findViewById(R.id.route_planning_table_below_reserve_button);
        Button guide = (Button) belowPopupView.findViewById(R.id.route_planning_table_below_guide_button);
        parkInfo.setOnClickListener(this);
        bestChoice.setOnClickListener(this);
        reserve.setOnClickListener(this);
        guide.setOnClickListener(this);
        optionBelow = new PopupWindow(belowPopupView, DensityUtil.getScreenWidth(this), (int) (DensityUtil.getScreenHeight(this) * 0.4f));
        optionBelow.setFocusable(true);
        optionBelow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dfdfdf")));
        optionBelow.setAnimationStyle(R.style.bottom_popup_anim);//显示和隐藏弹窗
        optionBelow.showAtLocation(baiduMapView, Gravity.BOTTOM, 0, 0);
    }

    private void showParkInfo() {
        View belowPopupView = View.inflate(this, R.layout.parkinfo_table_below, null);
        Button parkInfo = (Button) belowPopupView.findViewById(R.id.parkinfo_table_below_park_info_button);
        parkInfo.setOnClickListener(this);
        optionBelow = new PopupWindow(belowPopupView, DensityUtil.getScreenWidth(this), (int) (DensityUtil.getScreenHeight(this) * 0.13f));
        optionBelow.setFocusable(true);
        optionBelow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dfdfdf")));
        optionBelow.setAnimationStyle(R.style.bottom_popup_anim);//显示和隐藏弹窗
        optionBelow.showAtLocation(baiduMapView, Gravity.BOTTOM, 0, 0);
    }

    //点击弹出框按钮事件
    @Override
    public void onClick(View view) {
        optionBelow.dismiss();
        MyApp.setCurrentParkId(currClickId);
        switch (view.getId()) {
            case R.id.route_planning_table_below_park_info_button:
                parkInfo();
                break;
            case R.id.route_planning_table_below_best_choice_button:
                bestChoice();
                break;
            case R.id.route_planning_table_below_reserve_button:
                reserveSpace();
                break;
            case R.id.route_planning_table_below_guide_button:
                drivingRoute();
                break;
            case R.id.parkinfo_table_below_park_info_button:
                adminParkInfo();
            default:
                break;
        }
    }

    //设定导航的起点和终点
    @NonNull
    private RoutLinePlanots setPlanningRoad() {
        routLinePlanots = new RoutLinePlanots();
        PlanNode startNode = PlanNode.withLocation(new LatLng(MyApp.getCurrBDLocation().getLatitude(), MyApp.getCurrBDLocation().getLongitude()));
        PlanNode targetNode = PlanNode.withLocation(new LatLng(MyApp.getCurrClickPoi().location.latitude, MyApp.getCurrClickPoi().location.longitude));
        routLinePlanots.setStartPlanNode(startNode);
        routLinePlanots.setTargetPlanNode(targetNode);
        return routLinePlanots;
    }

    //停车场信息
    private void parkInfo() {
        Intent intent = new Intent(MainActivity.this, ParkInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("parkingLotUid", MyApp.getCurrClickPoi().uid);
        bundle.putString("parkingLotName", MyApp.getCurrClickPoi().name);
        bundle.putString("parkingLotAddress", MyApp.getCurrClickPoi().address);
        bundle.putString("parkingLotLatitude", String.valueOf(MyApp.getCurrClickPoi().location.latitude));
        bundle.putString("parkingLotLongitude", String.valueOf(MyApp.getCurrClickPoi().location.longitude));
        bundle.putString("destination", destination);
        intent.putExtras(bundle);
//        System.out.println(currClickPoi.name + currClickPoi.address + "\n" +
//                String.valueOf(currClickPoi.location.latitude) + "\n" +
//                String.valueOf(currClickPoi.location.longitude) + "\n" +
//                destination);
        MyApp.setIntent(true);
        startActivity(intent);
    }

    //最佳预估
    private void bestChoice() {
//        MyApp.setBestChoice(true);
        pleaseUserChoose.setVisibility(View.VISIBLE);
        sureParkingLots.setVisibility(View.VISIBLE);
        point_parkingLot.setVisibility(View.VISIBLE);
        point_parkingLot.setText("");
        MyApp.setBestChoice(true);
    }

    //停车场的预订
    private void reserveSpace() {
        Intent intent = new Intent(MainActivity.this, ReserveActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("parkingLotUid", MyApp.getCurrClickPoi().uid);
        bundle.putString("parkingLotName", MyApp.getCurrClickPoi().name);
        bundle.putString("parkingLotAddress", MyApp.getCurrClickPoi().address);

        routLinePlanots = setPlanningRoad();
        bundle.putParcelable(RoutePlanningActivity.ROUTE_PLANNING, routLinePlanots);

        intent.putExtras(bundle);
        MyApp.setIntent(true);
        startActivity(intent);
    }

    //驾车路线规划(起点和终点的信息传递给RoutePlanningActivity)
    private void drivingRoute() {
        routLinePlanots = setPlanningRoad();
        Intent intent = new Intent(MainActivity.this, RoutePlanningActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("parkingLotUid", MyApp.getCurrClickPoi().uid);
        bundle.putString("parkingLotName", MyApp.getCurrClickPoi().name);
        bundle.putString("parkingLotAddress", MyApp.getCurrClickPoi().address);
        bundle.putParcelable(RoutePlanningActivity.ROUTE_PLANNING, routLinePlanots);
        intent.putExtras(bundle);
        MyApp.setIntent(true);
        startActivity(intent);
    }

    private void adminParkInfo() {
        Intent intent = new Intent(MainActivity.this, AdminParkInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("parkingLotUid", MyApp.getCurrClickPoi().uid);
        bundle.putString("parkingLotName", MyApp.getCurrClickPoi().name);
        bundle.putString("parkingLotAddress", MyApp.getCurrClickPoi().address);
        bundle.putString("destination", destination);
        intent.putExtras(bundle);
//        System.out.println(currClickPoi.name + currClickPoi.address + "\n" +
//                String.valueOf(currClickPoi.location.latitude) + "\n" +
//                String.valueOf(currClickPoi.location.longitude) + "\n" +
//                destination);
        MyApp.setIntent(true);
        startActivity(intent);
    }

    /**
     * 添加marker
     */
    private void setMarker(LatLng point) {
        Log.v("pcw", "setMarker : lat : " + lat + " lon : " + lon);
        //定义Maker坐标点
        //LatLng point = new LatLng(lat, lon);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.start);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);
    }

    /**
     * 设置中心点
     */
    private void setUserMapCenter(LatLng centerPoint) {
        Log.v("pcw", "setUserMapCenter : lat : " + lat + " lon : " + lon);
        //LatLng cenpt = new LatLng(lat, lon);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(centerPoint).zoom(18).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);

    }

    /**
     * 实现定位监听 位置一旦有所改变就会调用这个方法
     * 可以在这个方法里面获取到定位之后获取到的一系列数据
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
//            currBDLocation = location;
            MyApp.setCurrBDLocation(location);
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            lat = location.getLatitude();
            lon = location.getLongitude();
            currLocation = new LatLng(lat, lon);
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocation_describe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            if (isFirstLocation) {
                isFirstLocation = false;
                setMarker(currLocation);
                setUserMapCenter(currLocation);
            }
//            Log.v("pcw", "lat : " + lat + " lon : " + lon);
//            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }

    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (MyApp.isBestChoice()) {
                pleaseUserChoose.setVisibility(View.INVISIBLE);
                sureParkingLots.setVisibility(View.INVISIBLE);
                point_parkingLot.setVisibility(View.INVISIBLE);
                for(int a = 0;a < 2;a++) {
                    currClickPoi[a] = null;
                    parkingLotUid[a] = "";
                    parkingLotId[a] = -1;
                    i = 0;
                    firstParkingLot = 0;
                    secondParkingLot = 0;
                    str1 = new StringBuffer("");
                }
                MyApp.setBestChoice(false);
            } else {
                exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, R.string.exit, Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

}
