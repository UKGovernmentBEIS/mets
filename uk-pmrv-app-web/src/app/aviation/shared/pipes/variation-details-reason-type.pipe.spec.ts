import { VariationDetailsReasonTypePipe } from '@aviation/shared/pipes/variation-details-reason-type.pipe';

describe('VariationDetailsReasonTypePipe', () => {
  let pipe: VariationDetailsReasonTypePipe;

  beforeEach(() => (pipe = new VariationDetailsReasonTypePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform types', () => {
    expect(pipe.transform('FOLLOWING_IMPROVING_REPORT')).toEqual(
      'Following an improvement report submitted by the aircraft operator',
    );
    expect(pipe.transform('FAILED_TO_COMPLY_OR_APPLY')).toEqual(
      'Aircraft operator failed to comply with a requirement in the plan, or to apply in accordance with conditions',
    );
    expect(pipe.transform('OTHER')).toEqual('none of the above');
  });
});
