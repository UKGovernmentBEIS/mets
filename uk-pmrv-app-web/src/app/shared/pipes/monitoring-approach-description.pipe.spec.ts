import { MonitoringApproachDescriptionPipe } from './monitoring-approach-description.pipe';

describe('MonitoringApproachPipe', () => {
  let pipe: MonitoringApproachDescriptionPipe;

  beforeEach(async () => {
    pipe = new MonitoringApproachDescriptionPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('CALCULATION_CO2')).toEqual('Calculation of CO2');
    expect(pipe.transform('MEASUREMENT_CO2')).toEqual('Measurement of CO2');
    expect(pipe.transform('FALLBACK')).toEqual('Fallback approach');
    expect(pipe.transform('MEASUREMENT_N2O')).toEqual('Measurement of nitrous oxide (N2O)');
    expect(pipe.transform('CALCULATION_PFC')).toEqual('Calculation of perfluorocarbons (PFC)');
    expect(pipe.transform('INHERENT_CO2')).toEqual('Inherent CO2 emissions');
    expect(pipe.transform('TRANSFERRED_CO2_N2O')).toEqual('Procedures for transferred CO2 or N2O');
  });
});
