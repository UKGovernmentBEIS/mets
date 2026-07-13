import { TagStylePipe } from './tag-style.pipe';

describe('TagStylePipe', () => {
  it('create an instance', () => {
    const pipe = new TagStylePipe();

    expect(pipe).toBeTruthy();
  });

  it('should transform styles based on status', () => {
    const pipe = new TagStylePipe();

    expect(pipe.transform('complete')).toBe('none');
    expect(pipe.transform('accepted')).toBe('none');
    expect(pipe.transform('approved')).toBe('none');
    expect(pipe.transform('granted')).toBe('none');

    expect(pipe.transform('cannot start yet')).toBe('tinted');

    expect(pipe.transform('not started')).toBe('fill');
    expect(pipe.transform('in progress')).toBe('fill');
    expect(pipe.transform('incomplete')).toBe('fill');
    expect(pipe.transform('needs review')).toBe('fill');
    expect(pipe.transform('undecided')).toBe('fill');
    expect(pipe.transform('rejected')).toBe('fill');
    expect(pipe.transform('operator to amend')).toBe('fill');
    expect(pipe.transform('deemed withdrawn')).toBe('fill');
    expect(pipe.transform('withdrawn')).toBe('fill');
  });
});
