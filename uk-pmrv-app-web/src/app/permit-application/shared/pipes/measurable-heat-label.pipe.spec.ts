import { MeasurableHeatLabelPipe } from './measurable-heat-label.pipe';

describe('MeasurableHeatLabelPipe', () => {
  let pipe: MeasurableHeatLabelPipe;

  beforeEach(() => {
    pipe = new MeasurableHeatLabelPipe();
  });

  it('should return "Imported from other sources" for MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES (imported)', () => {
    const result = pipe.transform('MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES', 'imported');
    expect(result).toBe('Imported from other sources');
  });

  it('should return "Net measurable heat flows imported from other sources" for MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES (net)', () => {
    const result = pipe.transform('MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES', 'net');
    expect(result).toBe('Net measurable heat flows imported from other sources');
  });

  it('should return "Imported from product benchmark" for MEASURABLE_HEAT_IMPORTED_PRODUCT_BENCHMARK (imported)', () => {
    const result = pipe.transform('MEASURABLE_HEAT_IMPORTED_PRODUCT_BENCHMARK', 'imported');
    expect(result).toBe('Imported from product benchmark');
  });

  it('should return "Net measurable heat flows imported from product benchmark" for MEASURABLE_HEAT_IMPORTED_PRODUCT_BENCHMARK (net)', () => {
    const result = pipe.transform('MEASURABLE_HEAT_IMPORTED_PRODUCT_BENCHMARK', 'net');
    expect(result).toBe('Net measurable heat flows imported from product benchmark');
  });

  it('should return an empty string for unknown eventKey', () => {
    const result = pipe.transform('UNKNOWN_KEY', 'imported');
    expect(result).toBe('');
  });

  it('should handle MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED (imported)', () => {
    const result = pipe.transform('MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED', 'imported');
    expect(result).toBe('No, we do not have any measurable heat imported');
  });

  it('should return "N/A" for MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED (net)', () => {
    const result = pipe.transform('MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED', 'net');
    expect(result).toBe('N/A');
  });
});
