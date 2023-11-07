import { UserContactPipe } from './user-contact.pipe';

describe('UserContactPipe', () => {
  let pipe: UserContactPipe;

  beforeEach(() => (pipe = new UserContactPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map account contact types to names', () => {
    expect(pipe.transform('PRIMARY')).toEqual('Primary contact');
    expect(pipe.transform('SECONDARY')).toEqual('Secondary contact');
    expect(pipe.transform('FINANCIAL')).toEqual('Financial contact');
    expect(pipe.transform('SERVICE')).toEqual('Service contact');
    expect(pipe.transform('CA_SITE')).toEqual('Site contact');
    expect(pipe.transform('VB_SITE')).toEqual('Site contact');
    expect(pipe.transform(null)).toEqual('');
  });
});
