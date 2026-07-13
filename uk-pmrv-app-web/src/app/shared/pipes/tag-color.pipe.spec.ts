import { TagColorPipe } from './tag-color.pipe';

describe('TagColorPipe', () => {
  it('create an instance', () => {
    const pipe = new TagColorPipe();

    expect(pipe).toBeTruthy();
  });

  it('should transform colors based on status', () => {
    const pipe = new TagColorPipe();

    expect(pipe.transform('cannot start yet')).toBe('grey');
    expect(pipe.transform('CANCELLED')).toBe('grey');
    expect(pipe.transform('CLOSED')).toBe('grey');

    expect(pipe.transform('granted')).toBe('green');
    expect(pipe.transform('accepted')).toBe('green');
    expect(pipe.transform('complete')).toBe('green');
    expect(pipe.transform('COMPLETED')).toBe('green');
    expect(pipe.transform('approved')).toBe('green');
    expect(pipe.transform('APPROVED')).toBe('green');

    expect(pipe.transform('in progress')).toBe('teal');
    expect(pipe.transform('IN_PROGRESS')).toBe('teal');

    expect(pipe.transform('not started')).toBe('blue');
    expect(pipe.transform('undecided')).toBe('blue');

    expect(pipe.transform('rejected')).toBe('red');
    expect(pipe.transform('incomplete')).toBe('red');
    expect(pipe.transform('REJECTED')).toBe('red');

    expect(pipe.transform('deemed withdrawn')).toBe('orange');
    expect(pipe.transform('withdrawn')).toBe('orange');
    expect(pipe.transform('WITHDRAWN')).toBe('orange');

    expect(pipe.transform('needs review')).toBe('yellow');
    expect(pipe.transform('operator to amend')).toBe('yellow');
  });
});
