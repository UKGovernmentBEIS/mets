import { OverallAssessmentTypePipe } from '@shared/pipes/overall-assessment-type.pipe';

describe('OverallAssessmentTypePipe', () => {
  let pipe: OverallAssessmentTypePipe;

  beforeEach(() => (pipe = new OverallAssessmentTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform overall assessment types', () => {
    expect(pipe.transform('NOT_VERIFIED')).toEqual('Not verified');
    expect(pipe.transform('VERIFIED_AS_SATISFACTORY')).toEqual('Verified as satisfactory');
    expect(pipe.transform('VERIFIED_WITH_COMMENTS')).toEqual('Verified with comments');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
