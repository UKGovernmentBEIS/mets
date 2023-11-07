import { AviationNamePipePipe } from './aviation-name-pipe.pipe';

describe('AviationNamePipePipe', () => {
  it('create an instance', () => {
    const pipe = new AviationNamePipePipe();
    expect(pipe).toBeTruthy();
  });
  it('should return correct value for valid ETS', () => {
    const pipe = new AviationNamePipePipe();
    expect(pipe.transform('CORSIA')).toEqual('CORSIA');
    expect(pipe.transform('UK_ETS_AVIATION')).toEqual('UK ETS');
  });

  it('should return null value when invalid ETS', () => {
    const pipe = new AviationNamePipePipe();
    expect(pipe.transform('INVALID' as any)).toEqual(null);
  });
});
