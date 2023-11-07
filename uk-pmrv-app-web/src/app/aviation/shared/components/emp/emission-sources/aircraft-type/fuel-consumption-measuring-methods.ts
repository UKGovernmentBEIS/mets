export type FuelConsumptionMeasuringMethod =
  | 'METHOD_A'
  | 'METHOD_B'
  | 'BLOCK_ON_BLOCK_OFF'
  | 'FUEL_UPLIFT'
  | 'BLOCK_HOUR';
export const FuelConsumptionMeasuringMethods: { label: string; value: FuelConsumptionMeasuringMethod }[] = [
  {
    label: 'Method A',
    value: 'METHOD_A',
  },
  {
    label: 'Method B',
    value: 'METHOD_B',
  },
  {
    label: 'Block-off / Block-on',
    value: 'BLOCK_ON_BLOCK_OFF',
  },
  {
    label: 'Fuel Uplift',
    value: 'FUEL_UPLIFT',
  },
  {
    label: 'Block-hour',
    value: 'BLOCK_HOUR',
  },
];
