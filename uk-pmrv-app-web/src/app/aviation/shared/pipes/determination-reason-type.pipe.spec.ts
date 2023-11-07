import { AviationDreDeterminationReason } from 'pmrv-api';

import { DeterminationReasonTypePipe } from './determination-reason-type.pipe';

describe('DeterminationReasonTypePipe', () => {
  let pipe: DeterminationReasonTypePipe;

  beforeEach(() => {
    pipe = new DeterminationReasonTypePipe();
  });

  it('should transform a valid value VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER correctly', () => {
    const value: AviationDreDeterminationReason['type'] = 'VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER';
    const result = pipe.transform(value);
    expect(result).toBe('A report that was not submitted according to the Order');
  });

  it('should transform a valid value CORRECTING_NON_MATERIAL_MISSTATEMENT correctly', () => {
    const value: AviationDreDeterminationReason['type'] = 'CORRECTING_NON_MATERIAL_MISSTATEMENT';
    const result = pipe.transform(value);
    expect(result).toBe('Correcting a non-material misstatement');
  });

  it('should transform a valid value IMPOSING_OR_CONSIDERING_IMPOSING_CIVIL_PENALTY_IN_ACCORDANCE_WITH_ORDER correctly', () => {
    const value: AviationDreDeterminationReason['type'] =
      'IMPOSING_OR_CONSIDERING_IMPOSING_CIVIL_PENALTY_IN_ACCORDANCE_WITH_ORDER';
    const result = pipe.transform(value);
    expect(result).toBe('Imposing or considering imposing a civil penalty according to the Order');
  });

  it('should return null for a null value', () => {
    const value: AviationDreDeterminationReason['type'] = null;
    const result = pipe.transform(value);
    expect(result).toBeNull();
  });

  it('should return null for an unknown value', () => {
    const value: any = 'unknown';
    const result = pipe.transform(value);
    expect(result).toBeNull();
  });
});
