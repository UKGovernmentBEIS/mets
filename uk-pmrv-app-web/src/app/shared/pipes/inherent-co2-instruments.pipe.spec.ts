import { InherentCo2InstrumentsPipe } from './inherent-co2-instruments.pipe';

describe('InherentCo2InstrumentsPipe', () => {
  let pipe: InherentCo2InstrumentsPipe;

  beforeEach(() => (pipe = new InherentCo2InstrumentsPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map types to names', () => {
    expect(pipe.transform('INSTRUMENTS_BELONGING_TO_YOUR_INSTALLATION')).toEqual(
      'Instruments belonging to your installation',
    );
    expect(pipe.transform('INSTRUMENTS_BELONGING_TO_THE_OTHER_INSTALLATION')).toEqual(
      'Instruments belonging to the other installation',
    );

    expect(pipe.transform(null)).toBe('');
  });
});
