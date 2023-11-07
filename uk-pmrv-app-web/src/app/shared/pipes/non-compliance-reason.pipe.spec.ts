import { NonComplianceReasonPipe } from './non-compliance-reason.pipe';

describe('NonComplianceReasonPipe', () => {
  const pipe = new NonComplianceReasonPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('CARRYING_OUT_A_REGULATED_ACTIVITY_WITHOUT_A_PERMIT')).toEqual(
      'Installations - carrying out a regulated activity without a permit',
    );
    expect(pipe.transform('FAILURE_TO_COMPLY_WITH_THE_CONDITION_OF_A_PERMIT')).toEqual(
      'Installations - failure to comply with a condition of a permit or surrender / revocation notice (other)',
    );
    expect(pipe.transform('FAILURE_TO_SURRENDER_A_PERMIT')).toEqual('Installations - failure to surrender a permit');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
