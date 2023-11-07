import { MaterialityThresholdTypePipe } from '@aviation/shared/pipes/materiality-threshold-type.pipe';

describe('MaterialityThresholdTypePipe', () => {
  let pipe: MaterialityThresholdTypePipe;

  beforeEach(() => (pipe = new MaterialityThresholdTypePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform fuel uplift supplier record types', () => {
    expect(pipe.transform('THRESHOLD_2_PER_CENT')).toEqual('2%');
    expect(pipe.transform('THRESHOLD_5_PER_CENT')).toEqual('5%');
    expect(pipe.transform(null)).toEqual(null);
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
