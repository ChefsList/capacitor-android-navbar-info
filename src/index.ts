import { registerPlugin } from "@capacitor/core";

import type { NavigationBarInfoPlugin } from "./definitions";

const NavigationBarInfo =
  registerPlugin<NavigationBarInfoPlugin>("NavigationBarInfo");

export * from "./definitions";
export { NavigationBarInfo };
