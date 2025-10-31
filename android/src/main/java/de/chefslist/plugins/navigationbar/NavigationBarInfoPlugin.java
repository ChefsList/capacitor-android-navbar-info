package de.chefslist.plugins.navigationbarinfo;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Insets;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
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
  private static final String EVENT_NAV_BAR_CHANGED = "navigationBarInfoChanged";
  
  private NavigationBarInfo lastKnownInfo = null;
  private View.OnApplyWindowInsetsListener insetsListener = null;
  private View.OnLayoutChangeListener layoutChangeListener = null;

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

  @Override
  public void load() {
    super.load();
    Log.d(TAG, "Plugin loaded - setting up listeners");
    setupWindowInsetsListener();
  }
  
  @Override
  protected void handleOnStart() {
    super.handleOnStart();
    Log.d(TAG, "Plugin started - ensuring listeners are active");
    // Ensure listeners are set up if they were removed
    if (layoutChangeListener == null) {
      setupWindowInsetsListener();
    }
  }
  
  @Override
  protected void handleOnStop() {
    super.handleOnStop();
    Log.d(TAG, "Plugin stopped - keeping listeners active for background monitoring");
    // Don't remove listeners on stop - we want to keep monitoring even in background
  }
  
  @Override
  protected void handleOnDestroy() {
    super.handleOnDestroy();
    Log.d(TAG, "Plugin destroyed - removing listeners");
    removeWindowInsetsListener();
  }
  
  private void setupWindowInsetsListener() {
    try {
      Activity activity = getActivity();
      if (activity == null) {
        Log.w(TAG, "Cannot setup listeners - activity is null");
        return;
      }
      
      if (activity.getWindow() == null) {
        Log.w(TAG, "Cannot setup listeners - window is null");
        return;
      }
      
      View decorView = activity.getWindow().getDecorView();
      if (decorView == null) {
        Log.w(TAG, "Cannot setup listeners - decor view is null");
        return;
      }
    
    // Remove existing listeners if any
    removeWindowInsetsListener();
    
    Log.d(TAG, "Setting up window insets and layout change listeners");
    
    // Create and attach insets listener
    insetsListener = new View.OnApplyWindowInsetsListener() {
      @Override
      public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
        checkAndNotifyNavigationBarChanges();
        return insets;
      }
    };
    
    decorView.setOnApplyWindowInsetsListener(insetsListener);
    
    // Create and attach layout change listener for more reliable detection
    layoutChangeListener = new View.OnLayoutChangeListener() {
      @Override
      public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                 int oldLeft, int oldTop, int oldRight, int oldBottom) {
        // Check if layout dimensions have changed
        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
          Log.d(TAG, "Layout changed - checking navigation bar info");
          checkAndNotifyNavigationBarChanges();
        }
      }
    };
    
      decorView.addOnLayoutChangeListener(layoutChangeListener);
      
      Log.d(TAG, "Listeners attached successfully");
      
      // Initial check
      checkAndNotifyNavigationBarChanges();
    } catch (Exception e) {
      Log.e(TAG, "Error setting up window insets listener", e);
    }
  }
  
  private void checkAndNotifyNavigationBarChanges() {
    try {
      NavigationBarInfo currentInfo = getSystemNavigationBarInfo();
      
      if (currentInfo == null) {
        Log.w(TAG, "Navigation bar info is null, skipping change notification");
        return;
      }
      
      // Check if info has changed
      if (hasNavigationBarInfoChanged(lastKnownInfo, currentInfo)) {
        Log.d(TAG, "Navigation bar info changed - Height: " + currentInfo.heightInDp + "dp, DeviceHeight: " + currentInfo.deviceHeight + "px");
        lastKnownInfo = currentInfo;
        notifyNavigationBarInfoChanged(currentInfo);
      }
    } catch (Exception e) {
      Log.e(TAG, "Error checking navigation bar changes", e);
    }
  }
  
  private void removeWindowInsetsListener() {
    try {
      Activity activity = getActivity();
      if (activity == null || activity.getWindow() == null) {
        return;
      }
      
      View decorView = activity.getWindow().getDecorView();
      if (decorView != null) {
        if (insetsListener != null) {
          decorView.setOnApplyWindowInsetsListener(null);
        }
        if (layoutChangeListener != null) {
          decorView.removeOnLayoutChangeListener(layoutChangeListener);
        }
      }
    } catch (Exception e) {
      Log.e(TAG, "Error removing window insets listener", e);
    } finally {
      insetsListener = null;
      layoutChangeListener = null;
    }
  }
  
  private boolean hasNavigationBarInfoChanged(NavigationBarInfo oldInfo, NavigationBarInfo newInfo) {
    if (oldInfo == null) {
      return true;
    }
    
    return oldInfo.heightInDp != newInfo.heightInDp ||
           oldInfo.deviceHeight != newInfo.deviceHeight ||
           oldInfo.isNavigationBarVisible != newInfo.isNavigationBarVisible ||
           oldInfo.isGestureNavigation != newInfo.isGestureNavigation;
  }
  
  private void notifyNavigationBarInfoChanged(NavigationBarInfo info) {
    JSObject data = createNavigationBarInfoResult(info);
    if (data != null) {
      Log.d(TAG, "Notifying listeners: " + EVENT_NAV_BAR_CHANGED + " with data: " + data.toString());
      notifyListeners(EVENT_NAV_BAR_CHANGED, data);
    } else {
      Log.e(TAG, "Failed to create navigation bar info result for notification");
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
      lastKnownInfo = systemNavigationBarInfo;
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
    float density = 1.0f;

    // Return safe defaults if activity is null
    if (activity == null) {
      Log.w(TAG, "Activity is null, returning default navigation bar info");
      return new NavigationBarInfo(0, 0, 1.0f, false, false);
    }

    try {
      // Get display density safely
      if (activity.getResources() != null && activity.getResources().getDisplayMetrics() != null) {
        density = activity.getResources().getDisplayMetrics().density;
      }

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
        } catch (Exception e) {
          Log.w(TAG, "Error detecting navigation mode", e);
          isGestureNavigation = false;
        }

      } else {
        // Pre-Android 11 fallback
        Resources res = activity.getResources();
        if (res != null) {
          int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");

          if (resourceId > 0) {
            deviceHeight = res.getDimensionPixelSize(resourceId);
          }
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
          } catch (Exception e) {
            Log.w(TAG, "Error detecting navigation mode", e);
            isGestureNavigation = false;
          }
        }

        // If navigation bar height > 0, assume visible
        isNavigationBarVisible = deviceHeight > 0 && !isGestureNavigation;
      }
    } catch (Exception e) {
      Log.e(TAG, "Error getting system navigation bar info, returning defaults", e);
      // Return safe defaults on any error
      return new NavigationBarInfo(0, 0, density, false, false);
    }

    return new NavigationBarInfo(
        pxToDp(deviceHeight),
        deviceHeight,
        density,
        isNavigationBarVisible,
        isGestureNavigation);
  }

  private int pxToDp(int devicePx) {
    if (devicePx == 0) {
      return 0;
    }

    try {
      Activity activity = getActivity();
      if (activity == null || activity.getResources() == null || activity.getResources().getDisplayMetrics() == null) {
        Log.w(TAG, "Cannot convert px to dp, returning px value");
        return devicePx;
      }

      float density = activity.getResources().getDisplayMetrics().density;
      if (density <= 0) {
        return devicePx;
      }

      return (int) Math.round(devicePx / density);
    } catch (Exception e) {
      Log.w(TAG, "Error converting px to dp", e);
      return devicePx;
    }
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
