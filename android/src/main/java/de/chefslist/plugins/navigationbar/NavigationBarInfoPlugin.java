package de.chefslist.plugins.navigationbarinfo;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Insets;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowInsets;
import android.view.WindowMetrics;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "NavigationBarInfo")
public class NavigationBarInfoPlugin extends Plugin {
  private static final String TAG = "NavigationBarInfoPlugin";
  private static final String ERROR_UNKNOWN = "UNKNOWN_ERROR";

  private static class NavigationBarInfo {
    final int heightInDp;
    final int deviceHeight;
    final float density;
    final boolean isNavigationBarVisible;
    final boolean isGestureNavigation;

    NavigationBarInfo(int heightInDp, int deviceHeight, float density, boolean isVisible, boolean isGestureNav) {
      this.heightInDp = Math.max(0, heightInDp);
      this.deviceHeight = Math.max(0, deviceHeight);
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
    int deviceHeight = 0;
    boolean isNavigationBarVisible = false;
    boolean isGestureNavigation = false;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // Android 11+ (API 30)
      WindowMetrics metrics = activity.getWindowManager().getCurrentWindowMetrics();
      WindowInsets insets = metrics.getWindowInsets();

      Insets navInsets = insets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars());
      deviceHeight = navInsets.bottom;

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
        deviceHeight = res.getDimensionPixelSize(resourceId);
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
      isNavigationBarVisible = deviceHeight > 0 && !isGestureNavigation;
    }

    return new NavigationBarInfo(
        pxToDp(deviceHeight),
        deviceHeight,
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

    result.put("heightInDp", info.heightInDp);
    result.put("deviceHeight", info.deviceHeight);
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
