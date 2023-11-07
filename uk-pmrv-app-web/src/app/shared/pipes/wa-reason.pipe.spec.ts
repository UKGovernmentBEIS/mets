import { WaReasonDescriptionPipe } from './wa-reason.pipe';

describe('WaReasonDescriptionPipe', () => {
  let pipe: WaReasonDescriptionPipe;

  beforeEach(() => {
    pipe = new WaReasonDescriptionPipe();
  });

  it('should transform the value to the corresponding description', () => {
    expect(pipe.transform('ASSESSING_A_RENUNCIATION_NOTICE')).toBe('Assessing a renunciation notice');
    expect(pipe.transform('ASSESSING_A_SPLIT_OR_A_MERGER')).toBe('Assessing a split or a merger');
    expect(pipe.transform('ASSESSING_TRANSFER_OF_AVIATION_FREE_ALLOCATION_UNDER_ARTICLE_34Q')).toBe(
      'Assessing transfer of aviation free allocation under article 34Q',
    );
  });
});
