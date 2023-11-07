import { TagColorPipe } from './tag-color.pipe';

describe('TagColorPipe', () => {
  it('create an instance', () => {
    const pipe = new TagColorPipe();

    expect(pipe).toBeTruthy();
  });

  it('should transform colors based on status', () => {
    const pipe = new TagColorPipe();

    expect(pipe.transform('not started')).toBe('grey');
    expect(pipe.transform('undecided')).toBe('grey');
    expect(pipe.transform('cannot start yet')).toBe('grey');
    expect(pipe.transform('CANCELLED')).toBe('grey');
    expect(pipe.transform('CLOSED')).toBe('grey');

    expect(pipe.transform('granted')).toBe('green');
    expect(pipe.transform('accepted')).toBe('green');
    expect(pipe.transform('COMPLETED')).toBe('green');
    expect(pipe.transform('approved')).toBe('green');
    expect(pipe.transform('APPROVED')).toBe('green');

    expect(pipe.transform('operator to amend')).toBe('blue');
    expect(pipe.transform('in progress')).toBe('blue');
    expect(pipe.transform('IN_PROGRESS')).toBe('blue');

    expect(pipe.transform('rejected')).toBe('red');
    expect(pipe.transform('deemed withdrawn')).toBe('red');
    expect(pipe.transform('withdrawn')).toBe('red');
    expect(pipe.transform('incomplete')).toBe('red');
    expect(pipe.transform('WITHDRAWN')).toBe('red');
    expect(pipe.transform('REJECTED')).toBe('red');

    expect(pipe.transform('needs review')).toBe('yellow');

    expect(pipe.transform('complete')).toBeNull();
  });
});
