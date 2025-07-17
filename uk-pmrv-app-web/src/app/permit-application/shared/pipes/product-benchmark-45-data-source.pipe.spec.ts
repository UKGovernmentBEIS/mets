import { ProductBenchmark45DataSourcePipe } from './product-benchmark-45-data-source.pipe';

describe('ProductBenchmark45DataSourcePipe', () => {
  const pipe = new ProductBenchmark45DataSourcePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('LEGAL_METROLOGICAL_CONTROL_READING')).toEqual(
      '4.5.(a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU',
    );
    expect(pipe.transform('OPERATOR_CONTROL_DIRECT_READING_NOT_A')).toEqual(
      "4.5.(b) Readings of measuring instruments under the operator's control for direct determination of a data set not falling under point a",
    );
    expect(pipe.transform('NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A')).toEqual(
      "4.5.(c) Readings of measuring instruments not under the operator's control for direct determination of a data set not falling under point a",
    );
    expect(pipe.transform('INDIRECT_DETERMINATION_READING')).toEqual(
      '4.5.(d) Readings of measuring instruments for indirect determination of a data set, provided that an appropriate correlation between the measurement and the data set in question is established in line with section 3.4 of Annex VII (FAR)',
    );
    expect(pipe.transform('PROXY_CALCULATION_METHOD')).toEqual(
      '4.5.(e) Calculation of a proxy for the determining net amounts of measurable heat in accordance with Method 3 of section 7.2 in Annex VII (FAR)',
    );
    expect(pipe.transform('OTHER_METHODS')).toEqual(
      '4.5.(f) Other methods, in particular for historical data or where no other data source can be identified by the operator as available',
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
