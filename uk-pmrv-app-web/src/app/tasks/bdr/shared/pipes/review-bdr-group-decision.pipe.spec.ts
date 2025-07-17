import { ReviewBdrGroupDecisionPipe } from './review-bdr-group-decision.pipe';

describe('ReviewBdrGroupDecisionPipe', () => {
  let pipe: ReviewBdrGroupDecisionPipe;

  beforeEach(async () => {
    pipe = new ReviewBdrGroupDecisionPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('ACCEPTED')).toEqual('Accepted');
    expect(pipe.transform('OPERATOR_AMENDS_NEEDED')).toEqual('Operator amendments needed');
  });
});
