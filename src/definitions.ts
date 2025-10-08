export interface NavigationBarInfoPlugin {
  getNavigationBarInfo(): Promise<{
    navigationBarHeight: number;
    navigationBarDeviceHeight: number;
    density: number;
    isNavigationBarVisible: boolean;
    isGestureNavigation: boolean;
  }>;
}
