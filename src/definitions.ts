import type { PluginListenerHandle } from "@capacitor/core";

export interface NavigationBarInfo {
  heightInDp: number;
  deviceHeight: number;
  density: number;
  isNavigationBarVisible: boolean;
  isGestureNavigation: boolean;
}

export interface NavigationBarInfoPlugin {
  getNavigationBarInfo(): Promise<NavigationBarInfo>;
  
  /**
   * Listen for navigation bar info changes (e.g., when switching between windowed and fullscreen modes)
   */
  addListener(
    eventName: 'navigationBarInfoChanged',
    listenerFunc: (info: NavigationBarInfo) => void
  ): Promise<PluginListenerHandle>;
  
  /**
   * Remove all listeners for this plugin
   */
  removeAllListeners(): Promise<void>;
}
