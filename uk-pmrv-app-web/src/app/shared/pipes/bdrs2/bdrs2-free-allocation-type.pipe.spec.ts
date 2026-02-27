import { FreeAllocationTypePipe } from './bdrs2-free-allocation-type.pipe';

describe('FreeAllocationTypePipe', () => {
  const pipe = new FreeAllocationTypePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('CONTINUE_AS_HSE')).toEqual(
      'Yes, I currently hold HSE status and want to continue my application for free allocation but remain on the HSE list',
    );
    expect(pipe.transform('CONTINUE_AS_MAIN_SCHEME_PARTICIPANT')).toEqual(
      'Yes, I hold a GHGE permit and want to continue my application for free allocation as a main scheme participant, or I currently hold HSE status and want to become a main scheme participant from 2027 to 2030',
    );
    expect(pipe.transform('WITHDRAW')).toEqual('No, I want to withdraw my application for free allocation');
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
