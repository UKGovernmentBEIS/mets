import { TextEllipsisPipe } from './text-ellipsis.pipe';

describe('TextEllipsisPipe', () => {
  let pipe: TextEllipsisPipe;
  const longString =
    'Lorem ipsum dolor sit amet, consectetur adipisicing elit. A ad aliquid amet aspernatur assumenda beatae cupiditate dolore esse expedita incidunt ipsum itaque libero minima molestiae pariatur quod repellat, vel vero?';
  const shortString = 'Lorem';

  beforeEach(() => (pipe = new TextEllipsisPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should truncate appropriate length of chars', () => {
    expect(pipe.transform(longString, 10)).toEqual('Lorem ipsu...');
    expect(pipe.transform(longString)).toEqual(
      'Lorem ipsum dolor sit amet, consectetur adipisicing elit. A ad aliquid amet aspernatur assumenda bea...',
    );
    expect(pipe.transform(shortString, 10)).toEqual(shortString);
  });
});
