import { WebPlugin } from '@capacitor/core';

import type { SystemBarsPlugin } from './definitions';

export class SystemBarsWeb extends WebPlugin implements SystemBarsPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
