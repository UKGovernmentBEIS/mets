import { ApplicationTypePipe } from './application-type.pipe';

describe('ApplicationTypePipe', () => {
  let pipe: ApplicationTypePipe;

  beforeEach(() => (pipe = new ApplicationTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform account types', () => {
    expect(pipe.transform('NEW_PERMIT')).toEqual('New Permit');
    expect(pipe.transform('TRANSFER')).toEqual('Transfer');
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
