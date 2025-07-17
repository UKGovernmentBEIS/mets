import { FuelBurnCalculationTypePipe } from './fuel-burn-calculation-type.pipe';

describe('FuelBurnCalculationTypePipe', () => {
  const pipe = new FuelBurnCalculationTypePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('MEASURABLE_HEAT_IMPORTED')).toEqual('Measurable heat imported');
    expect(pipe.transform('MEASURABLE_HEAT_FROM_PULP')).toEqual('Measurable heat from pulp');
    expect(pipe.transform('MEASURABLE_HEAT_FROM_NITRIC_ACID')).toEqual('Measurable heat from nitric acid');
    expect(pipe.transform('MEASURABLE_HEAT_EXPORTED')).toEqual('Measurable heat exported');
    expect(pipe.transform('NO_MEASURABLE_HEAT')).toEqual(
      'No, measurable heat is not imported to or exported from this sub-installation',
    );
  });

  it('should handle empty value', () => {
    const transformation = pipe.transform(null);

    expect(transformation).toEqual('');
  });

  it('should handle undefined value', () => {
    const transformation = pipe.transform(undefined);

    expect(transformation).toEqual('');
  });
});
