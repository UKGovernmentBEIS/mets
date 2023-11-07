import { NotVerifiedReasonTypePipe } from '@shared/pipes/not-verified-reason-type.pipe';

describe('NotVerifiedReasonTypePipe', () => {
  const pipe = new NotVerifiedReasonTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('ANOTHER_REASON', 'Other')).toEqual('Other');
    expect(pipe.transform('DATA_OR_INFORMATION_LIMITATIONS')).toEqual(
      'limitations in the data or information made available for verification',
    );
    expect(pipe.transform('NOT_APPROVED_MONITORING_PLAN')).toEqual(
      'the monitoring plan is not approved by the competent authority',
    );
    expect(pipe.transform('SCOPE_LIMITATIONS_CLARITY')).toEqual('limitations of scope due to lack of clarity');
    expect(pipe.transform('SCOPE_LIMITATIONS_MONITORING_PLAN')).toEqual(
      'limitations of scope of the approved monitoring plan',
    );
    expect(pipe.transform('UNCORRECTED_MATERIAL_MISSTATEMENT')).toEqual(
      'uncorrected material mis-statment (individual or in aggregate)',
    );
    expect(pipe.transform('UNCORRECTED_MATERIAL_NON_CONFORMITY')).toEqual(
      'uncorrected material non-comformity (individual or in aggregate)',
    );
    expect(pipe.transform(undefined)).toEqual('');
  });
});
