import { IncludesAnyPipe } from './includes-any.pipe';

describe('IncludesAnyPipe', () => {
  const pipe = new IncludesAnyPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('return true', () => {
    expect(pipe.transform([1, 2, 3], [1, 2])).toEqual(true);
  });

  it('return false', () => {
    expect(pipe.transform([1, 2, 3], [4, 5])).toEqual(false);
  });
});
