package com.example.realgodjj.parking_system;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DensityUtil {	
	
	 /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2Px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2Dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }
    
    /**
     * 根据上下文重置DisplayMetrics
     */
    public static DisplayMetrics dueDisplayMetrics(Context context){
    	WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    	DisplayMetrics dm=new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        return dm;
    }
    
    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context){
    	DisplayMetrics dm=dueDisplayMetrics(context);
    	return dm.widthPixels;
    }
    
    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context){
    	DisplayMetrics dm=dueDisplayMetrics(context);
    	return dm.heightPixels;
    }
}
