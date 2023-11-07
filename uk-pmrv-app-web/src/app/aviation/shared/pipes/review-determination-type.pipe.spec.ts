import { EmpReviewDeterminationTypePipe } from './review-determination-type.pipe';

describe('EmpReviewDeterminationTypePipe', () => {
  let pipe: EmpReviewDeterminationTypePipe;

  beforeEach(() => (pipe = new EmpReviewDeterminationTypePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform review determination types', () => {
    expect(pipe.transform('APPROVED')).toEqual('Approve');
    expect(pipe.transform('DEEMED_WITHDRAWN')).toEqual('Withdraw');
    expect(pipe.transform('REJECTED')).toEqual('Reject');
  });
});
