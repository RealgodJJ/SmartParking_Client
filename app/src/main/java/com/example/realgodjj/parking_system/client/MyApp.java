package com.example.realgodjj.parking_system.client;


import com.baidu.location.BDLocation;
import com.baidu.mapapi.cloud.CloudRgcResult;
import com.baidu.mapapi.search.core.PoiInfo;

public class MyApp {

    private static boolean isLogin = false;

    private static boolean isRegister = false;

    private static boolean isReceive = false;

    private static boolean isSureDestination = false;

    private static boolean isIntent = false;

    private static String ipAddress = "http://10.25.40.125:8085";

    private static String userName = "";

    private static int currentParkId;//当前点击的停车场ID

    private static BDLocation currBDLocation;

    private static PoiInfo currClickPoi;

    private static boolean isReserve = false;

    private static boolean isResetPassword = false;

    private static boolean isSearchDestination = false;

    private static boolean isBestChoice = false;

    public static boolean isLogin() {
        return isLogin;
    }

    public static void setLogin(boolean login) {
        MyApp.isLogin = login;
    }

    public static boolean isRegister() {
        return isRegister;
    }

    public static void setRegister(boolean isRegister) {
        MyApp.isRegister = isRegister;
    }

    public static boolean isReceive() {
        return isReceive;
    }

    public static void setReceive(boolean isReceive) {
        MyApp.isReceive = isReceive;
    }

    public static boolean isSureDestination() {
        return isSureDestination;
    }

    public static void setSureDestination(boolean isSureDestination) {
        MyApp.isSureDestination = isSureDestination;
    }

    public static boolean isIntent() {
        return isIntent;
    }

    public static void setIntent(boolean isIntent) {
        MyApp.isIntent = isIntent;
    }

    public static String getIpAddress() {
        return ipAddress;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        MyApp.userName = userName;
    }

    public static int getCurrentParkId() {
        return currentParkId;
    }

    public static void setCurrentParkId(int currentParkId) {
        MyApp.currentParkId = currentParkId;
    }

    public static BDLocation getCurrBDLocation() {
        return currBDLocation;
    }

    public static void setCurrBDLocation(BDLocation currBDLocation) {
        MyApp.currBDLocation = currBDLocation;
    }

    public static PoiInfo getCurrClickPoi() {
        return currClickPoi;
    }

    public static void setCurrClickPoi(PoiInfo currClickPoi) {
        MyApp.currClickPoi = currClickPoi;
    }

    public static boolean isReserve() {
        return isReserve;
    }

    public static void setReserve(boolean isReserve) {
        MyApp.isReserve = isReserve;
    }

    public static boolean isResetPassword() {
        return isResetPassword;
    }

    public static void setResetPassword(boolean isResetPassword) {
        MyApp.isResetPassword = isResetPassword;
    }

    public static boolean isSearchDestination() {
        return isSearchDestination;
    }

    public static void setSearchDestination(boolean isSearchDestination) {
        MyApp.isSearchDestination = isSearchDestination;
    }

    public static boolean isBestChoice() {
        return isBestChoice;
    }

    public static void setBestChoice(boolean isBestChoice) {
        MyApp.isBestChoice = isBestChoice;
    }
}
