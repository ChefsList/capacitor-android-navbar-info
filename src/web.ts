import { WebPlugin } from "@capacitor/core";

import type { NavigationBarInfo, NavigationBarInfoPlugin } from "./definitions";

export class NavigationBarInfoWeb
  extends WebPlugin
  implements NavigationBarInfoPlugin
{
  async getNavigationBarInfo(): Promise<NavigationBarInfo> {
    // Return default values for web/iOS platforms
    // Navigation bar is an Android-specific concept
    return {
      navigationBarHeight: 0,
      navigationBarDeviceHeight: 0,
      density: 1,
      isNavigationBarVisible: false,
      isGestureNavigation: false,
    };
  }
}
