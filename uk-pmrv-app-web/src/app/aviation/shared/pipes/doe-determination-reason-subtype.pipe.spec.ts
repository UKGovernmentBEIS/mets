import { AviationDoECorsiaDeterminationReason } from 'pmrv-api';

import { DoeDeterminationReasonSubTypePipe } from './doe-determination-reason-subtype.pipe';

describe('DoeDeterminationReasonSubTypePipe', () => {
  let pipe: DoeDeterminationReasonSubTypePipe;

  beforeEach(() => {
    pipe = new DoeDeterminationReasonSubTypePipe();
  });

  it('should transform a valid value CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS correctly', () => {
    const value: AviationDoECorsiaDeterminationReason['subtypes'][number] =
      'CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS';
    const result = pipe.transform(value);
    expect(result).toBe('Correcting total emissions on all international flights');
  });

  it('should transform a valid value CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS correctly', () => {
    const value: AviationDoECorsiaDeterminationReason['subtypes'][number] =
      'CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS';
    const result = pipe.transform(value);
    expect(result).toBe('Correcting emissions on flights with offsetting requirements');
  });

  it('should transform a valid value CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS correctly', () => {
    const value: AviationDoECorsiaDeterminationReason['subtypes'][number] =
      'CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS';
    const result = pipe.transform(value);
    expect(result).toBe('Correcting emissions related to a claim from CORSIA Eligible Fuels');
  });

  it('should return null for a null value', () => {
    const value: AviationDoECorsiaDeterminationReason['subtypes'][number] = null;
    const result = pipe.transform(value);
    expect(result).toBeNull();
  });

  it('should return null for an unknown value', () => {
    const value: any = 'unknown';
    const result = pipe.transform(value);
    expect(result).toBeNull();
  });
});
