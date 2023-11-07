import { MonitoringApproachEmissionDescriptionPipe } from './monitoring-approach-emission-description.pipe';

describe('MonitoringApproachEmissionDescriptionPipe', () => {
  let pipe: MonitoringApproachEmissionDescriptionPipe;

  beforeEach(async () => {
    pipe = new MonitoringApproachEmissionDescriptionPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('CALCULATION_CO2')).toEqual('Calculation of CO2 emissions');
    expect(pipe.transform('MEASUREMENT_CO2')).toEqual('Measurement of CO2 emissions');
    expect(pipe.transform('FALLBACK')).toEqual('Fallback approach emissions');
    expect(pipe.transform('MEASUREMENT_N2O')).toEqual('Measurement of nitrous oxide (N2O) emissions');
    expect(pipe.transform('CALCULATION_PFC')).toEqual('Calculation of perfluorocarbons (PFC) emissions');
    expect(pipe.transform('INHERENT_CO2')).toEqual('Inherent CO2 emissions');
    expect(pipe.transform('TRANSFERRED_CO2_N2O')).toEqual('Procedures for transferred CO2 or N2O emissions');
  });
});
