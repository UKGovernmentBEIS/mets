import { EthyleneOxideCFPipe } from './ethylene-oxide-cf.pipe';

describe('EthyleneOxideCFPipe', () => {
  let pipe: EthyleneOxideCFPipe;

  beforeEach(() => {
    pipe = new EthyleneOxideCFPipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform known chemical names to their CF values', () => {
    expect(pipe.transform('ETHYLEN_OXIDE')).toBe('1.000');
    expect(pipe.transform('MONOTHYLENE_GLYCOL')).toBe('0.710');
    expect(pipe.transform('DIETHYLENE_GLYCOL')).toBe('0.830');
    expect(pipe.transform('TRIETHYLENE_GLYCOL')).toBe('0.880');
  });

  it('should return null for unknown chemical names', () => {
    expect(pipe.transform('UNKNOWN_CHEMICAL')).toBeNull();
    expect(pipe.transform('')).toBeNull();
  });

  it('should handle null and undefined inputs gracefully', () => {
    expect(pipe.transform(null as any)).toBeNull();
    expect(pipe.transform(undefined as any)).toBeNull();
  });

  it('should be case-sensitive and only match exact names', () => {
    expect(pipe.transform('ethylen_oxide')).toBeNull();
    expect(pipe.transform('Ethylene_Oxide')).toBeNull();
  });
});
