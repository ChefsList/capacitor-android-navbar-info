package de.chefslist.plugins.systembars;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "SystemBars")
public class SystemBarsPlugin extends Plugin {

    @PluginMethod
    public void getNavigationBarInfo(PluginCall call) {
        JSObject result = new JSObject();
        // Mock data - replace with actual implementation later
        result.put("navigationBarHeight", 48); // Typical height in dp
        result.put("isNavigationBarVisible", true);
        result.put("isGestureNavigation", false);
        
        call.resolve(result);
    }
}
