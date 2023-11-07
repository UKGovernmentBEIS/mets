import { MonitoringApproachVerifyCorsiaTypePipe } from '@aviation/shared/pipes/monitoring-approach-verify-corsia-type.pipe';

describe('MonitoringApproachVerifyCorsiaTypePipe', () => {
  let pipe: MonitoringApproachVerifyCorsiaTypePipe;

  beforeEach(() => (pipe = new MonitoringApproachVerifyCorsiaTypePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform monitoring approach types', () => {
    expect(pipe.transform('CERT_MONITORING')).toEqual('CERT only');
    expect(pipe.transform('FUEL_USE_MONITORING')).toEqual('Fuel use monitoring only');
    expect(pipe.transform('CERT_AND_FUEL_USE_MONITORING')).toEqual('CERT and fuel use monitoring');
    expect(pipe.transform(null)).toEqual(null);
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
