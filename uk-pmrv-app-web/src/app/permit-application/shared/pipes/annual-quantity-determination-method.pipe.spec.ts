import { AnnualQuantityDeterminationMethodPipe } from './annual-quantity-determination-method.pipe';

describe('AnnualQuantityDeterminationMethodPipe', () => {
  const pipe = new AnnualQuantityDeterminationMethodPipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('CONTINUAL_METERING_PROCESS')).toEqual(
      '5. (a) based on continual metering at the process where the material is consumed or produced',
    );
    expect(pipe.transform('AGGREGATION_METERING_QUANTITIES')).toEqual(
      '5. (b) based on aggregation of metering of quantities separately delivered or produced taking into account relevant stock changes',
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
