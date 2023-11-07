import { AviationDreEmissionsCalculationApproach } from 'pmrv-api';

import { AviationDreEmissionsTypePipe } from './aviation-dre-emissions-type.pipe';

describe('AviationDreEmissionsTypePipe', () => {
  let pipe: AviationDreEmissionsTypePipe;

  beforeEach(() => {
    pipe = new AviationDreEmissionsTypePipe();
  });

  it('should transform a valid value EUROCONTROL_SUPPORT_FACILITY correctly', () => {
    const value: AviationDreEmissionsCalculationApproach['type'] = 'EUROCONTROL_SUPPORT_FACILITY';
    const result = pipe.transform(value);
    expect(result).toBe('Eurocontrol Support Facility');
  });

  it('should transform a valid value VERIFIED_ANNUAL_EMISSIONS_REPORT_SUBMITTED_LATE correctly', () => {
    const value: AviationDreEmissionsCalculationApproach['type'] = 'VERIFIED_ANNUAL_EMISSIONS_REPORT_SUBMITTED_LATE';
    const result = pipe.transform(value);
    expect(result).toBe('a verified annual emissions report that was submitted late');
  });

  it('should transform a valid value OTHER_DATASOURCE correctly', () => {
    const value: AviationDreEmissionsCalculationApproach['type'] = 'OTHER_DATASOURCE';
    const result = pipe.transform(value);
    expect(result).toBe('another data source');
  });

  it('should return null for a null value', () => {
    const value: AviationDreEmissionsCalculationApproach['type'] = null;
    const result = pipe.transform(value);
    expect(result).toBeNull();
  });

  it('should return null for an unknown value', () => {
    const value: any = 'unknown';
    const result = pipe.transform(value);
    expect(result).toBeNull();
  });
});
