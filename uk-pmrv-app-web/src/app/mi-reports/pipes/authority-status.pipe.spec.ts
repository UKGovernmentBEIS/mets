import { AuthorityStatusPipe } from './authority-status.pipe';

describe('AuthorityStatusPipe', () => {
  const pipe = new AuthorityStatusPipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform authority status', () => {
    let transformation = pipe.transform(null);
    expect(transformation).toEqual(null);

    transformation = pipe.transform(undefined);
    expect(transformation).toEqual(null);

    transformation = pipe.transform('ACCEPTED');
    expect(transformation).toEqual('Accepted');

    transformation = pipe.transform('ACTIVE');
    expect(transformation).toEqual('Active');

    transformation = pipe.transform('DISABLED');
    expect(transformation).toEqual('Disabled');

    transformation = pipe.transform('PENDING');
    expect(transformation).toEqual('Pending');

    transformation = pipe.transform('TEMP_DISABLED');
    expect(transformation).toEqual('Temporarily Disabled');

    transformation = pipe.transform('TEMP_DISABLED_PENDING');
    expect(transformation).toEqual('Temporarily Disabled Pending');
  });
});
