import { ActivityCalculationMethodTypePipe } from './activity-calculation-method-type.pipe';

describe('ActivityCalculationMethodTypePipe', () => {
  const pipe = new ActivityCalculationMethodTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('CONTINUOUS_METERING')).toEqual('Continuous metering');
    expect(pipe.transform('AGGREGATION_OF_METERING_QUANTITIES')).toEqual('Aggregation of metering quantities');
  });
});
