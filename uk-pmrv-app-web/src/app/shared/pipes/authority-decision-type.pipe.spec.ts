import { AuthorityDecisionTypePipe } from '@shared/pipes/authority-decision-type.pipe';

describe('AuthorityDecisionTypePipe', () => {
  let pipe: AuthorityDecisionTypePipe;

  beforeEach(() => (pipe = new AuthorityDecisionTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform decision types', () => {
    expect(pipe.transform('VALID')).toEqual('Approved');
    expect(pipe.transform('VALID_WITH_CORRECTIONS')).toEqual('Approved with corrections');
    expect(pipe.transform('INVALID')).toEqual('Not approved');
    expect(pipe.transform(null)).toEqual(null);
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
