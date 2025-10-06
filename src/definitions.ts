export interface SystemBarsPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
