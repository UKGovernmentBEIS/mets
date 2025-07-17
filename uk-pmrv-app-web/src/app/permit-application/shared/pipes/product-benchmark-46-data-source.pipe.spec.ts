import { ProductBenchmark46DataSourcePipe } from './product-benchmark-46-data-source.pipe';

describe('ProductBenchmark46DataSourcePipe', () => {
  const pipe = new ProductBenchmark46DataSourcePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('CALCULATION_METHOD_MONITORING_PLAN')).toEqual(
      '4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
    );
    expect(pipe.transform('LABORATORY_ANALYSES_SECTION_61')).toEqual(
      '4.6.(b) Laboratory analyses in accordance with section 6.1 of  Annex VII (FAR)',
    );
    expect(pipe.transform('SIMPLIFIED_LABORATORY_ANALYSES_SECTION_62')).toEqual(
      '4.6.(c) Simplified laboratory analyses in accordance with section 6.2 of Annex VII (FAR)',
    );
    expect(pipe.transform('CONSTANT_VALUES_STANDARD_SUPPLIER')).toEqual(
      '4.6.(d) Constant values based on one of the following data sources: standard factors, literature values, values specified and guaranteed by the supplier',
    );
    expect(pipe.transform('CONSTANT_VALUES_SCIENTIFIC_EVIDENCE')).toEqual(
      '4.6.(e) Constant values based on one of the following data sources: standard/stoichiometric factors, analysis-based values, other values based on scientific evidence',
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
