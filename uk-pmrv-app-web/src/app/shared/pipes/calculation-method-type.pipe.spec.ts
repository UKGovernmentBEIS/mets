import { CalculationMethodTypePipe } from './calculation-method-type.pipe';

describe('CalculationMethodTypePipe', () => {
  const pipe = new CalculationMethodTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('MANUAL')).toEqual('Calculate the values manually');
    expect(pipe.transform('NATIONAL_INVENTORY_DATA')).toEqual('Use national inventory data');
    expect(pipe.transform('REGIONAL_DATA')).toEqual('Use regional data for natural gas');
  });
});
