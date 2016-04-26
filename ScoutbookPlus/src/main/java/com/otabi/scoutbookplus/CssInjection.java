package com.otabi.scoutbookplus;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.CompoundButton;

import java.util.Observable;

/**
 * Created by Stephen on 4/19/2016.
 *
 * POJO for data binding
 */
public class CssInjection implements CompoundButton.OnCheckedChangeListener {
    private final MainActivity activity;
    private boolean on;

    public CssInjection(boolean on, MainActivity activity) {
        this.on = on;
        this.activity = activity;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.i(MainActivity.TAG, "set green sliders");
        if (this.on != isChecked) {
            this.on = isChecked;
            activity.reload();
        }

    }
}

