import { WasteGasActivityPipe } from './waste-gas-activity.pipe';

describe('WasteGasActivityPipe', () => {
  const pipe = new WasteGasActivityPipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('WASTE_GAS_PRODUCED')).toEqual('Waste gas produced');
    expect(pipe.transform('WASTE_GAS_CONSUMED')).toEqual('Waste gas consumed, including safety flaring');
    expect(pipe.transform('WASTE_GAS_FLARED')).toEqual('Waste gas flared, not including safety flaring');
    expect(pipe.transform('WASTE_GAS_IMPORTED')).toEqual('Waste gas imported');
    expect(pipe.transform('WASTE_GAS_EXPORTED')).toEqual('Waste gas exported');
    expect(pipe.transform('NO_WASTE_GAS_ACTIVITIES')).toEqual(
      'No, there are no waste gas activities at this sub-installation',
    );
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
