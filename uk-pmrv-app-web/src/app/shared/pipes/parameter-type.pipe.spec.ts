import { ParameterTypePipe } from './parameter-type.pipe';

describe('ParameterTypePipe', () => {
  const pipe = new ParameterTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('ACTIVITY_DATA')).toEqual('Activity data');
    expect(pipe.transform('NET_CALORIFIC_VALUE')).toEqual('Net calorific value');
    expect(pipe.transform('CARBON_CONTENT')).toEqual('Carbon content');
  });
});
