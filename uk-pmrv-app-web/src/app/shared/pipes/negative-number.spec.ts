import { NegativeNumberPipe } from './negative-number.pipe';

describe('NegativeNumberPipe', () => {
  const pipe = new NegativeNumberPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform(5)).toEqual(-5);
  });
});
