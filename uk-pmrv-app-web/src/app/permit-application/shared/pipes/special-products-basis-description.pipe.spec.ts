import { SpecialProductsBasisDescriptionPipe } from './special-products-basis-description.pipe';

describe('RefineryProductsRelevantBasisDescriptionPipe', () => {
  let pipe: SpecialProductsBasisDescriptionPipe;

  beforeEach(() => {
    pipe = new SpecialProductsBasisDescriptionPipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform known basis abbreviations to their descriptions', () => {
    expect(pipe.transform('F')).toBe('Net fresh feed');
    expect(pipe.transform('F (MNm3)')).toBe('Net fresh feed (MNm3)');
    expect(pipe.transform('R')).toBe('Reactor feed (includes recycle)');
    expect(pipe.transform('P')).toBe('Product feed');
    expect(pipe.transform('P (MNm3 O2)')).toBe('Product feed (MNm3 O2)');
    expect(pipe.transform('SG')).toBe('Synthesis gas production for POX units');
  });

  it('should return the original basis if abbreviation is unknown', () => {
    expect(pipe.transform('XYZ')).toBe('XYZ');
    expect(pipe.transform('')).toBe('');
  });

  it('should handle null and undefined inputs gracefully', () => {
    expect(pipe.transform(null as any)).toBe(null);
    expect(pipe.transform(undefined as any)).toBe(undefined);
  });
});
