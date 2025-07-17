import { ReviewGroupDecisionPipe } from './review-group-decision.pipe';

describe('ReviewGroupDecisionPipe', () => {
  let pipe: ReviewGroupDecisionPipe;

  beforeEach(async () => {
    pipe = new ReviewGroupDecisionPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('ACCEPTED')).toEqual('Accepted');
    expect(pipe.transform('REJECTED')).toEqual('Rejected');
    expect(pipe.transform('OPERATOR_AMENDS_NEEDED')).toEqual('Operator amends needed');
    expect(pipe.transform('PERMANENT_CESSATION')).toEqual('Permanent cessation');
    expect(pipe.transform('TEMPORARY_CESSATION')).toEqual('Temporary cessation');
    expect(pipe.transform('CESSATION_TREATED_AS_PERMANENT')).toEqual('Cessation treated as permanent');
    expect(pipe.transform('NOT_CESSATION')).toEqual('Not cessation');
  });
});
