package com.example.tihomir.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DatabaseChangedReceiver extends BroadcastReceiver {
    public static String ACTION_DATABASE_CHANGED = "com.youapppackage.DATABASE_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}