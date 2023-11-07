import BigNumber from 'bignumber.js';

export function format(value: BigNumber, maxDecimals: number = 5): string {
  const decimals = value.decimalPlaces();
  const decimalsToTrim = decimals <= maxDecimals ? decimals : maxDecimals;

  return value.toFixed(decimalsToTrim);
}
