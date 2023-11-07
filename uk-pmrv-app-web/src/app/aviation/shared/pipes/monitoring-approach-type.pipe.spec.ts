import { MonitoringApproachTypePipe } from '@aviation/shared/pipes/monitoring-approach-type.pipe';

describe('MonitoringApproachTypePipe', () => {
  let pipe: MonitoringApproachTypePipe;

  beforeEach(() => (pipe = new MonitoringApproachTypePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform monitoring approach types', () => {
    expect(pipe.transform('EUROCONTROL_SUPPORT_FACILITY')).toEqual('Use unmodified Eurocontrol Support Facility data');
    expect(pipe.transform('EUROCONTROL_SMALL_EMITTERS')).toEqual(
      'Use your own flight data with the Eurocontrol Small Emitters Tool',
    );
    expect(pipe.transform('FUEL_USE_MONITORING')).toEqual('Use fuel use monitoring');
    expect(pipe.transform(null)).toEqual(null);
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
