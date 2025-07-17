import { ProductBenchmark72DataSourcePipe } from './product-benchmark-72-data-source.pipe';

describe('ProductBenchmark72DataSourcePipe', () => {
  const pipe = new ProductBenchmark72DataSourcePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('MEASUREMENTS')).toEqual('7.2. Method 1: Using measurements');
    expect(pipe.transform('DOCUMENTATION')).toEqual('7.2. Method 2: Using documentation');
    expect(pipe.transform('PROXY_MEASURED_EFFICIENCY')).toEqual(
      '7.2. Method 3: Calculation of a proxy based on measured efficiency',
    );
    expect(pipe.transform('PROXY_REFERENCE_EFFICIENCY')).toEqual(
      '7.2. Method 4: Calculating a proxy based on the reference efficiency',
    );
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
