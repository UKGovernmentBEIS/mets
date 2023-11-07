import { PfcCalculationMethodPipe } from './pfc-calculation-method.pipe';

describe('PfcCalculationMethodPipe', () => {
  let pipe: PfcCalculationMethodPipe;

  beforeEach(async () => {
    pipe = new PfcCalculationMethodPipe();
  });
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('SLOPE')).toEqual('Slope');
    expect(pipe.transform('OVERVOLTAGE')).toEqual('Overvoltage');
  });
});
