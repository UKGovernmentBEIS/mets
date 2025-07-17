import { ConnectionFlowDirectionTypePipe } from './connection-flow-direction.pipe';

describe('ConnectionFlowDirectionTypePipe', () => {
  const pipe = new ConnectionFlowDirectionTypePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('IMPORT')).toEqual('Import: something entering the boundaries of your installation');
    expect(pipe.transform('EXPORT')).toEqual('Export: something leaving the boundaries of your installation');
  });

  it('should handle empty value', () => {
    const transformation = pipe.transform(null);

    expect(transformation).toEqual('');
  });

  it('should handle undefined value', () => {
    const transformation = pipe.transform(undefined);

    expect(transformation).toEqual('');
  });
});
