import { VerificationBodyTypePipe } from '@shared/pipes/verification-body-type.pipe';

describe('VerificationBodyTypePipe', () => {
  let pipe: VerificationBodyTypePipe;

  beforeEach(() => (pipe = new VerificationBodyTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform emission trading scheme', () => {
    expect(pipe.transform('UK_ETS_INSTALLATIONS')).toEqual('UK ETS Installations');
    expect(pipe.transform('EU_ETS_INSTALLATIONS')).toEqual('EU ETS Installations');
    expect(pipe.transform('UK_ETS_AVIATION')).toEqual('UK ETS Aviation');
    expect(pipe.transform('CORSIA')).toEqual('CORSIA');
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
