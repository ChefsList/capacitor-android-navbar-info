import { registerPlugin } from "@capacitor/core";
import type { SystemBarsPlugin } from "./definitions";

const SystemBars = registerPlugin<SystemBarsPlugin>("SystemBars");

export * from "./definitions";
export { SystemBars };
