import { VerificationBodyStatusPipe } from './verification-body-status.pipe';

describe('VerificationBodyStatusPipe', () => {
  const pipe = new VerificationBodyStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform verification body status', () => {
    let transformation = pipe.transform(null);
    expect(transformation).toEqual(null);

    transformation = pipe.transform(undefined);
    expect(transformation).toEqual(null);

    transformation = pipe.transform('ACTIVE');
    expect(transformation).toEqual('Active');

    transformation = pipe.transform('PENDING');
    expect(transformation).toEqual('Pending');

    transformation = pipe.transform('DISABLED');
    expect(transformation).toEqual('Disabled');
  });
});
