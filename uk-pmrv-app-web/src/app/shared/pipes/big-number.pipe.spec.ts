import { BigNumberPipe } from './big-number.pipe';

describe('BigNumberPipe', () => {
  const pipe = new BigNumberPipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });
  it('should default to 5 decimal value', () => {
    expect(pipe.transform(1.23456789)).toEqual('1.23457');
  });
  it('should return NaN when input is invalid', () => {
    expect(pipe.transform('abc')).toEqual('NaN');
    expect(pipe.transform(null)).toEqual('NaN');
    expect(pipe.transform(undefined)).toEqual('NaN');
  });
  it("should correctly parse exponential notation", () => {
    expect(pipe.transform(1.23456789e+5)).toEqual('123456.789');
  });
  it('should remove trailing zeros', () => {
    expect(pipe.transform(1.23450000)).toEqual('1.2345');
  });
});
