import { ProductBenchmark44DataSourcePipe } from './product-benchmark-44-data-source.pipe';

describe('ProductBenchmark44DataSourcePipe', () => {
  const pipe = new ProductBenchmark44DataSourcePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('METHOD_MONITORING_PLAN')).toEqual(
      '4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
    );
    expect(pipe.transform('LEGAL_METROLOGICAL_CONTROL')).toEqual(
      '4.4.(b) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU for direct determination of a data set',
    );
    expect(pipe.transform('OPERATOR_CONTROL_NOT_POINT_B')).toEqual(
      '4.4.(c) Readings of measuring instruments under the operators control for direct determination of a data set not falling under point b',
    );
    expect(pipe.transform('NOT_OPERATOR_CONTROL_NOT_POINT_B')).toEqual(
      '4.4.(d) Readings of measuring instruments not under the operators control for direct determination of a data set not falling under point b',
    );
    expect(pipe.transform('INDIRECT_DETERMINATION')).toEqual(
      '4.4.(e) Readings of measuring instruments for indirect determination of a data set, provided that an appropriate correlation between the measurement and the data set in question is established in line with section 3.4 of this Annex',
    );
    expect(pipe.transform('OTHER_METHODS')).toEqual(
      '4.4.(f) Other methods, in particular for historical data or where no other data source can be identified by the operator as available',
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
