import { BusinessError } from './business-error';

describe('Business', () => {
  it('should create an instance', () => {
    expect(new BusinessError('')).toBeTruthy();
  });
});
