import { registerPlugin } from "@capacitor/core";

import type { NavigationBarInfoPlugin } from "./definitions";

const NavigationBarInfo = registerPlugin<NavigationBarInfoPlugin>(
  "NavigationBarInfo",
  {
    web: () => import("./web").then((m) => new m.NavigationBarInfoWeb()),
  }
);

export * from "./definitions";
export { NavigationBarInfo };
