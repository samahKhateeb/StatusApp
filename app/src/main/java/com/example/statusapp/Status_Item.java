package com.example.statusapp;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Status_Item {

    private String UserName;
    private BitmapDrawable StatusData;
    private String StatusPath;
    private String StatusText;
    private String StartDate;
    private String EndDate;
    private String StatsType;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public BitmapDrawable getStatusData() {
        return StatusData;
    }

    public void setStatusData(BitmapDrawable statusData) {
        StatusData = statusData;
    }

    public String getStatusPath() {
        return StatusPath;
    }

    public void setStatusPath(String statusPath) {
        StatusPath = statusPath;
    }

    public String getStatusText() {
        return StatusText;
    }

    public void setStatusText(String statusText) {
        StatusText = statusText;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getStatsType() {
        return StatsType;
    }

    public void setStatsType(String statsType) {
        StatsType = statsType;
    }

    public Status_Item(String userName, BitmapDrawable statusData, String statusPath, String statusText, String startDate, String endDate, String statsType) {
        UserName = userName;
        StatusData = statusData;
        StatusPath = statusPath;
        StatusText = statusText;
        StartDate = startDate;
        EndDate = endDate;
        StatsType = statsType;


    }
}
