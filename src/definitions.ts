export interface NavigationBarInfo {
  navigationBarHeight: number;
  navigationBarDeviceHeight: number;
  density: number;
  isNavigationBarVisible: boolean;
  isGestureNavigation: boolean;
}
export interface NavigationBarInfoPlugin {
  getNavigationBarInfo(): Promise<NavigationBarInfo>;
}
