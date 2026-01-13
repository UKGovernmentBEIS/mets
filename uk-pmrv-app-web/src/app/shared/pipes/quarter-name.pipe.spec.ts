import { QuarterNamePipe } from './quarter-name.pipe';

describe('QuarterNamePipe', () => {
  let pipe = new QuarterNamePipe();

  beforeEach(() => (pipe = new QuarterNamePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('hould properly transform account types', () => {
    expect(pipe.transform('Q1')).toEqual('January to March');
    expect(pipe.transform('Q2')).toEqual('April to June');
    expect(pipe.transform('Q3')).toEqual('July to September');
    expect(pipe.transform('Q4')).toEqual('October to December');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
