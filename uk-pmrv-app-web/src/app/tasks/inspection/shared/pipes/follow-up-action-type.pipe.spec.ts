import { FollowUpActionTypePipe } from './follow-up-action-type.pipe';

describe('FollowUpActionTypePipe', () => {
  let pipe: FollowUpActionTypePipe;

  beforeEach(() => {
    pipe = new FollowUpActionTypePipe();
  });

  it('should transform follow-up action types correctly', () => {
    expect(pipe).toBeTruthy();

    expect(pipe.transform('MISSTATEMENT')).toBe('Misstatement');
    expect(pipe.transform('NON_CONFORMITY')).toBe('Non-conformity');
    expect(pipe.transform('NON_COMPLIANCE')).toBe('Non-compliance');
    expect(pipe.transform('RECOMMENDED_IMPROVEMENT')).toBe('Recommended improvement');
    expect(pipe.transform('UNRESOLVED_ISSUE_FROM_PREVIOUS_AUDIT')).toBe('Unresolved issue from previous audit');
  });
});
