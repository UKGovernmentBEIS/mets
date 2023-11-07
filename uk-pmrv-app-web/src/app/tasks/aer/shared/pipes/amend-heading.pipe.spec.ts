import { AmendHeadingPipe } from './amend-heading.pipe';

describe('AmendHeadingPipe', () => {
  const pipe = new AmendHeadingPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('INSTALLATION_DETAILS')).toEqual('installation details');
    expect(pipe.transform('FUELS_AND_EQUIPMENT')).toEqual('fuels and equipment inventory');
    expect(pipe.transform('ADDITIONAL_INFORMATION')).toEqual('supplemental information');
    expect(pipe.transform('ACTIVITY_LEVEL_REPORT')).toEqual('activity level report');
  });
});
