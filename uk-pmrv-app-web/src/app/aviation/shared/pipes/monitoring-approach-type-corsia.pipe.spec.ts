import { MonitoringApproachTypeCorsiaPipe } from '@aviation/shared/pipes/monitoring-approach-type-corsia.pipe';

describe('MonitoringApproachTypeCorsiaPipe', () => {
  let pipe: MonitoringApproachTypeCorsiaPipe;

  beforeEach(() => (pipe = new MonitoringApproachTypeCorsiaPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform monitoring approach corsia types', () => {
    expect(pipe.transform('CERT_MONITORING')).toEqual('CORSIA CO2 estimation and reporting tool (CERT)');
    expect(pipe.transform('FUEL_USE_MONITORING')).toEqual('Fuel use monitoring');

    expect(pipe.transform('GREAT_CIRCLE_DISTANCE', false)).toEqual('Great circle distance');
    expect(pipe.transform('BLOCK_TIME', false)).toEqual('Block-time');

    expect(pipe.transform(null)).toEqual(null);
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
