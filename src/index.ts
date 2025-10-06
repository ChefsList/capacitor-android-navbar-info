import { registerPlugin } from '@capacitor/core';

import type { SystemBarsPlugin } from './definitions';

const SystemBars = registerPlugin<SystemBarsPlugin>('SystemBars', {
  web: () => import('./web').then((m) => new m.SystemBarsWeb()),
});

export * from './definitions';
export { SystemBars };
