import { FuelMeteringConditionTypePipe } from './fuel-metering-condition-type.pipe';

describe('FuelMeteringConditionTypePipe', () => {
  const pipe = new FuelMeteringConditionTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('CELSIUS_0')).toEqual('0º celsius (standard conditions)');
    expect(pipe.transform('CELSIUS_15')).toEqual('15º celsius (metering condition)');
  });
});
