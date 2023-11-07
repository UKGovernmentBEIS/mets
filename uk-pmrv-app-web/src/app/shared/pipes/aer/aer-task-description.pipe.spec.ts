import { AerTaskDescriptionPipe } from './aer-task-description.pipe';

describe('AerTaskDescriptionPipe', () => {
  const pipe = new AerTaskDescriptionPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('INSTALLATION_DETAILS')).toEqual('Installation details');
    expect(pipe.transform('FUELS_AND_EQUIPMENT')).toEqual('Fuels and equipment inventory');
    expect(pipe.transform('ADDITIONAL_INFORMATION')).toEqual('Additional information');
    expect(pipe.transform('EMISSIONS_SUMMARY')).toEqual('Emissions summary');
  });
});
