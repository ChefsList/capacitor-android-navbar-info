export interface SystemBarsPlugin {
  getNavigationBarInfo(): Promise<{
    navigationBarHeight: number;
    isNavigationBarVisible: boolean;
    isGestureNavigation: boolean;
  }>;
}
