import { ConnectionTypePipe } from './connection-type.pipe';

describe('ConnectionTypePipe', () => {
  const pipe = new ConnectionTypePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('MEASURABLE_HEAT')).toEqual('Measurable heat');
    expect(pipe.transform('WASTE_GAS')).toEqual('Waste gas');
    expect(pipe.transform('TRANSFERRED_CO2_FOR_USE_IN_YOUR_INSTALLATION_CCU')).toEqual(
      'Transferred CO2 for use in your installation (CCU)',
    );
    expect(pipe.transform('TRANSFERRED_CO2_FOR_STORAGE')).toEqual('Transferred CO2 for geological storage (CCS)');
    expect(pipe.transform('INTERMEDIATE_PRODUCTS_COVERED_BY_PRODUCT_BENCHMARKS')).toEqual(
      'Intermediate products covered by product benchmarks',
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
