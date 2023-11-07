import { NotVerifiedDecisionReasonTypePipe } from '@aviation/shared/pipes/not-verified-reason.pipe';

describe('NotVerifiedDecisionReasonTypePipe', () => {
  let pipe: NotVerifiedDecisionReasonTypePipe;

  beforeEach(() => (pipe = new NotVerifiedDecisionReasonTypePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform review determination types', () => {
    expect(pipe.transform('UNCORRECTED_MATERIAL_MISSTATEMENT')).toEqual(
      'An uncorrected material misstatement (individual or in aggregate)',
    );
    expect(pipe.transform('UNCORRECTED_MATERIAL_NON_CONFORMITY')).toEqual(
      'An uncorrected material non-conformity (individual or in aggregate)',
    );
    expect(pipe.transform('VERIFICATION_DATA_OR_INFORMATION_LIMITATIONS', 'my details')).toEqual(
      'Limitations in the data or information made available for verification - my details',
    );
    expect(pipe.transform('SCOPE_LIMITATIONS_DUE_TO_LACK_OF_CLARITY', 'my details')).toEqual(
      'Limitations of scope due to lack of clarity - my details',
    );
    expect(pipe.transform('SCOPE_LIMITATIONS_OF_APPROVED_MONITORING_PLAN', 'my details')).toEqual(
      'Limitations of scope of the approved monitoring plan - my details',
    );
    expect(pipe.transform('NOT_APPROVED_MONITORING_PLAN_BY_REGULATOR', 'my details')).toEqual(
      'The emissions monitoring plan is not approved by the regulator - my details',
    );
    expect(pipe.transform('ANOTHER_REASON', 'my details')).toEqual('Another reason - my details');
    expect(pipe.transform(null)).toEqual('');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
