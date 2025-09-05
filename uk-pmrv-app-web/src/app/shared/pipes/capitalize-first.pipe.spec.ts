import { CapitalizeFirstPipe } from './capitalize-first.pipe';

describe('CapitalizeFirstPipe', () => {
  let pipe: CapitalizeFirstPipe;

  beforeEach(() => {
    pipe = new CapitalizeFirstPipe();
  });
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should capitalize only the first letter', () => {
    expect(pipe.transform('CAPITALIZE First')).toBe('Capitalize first');
  });

  it('should preserve existing spaces and only replace all underscore', () => {
    expect(pipe.transform('foo bar_baz')).toBe('Foo bar baz');
    expect(pipe.transform('FOO   BAR_baz')).toBe('Foo   bar baz');
    expect(pipe.transform('foo_bar baz')).toBe('Foo bar baz');
  });

  it('should capitalize the first letter and lowercase the rest', () => {
    expect(pipe.transform('hello')).toBe('Hello');
    expect(pipe.transform('hELLO')).toBe('Hello');
  });

  it('should replace a single underscore with space and capitalize', () => {
    expect(pipe.transform('hello_world')).toBe('Hello world');
    expect(pipe.transform('HELLO_WORLD')).toBe('Hello world');
  });

  it('should handle empty string', () => {
    expect(pipe.transform('')).toBe('');
  });

  it('should handle null and undefined', () => {
    expect(pipe.transform(null as any)).toBe('');
    expect(pipe.transform(undefined as any)).toBe('');
  });

  it('should not affect strings that are already capitalized', () => {
    expect(pipe.transform('Hello')).toBe('Hello');
  });

  it('should handle single character', () => {
    expect(pipe.transform('a')).toBe('A');
    expect(pipe.transform('A')).toBe('A');
  });
});
