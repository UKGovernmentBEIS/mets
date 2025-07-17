import { StatusApplicationTypePipe } from './statusApplicationType.pipe';

describe('StatusApplicationTypePipe', () => {
  const pipe = new StatusApplicationTypePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('NONE')).toEqual('No');
    expect(pipe.transform('HSE')).toEqual('Yes, I am applying for HSE status');
    expect(pipe.transform('USE')).toEqual('Yes, I am applying for USE status');
  });

  it('should handle empty value', () => {
    const transformation = pipe.transform(null);

    expect(transformation).toEqual('');
  });

  it('should handle undefined value', () => {
    const transformation = pipe.transform(undefined);

    expect(transformation).toEqual('');
  });
});
