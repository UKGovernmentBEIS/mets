import { ConnectionEntityTypePipe } from './connection-entity-type.pipe';

describe('ConnectionEntityTypePipe', () => {
  const pipe = new ConnectionEntityTypePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('ETS_INSTALLATION')).toEqual('Installation covered by ETS');
    expect(pipe.transform('NON_ETS_INSTALLATION')).toEqual('Installation outside ETS');
    expect(pipe.transform('NITRIC_ACID_PRODUCTION')).toEqual('Installation producing nitric acid');
    expect(pipe.transform('HEAT_DISTRIBUTION_NETWORK')).toEqual('Heat distribution network');
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
