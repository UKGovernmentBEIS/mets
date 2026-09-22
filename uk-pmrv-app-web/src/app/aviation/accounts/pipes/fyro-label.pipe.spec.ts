import { FyroLabelPipe } from './fyro-label.pipe';

describe('FyroLabelPipe', () => {
  it('create an instance', () => {
    const pipe = new FyroLabelPipe();
    expect(pipe).toBeTruthy();
  });

  it('should return the CORSIA label for a CORSIA scheme', () => {
    const pipe = new FyroLabelPipe();
    expect(pipe.transform('CORSIA')).toEqual('First year within the scope of applicability');
  });

  it('should return the UK ETS label for a UK ETS scheme', () => {
    const pipe = new FyroLabelPipe();
    expect(pipe.transform('UK_ETS_AVIATION')).toEqual('First year of reporting obligation');
  });

  it('should return the UK ETS label when the scheme is not set', () => {
    const pipe = new FyroLabelPipe();
    expect(pipe.transform(null)).toEqual('First year of reporting obligation');
  });
});
