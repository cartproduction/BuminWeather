package com.bumin.weather.model;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;

import java.lang.reflect.Field;

public class Snack {
    public static int LENGTH_LONG = Snackbar.LENGTH_LONG;
    public static int LENGTH_SHORT = Snackbar.LENGTH_SHORT;
    public static int LENGTH_INDEFINITE = Snackbar.LENGTH_INDEFINITE;

    public static void make(View view, String text, int duration) {
        Snackbar snackbar = Snackbar.make(view , text, duration);
        try {
            Field mAccessibilityManagerField = BaseTransientBottomBar.class.getDeclaredField("mAccessibilityManager");
            mAccessibilityManagerField.setAccessible(true);
            AccessibilityManager accessibilityManager = (AccessibilityManager) mAccessibilityManagerField.get(snackbar);
            Field mIsEnabledField = AccessibilityManager.class.getDeclaredField("mIsEnabled");
            mIsEnabledField.setAccessible(true);
            mIsEnabledField.setBoolean(accessibilityManager, false);
            mAccessibilityManagerField.set(snackbar, accessibilityManager);
        } catch (Exception e) {
            Log.d("Snackbar", "Reflection error: " + e.toString());
        }
        snackbar.show();
    }
}