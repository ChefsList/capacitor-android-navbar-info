package de.chefslist.plugins.navigationbarinfo;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.graphics.Insets;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "NavigationBarInfo")
public class NavigationBarInfoPlugin extends Plugin {
  private static final String TAG = "NavigationBarInfoPlugin";
  private static final String ERROR_INVALID_CALL = "INVALID_CALL";
  private static final String ERROR_UNKNOWN = "UNKNOWN_ERROR";
  private static final int DEFAULT_NAV_BAR_HEIGHT = 20;

  private static class NavigationBarInfo {
    final int navigationBarHeight;
    final int navigationBarDeviceHeight;
    final float density;
    final boolean isNavigationBarVisible;
    final boolean isGestureNavigation;

    NavigationBarInfo(int height, int deviceHeight, float density, boolean isVisible, boolean isGestureNav) {
      this.navigationBarHeight = Math.max(0, height); // Ensure non-negative height
      this.navigationBarDeviceHeight = Math.max(0, deviceHeight); // Ensure non-negative height
      this.density = Math.max(1, density);
      this.isNavigationBarVisible = isVisible;
      this.isGestureNavigation = isGestureNav;
    }
  }

  @PluginMethod
  public void getNavigationBarInfo(PluginCall call) {
    if (call == null) {
      Log.e(TAG, "PluginCall is null");
      return;
    }

    try {
      NavigationBarInfo systemNavigationBarInfo = getSystemNavigationBarInfo();
      JSObject result = createNavigationBarInfoResult(systemNavigationBarInfo);

      if (result != null) {
        call.resolve(result);
      } else {
        sendErrorResponse(call, "Failed to create navigation bar info result", ERROR_UNKNOWN);
      }
    } catch (Exception e) {
      Log.e(TAG, "Error getting navigation bar info", e);
      sendErrorResponse(call, "Error getting navigation bar info: " + e.getMessage(), ERROR_UNKNOWN);
    }
  }

  private NavigationBarInfo getSystemNavigationBarInfo() {
    Activity activity = getActivity();
    int navigationBarHeight = 0;
    boolean isNavigationBarVisible = false;
    boolean isGestureNavigation = false;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // Android 11+ (API 30)
      WindowMetrics metrics = activity.getWindowManager().getCurrentWindowMetrics();
      WindowInsets insets = metrics.getWindowInsets();

      Insets navInsets = insets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars());
      navigationBarHeight = navInsets.bottom;

      // Determine visibility
      isNavigationBarVisible = insets.isVisible(WindowInsets.Type.navigationBars());

      // Detect gesture navigation (Android 10+)
      try {
        int navigationMode = Settings.Secure.getInt(
            activity.getContentResolver(),
            "navigation_mode");
        // 0 = 3-button, 1 = 2-button, 2 = gesture
        isGestureNavigation = (navigationMode == 2);
      } catch (Settings.SettingNotFoundException e) {
        // Default to false if setting unavailable
        isGestureNavigation = false;
      }

    } else {
      // Pre-Android 11 fallback
      Resources res = activity.getResources();
      int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
      if (resourceId > 0) {
        navigationBarHeight = res.getDimensionPixelSize(resourceId);
      }

      // Try to detect if gesture navigation is active (Android 10)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        try {
          int navigationMode = Settings.Secure.getInt(
              activity.getContentResolver(),
              "navigation_mode");
          isGestureNavigation = (navigationMode == 2);
        } catch (Settings.SettingNotFoundException e) {
          isGestureNavigation = false;
        }
      }

      // If navigation bar height > 0, assume visible
      isNavigationBarVisible = navigationBarHeight > 0 && !isGestureNavigation;
    }

    return new NavigationBarInfo(
        pxToDp(navigationBarHeight),
        navigationBarHeight,
        activity.getResources().getDisplayMetrics().density,
        isNavigationBarVisible,
        isGestureNavigation);
  }

  private int pxToDp(int devicePx) {
    Activity activity = getActivity();

    if (devicePx == 0) {
      return 0;
    }

    float density = activity.getResources().getDisplayMetrics().density;

    return (int) Math.round(devicePx / density);
  }

  private JSObject createNavigationBarInfoResult(NavigationBarInfo info) {
    if (info == null) {
      return null;
    }

    JSObject result = new JSObject();

    result.put("navigationBarHeight", info.navigationBarHeight);
    result.put("navigationBarDeviceHeight", info.navigationBarDeviceHeight);
    result.put("density", info.density);
    result.put("isNavigationBarVisible", info.isNavigationBarVisible);
    result.put("isGestureNavigation", info.isGestureNavigation);

    return result;
  }

  private void sendErrorResponse(PluginCall call, String message, String errorCode) {
    if (call != null) {
      try {
        call.reject(message, errorCode);
      } catch (Exception e) {
        Log.e(TAG, "Error sending error response", e);
      }
    }
  }
}
