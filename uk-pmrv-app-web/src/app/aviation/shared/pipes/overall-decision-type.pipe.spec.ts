import { AviationOverallDecisionTypePipe } from '@aviation/shared/pipes/overall-decision-type.pipe';

describe('AviationOverallDecisionTypePipe', () => {
  let pipe: AviationOverallDecisionTypePipe;

  beforeEach(() => (pipe = new AviationOverallDecisionTypePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform review determination types', () => {
    expect(pipe.transform('NOT_VERIFIED')).toEqual('Not verified');
    expect(pipe.transform('NOT_VERIFIED', true)).toEqual('Verified as not satisfactory');
    expect(pipe.transform('VERIFIED_AS_SATISFACTORY')).toEqual('Verified as satisfactory');
    expect(pipe.transform('VERIFIED_AS_SATISFACTORY_WITH_COMMENTS')).toEqual('Verified with comments');
    expect(pipe.transform(null)).toEqual('');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
