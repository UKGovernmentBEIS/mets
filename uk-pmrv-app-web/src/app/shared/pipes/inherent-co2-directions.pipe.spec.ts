import { InherentCo2DirectionsPipe } from './inherent-co2-directions.pipe';

describe('InherentCo2DirectionsPipe', () => {
  let pipe: InherentCo2DirectionsPipe;

  beforeEach(() => (pipe = new InherentCo2DirectionsPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map types to names', () => {
    expect(pipe.transform('EXPORTED_TO_ETS_INSTALLATION')).toEqual('Exported to an ETS installation');
    expect(pipe.transform('EXPORTED_TO_NON_ETS_CONSUMER')).toEqual('Exported to a non-ETS consumer');
    expect(pipe.transform('RECEIVED_FROM_ANOTHER_INSTALLATION')).toEqual('Received from another installation');

    expect(pipe.transform(null)).toBe('');
  });
});
