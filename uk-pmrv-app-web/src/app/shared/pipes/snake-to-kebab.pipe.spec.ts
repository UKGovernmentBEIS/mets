import { SnakeToKebabPipe } from './snake-to-kebab.pipe';

describe('SnakeToKebabPipe', () => {
  let pipe: SnakeToKebabPipe;

  beforeEach(() => {
    pipe = new SnakeToKebabPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform "ASSIGNMENT_OF_RESPONSIBILITIES" to "assignment-of-responsibilities"', () => {
    const input = 'ASSIGNMENT_OF_RESPONSIBILITIES';
    const output = pipe.transform(input);
    expect(output).toBe('assignment-of-responsibilities');
  });

  it('should transform "CONTROL_ACTIVITIES" to "control-activities"', () => {
    const input = 'CONTROL_ACTIVITIES';
    const output = pipe.transform(input);
    expect(output).toBe('control-activities');
  });

  it('should handle a string with no underscores', () => {
    const input = 'MONITORING';
    const output = pipe.transform(input);
    expect(output).toBe('monitoring');
  });

  it('should return an empty string if provided null', () => {
    const input = null as any;
    const output = pipe.transform(input);
    expect(output).toBe('');
  });

  it('should return an empty string if provided undefined', () => {
    const input = undefined as any;
    const output = pipe.transform(input);
    expect(output).toBe('');
  });

  it('should return an empty string if provided an empty string', () => {
    const input = '';
    const output = pipe.transform(input);
    expect(output).toBe('');
  });
});
