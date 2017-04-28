package com.example.realgodjj.parking_system;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.*;
import com.example.realgodjj.parking_system.baidu.DrivingRouteOverlay;
import com.example.realgodjj.parking_system.baidu.RoutLinePlanots;
import com.example.realgodjj.parking_system.client.MyApp;

import java.math.BigDecimal;
import java.util.*;

public class RoutePlanningActivity extends AppCompatActivity {

    private BaiduMap baiduMap;
    private MapView baiduMapView;
    private RoutePlanSearch routePlanSearch;
    private RoutLinePlanots routLinePlanots;
    private List<String> drivingStepsList;
    private ArrayAdapter<String> arrayAdapter;
    public static final String ROUTE_PLANNING = "route_planning";
    private String isSuccess = "";
    private String totalHour, totalMinute, parkFee;
    private String parkingLotName, parkingLotAddress, parkingLotUid;
    private int duration;
    private double distance;
    private Timer timer;
    private TimerTask timerTask_synchronize;
    private static final int COUNTING_TIME = 1;
    private int reserveHour, reserveMinute;
    private int count = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_route_planning);
        initView();
        getIntentInfo();//获取起点和终点的信息
        //关闭百度地图大小调节
        baiduMapView.showZoomControls(false);
        baiduMapView.showScaleControl(false);
        MapStatus mMapStatus = new MapStatus.Builder().zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        baiduMap.animateMapStatus(mMapStatusUpdate);

        timer = new Timer();
        //如果预订车位,车位会为用户保留一定时间
        if (MyApp.isReserve()) {
            synchronize();
        }
    }

    //同步时间
    private void synchronize() {
        timerTask_synchronize = new TimerTask() {
            @Override
            public void run() {
                try {
//                    System.out.println("Reserve Timing..........................." + count);
                    Message message = new Message();
                    message.what = COUNTING_TIME;
                    handler.sendMessage(message);

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(timerTask_synchronize, 0, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routeplanning, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_parking) {
            AlertDialog.Builder build = new AlertDialog.Builder(RoutePlanningActivity.this);
            build.setTitle(R.string.sure_arrive_destination);
            build.setCancelable(false);
            build.setIcon(R.drawable.icon_arrive);
            build.setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (MyApp.isReserve()) {
                        timerTask_synchronize.cancel();
                        timer.cancel();
                        Intent intent = new Intent(RoutePlanningActivity.this, TimingActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("totalHour", totalHour);
                        bundle.putString("totalMinute", totalMinute);
                        bundle.putString("parkFee", parkFee);
                        bundle.putString("parkingLotUid", parkingLotUid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(RoutePlanningActivity.this, SetParkingActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("parkingLotUid", parkingLotUid);
                        bundle.putString("parkingLotName", parkingLotName);
                        bundle.putString("parkingLotAddress", parkingLotAddress);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                }
            });
            build.setPositiveButton(R.string.sure_not_arrive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            build.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        baiduMapView = (MapView) findViewById(R.id.baidu_map);
        baiduMap = baiduMapView.getMap();
        //Open real time traffic
        baiduMap.setTrafficEnabled(true);
        //Set my location
        baiduMap.setMyLocationEnabled(true);
        //Open Compass
        baiduMap.getUiSettings().setCompassEnabled(false);
        baiduMap = baiduMapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setTrafficEnabled(true);
        baiduMap.getUiSettings().setCompassEnabled(true);
    }

    //获取停车规划路沿线所需信息
    private void getIntentInfo() {
        if (MyApp.isReserve()) {
            Intent intent = getIntent();
            Bundle bundle = this.getIntent().getExtras();
            totalHour = bundle.getString("totalHour");
            totalMinute = bundle.getString("totalMinute");
            parkFee = bundle.getString("parkFee");
            parkingLotUid = bundle.getString("parkingLotUid");
            routLinePlanots = intent.getParcelableExtra(ROUTE_PLANNING);
        } else {
            Intent intent = getIntent();
            Bundle bundle = this.getIntent().getExtras();
            parkingLotName = bundle.getString("parkingLotName");
            parkingLotAddress = bundle.getString("parkingLotAddress");
            parkingLotUid = bundle.getString("parkingLotUid");
            routLinePlanots = intent.getParcelableExtra(ROUTE_PLANNING);
        }

        if (routLinePlanots != null)
            routePlanning();//规划出路经
    }

    private void routePlanning() {
        if (drivingStepsList == null) {
            drivingStepsList = new ArrayList<String>();
        } else {
            drivingStepsList.clear();
        }
        baiduMap.clear();
        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(RoutePlanningActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
                    baiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(drivingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
                //获取驾车路线
                List<DrivingRouteLine> drivingRouteLines = drivingRouteResult.getRouteLines();
                if (drivingRouteLines != null && drivingRouteLines.size() > 0) {
                    for (int i = 0; i < drivingRouteLines.size(); i++) {
                        DrivingRouteLine drivingRouteLine = drivingRouteLines.get(i);
                        if (drivingRouteLine != null) {
                            List<DrivingRouteLine.DrivingStep> drivingSteps = drivingRouteLine.getAllStep();
                            if (drivingSteps != null && drivingSteps.size() > 0) {
                                for (int j = 0; j < drivingSteps.size(); j++) {
                                    DrivingRouteLine.DrivingStep drivingStep = drivingSteps.get(j);
                                    if (drivingStep != null) {
                                        drivingStepsList.add(drivingStep.getInstructions());
                                    }
                                }
                            }
                        }
                    }
                }
                for (String a : drivingStepsList) {
                    System.out.printf("%s\n", a);
                }
                arrayAdapter = new ArrayAdapter<String>(RoutePlanningActivity.this, android.R.layout.simple_expandable_list_item_1, drivingStepsList);
                ListView listView = (ListView) findViewById(R.id.route);
                listView.setAdapter(arrayAdapter);

                //计算路线的距离
                if (drivingRouteResult.getRouteLines() == null) {
                    Toast.makeText(RoutePlanningActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                distance = drivingRouteResult.getRouteLines().get(0).getDistance();
                if ((int) distance / 1000 == 0) {
                    Toast.makeText(RoutePlanningActivity.this, "全程距离是:" + distance + "米", Toast.LENGTH_SHORT).show();
                } else {
                    distance = distance / 1000;
                    BigDecimal b = new BigDecimal(distance);
                    double formatDistance = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    Toast.makeText(RoutePlanningActivity.this, "全程距离是:" + formatDistance + "千米", Toast.LENGTH_SHORT).show();
                }

                duration = drivingRouteResult.getRouteLines().get(0).getDuration();
                if (duration / 3600 == 0) {
                    reserveHour = 0;
                    reserveMinute = duration / 60;
                    Toast.makeText(RoutePlanningActivity.this, "大约需要：" + reserveMinute + "分钟", Toast.LENGTH_SHORT).show();
                } else {
                    reserveHour = duration / 3600;
                    reserveMinute = (duration % 3600) / 60;
                    Toast.makeText(RoutePlanningActivity.this, "大约需要：" + reserveHour + "小时" + reserveMinute + "分钟", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            }

            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
            }
        });
        routePlanSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(routLinePlanots.getStartPlanNode()).to(routLinePlanots.getTargetPlanNode()));
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case COUNTING_TIME:
                    count++;
                    //超过预计时间10分钟
                    if (reserveHour * 3600 + (reserveMinute + 10) * 60 == count) {
                        AlertDialog.Builder build = new AlertDialog.Builder(RoutePlanningActivity.this);
                        build.setTitle(R.string.cancel_reserve);
                        build.setIcon(R.drawable.icon_arrive);
                        build.setNegativeButton(R.string.sure_arrive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(RoutePlanningActivity.this, R.string.reserve_again, Toast.LENGTH_SHORT).show();
                                timerTask_synchronize.cancel();
                                timer.cancel();
                                finish();
                            }
                        });
                        build.show();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            if (MyApp.isReserve()) {
                moveTaskToBack(true);
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
