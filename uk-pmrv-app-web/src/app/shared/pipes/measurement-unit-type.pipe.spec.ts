import { MeasurementUnitTypePipe } from './measurement-unit-type.pipe';

describe('MeasurementUnitTypePipe', () => {
  const pipe = new MeasurementUnitTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('TONNES')).toEqual('tonnes');
    expect(pipe.transform('NM3')).toEqual('normal cubic meter (Nm3)');
    expect(pipe.transform('GJ_PER_NM3')).toEqual('GJ/Nm3');
  });
});
