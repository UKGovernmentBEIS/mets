import { SubtaskNamePipe } from './subtask-name.pipe';

describe('SubtaskNamePipe', () => {
  let pipe: SubtaskNamePipe;

  beforeEach(async () => {
    pipe = new SubtaskNamePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('MEASUREMENT_CO2_Measured_Emissions')).toEqual('Measured emissions');
    expect(pipe.transform('MEASUREMENT_CO2_Applied_Standard')).toEqual('Applied standard');
    expect(pipe.transform('MEASUREMENT_CO2_Biomass_Fraction')).toEqual('Biomass fraction');
  });
});
