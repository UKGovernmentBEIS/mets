import { FuelDensityTypePipe } from '@aviation/shared/pipes/fuel-density-type.pipe';

describe('FuelDensityTypePipe', () => {
  const pipe = new FuelDensityTypePipe();

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform fuel density types', () => {
    expect(pipe.transform('ACTUAL_DENSITY')).toEqual('Actual density');
    expect(pipe.transform('ACTUAL_STANDARD_DENSITY')).toEqual('Actual and standard density');
    expect(pipe.transform('STANDARD_DENSITY')).toEqual('Standard density');
    expect(pipe.transform('NOT_APPLICABLE')).toEqual('Not applicable - we only use the block-off/block-on method');
    expect(pipe.transform(null)).toEqual(null);
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
