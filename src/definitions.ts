export interface NavigationBarInfo {
  heightInDp: number;
  deviceHeight: number;
  density: number;
  isNavigationBarVisible: boolean;
  isGestureNavigation: boolean;
}
export interface NavigationBarInfoPlugin {
  getNavigationBarInfo(): Promise<NavigationBarInfo>;
}
