import { AviationDoECorsiaDeterminationReason } from 'pmrv-api';

import { DoeDeterminationReasonTypePipe } from './doe-determination-reason-type.pipe';

describe('DoeDeterminationReasonTypePipe', () => {
  let pipe: DoeDeterminationReasonTypePipe;

  beforeEach(() => {
    pipe = new DoeDeterminationReasonTypePipe();
  });

  it('should transform a valid value VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED correctly', () => {
    const value: AviationDoECorsiaDeterminationReason['type'] = 'VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED';
    const result = pipe.transform(value);
    expect(result).toBe('A verified emissions report has not been submitted');
  });

  it('should transform a valid value CORRECTIONS_TO_A_VERIFIED_REPORT correctly', () => {
    const value: AviationDoECorsiaDeterminationReason['type'] = 'CORRECTIONS_TO_A_VERIFIED_REPORT';
    const result = pipe.transform(value);
    expect(result).toBe('We are making corrections to a verified report');
  });

  it('should return null for a null value', () => {
    const value: AviationDoECorsiaDeterminationReason['type'] = null;
    const result = pipe.transform(value);
    expect(result).toBeNull();
  });

  it('should return null for an unknown value', () => {
    const value: any = 'unknown';
    const result = pipe.transform(value);
    expect(result).toBeNull();
  });
});
