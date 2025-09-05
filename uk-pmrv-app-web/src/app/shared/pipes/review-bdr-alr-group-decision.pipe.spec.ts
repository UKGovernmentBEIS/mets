import { ReviewBdrAlrGroupDecisionPipe } from './review-bdr-alr-group-decision.pipe';

describe('reviewBdrAlrGroupDecision', () => {
  let pipe: ReviewBdrAlrGroupDecisionPipe;

  beforeEach(async () => {
    pipe = new ReviewBdrAlrGroupDecisionPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('ACCEPTED')).toEqual('Accepted');
    expect(pipe.transform('OPERATOR_AMENDS_NEEDED')).toEqual('Operator amendments needed');
  });
});
