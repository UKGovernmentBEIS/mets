import { OperatorDetailsActivitiesDescriptionPipe } from '@aviation/shared/pipes/operator-details-activities-description.pipe';

describe('OperatorDetailsActivitiesDescriptionPipe', () => {
  let pipe: OperatorDetailsActivitiesDescriptionPipe;

  beforeEach(() => (pipe = new OperatorDetailsActivitiesDescriptionPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform activities descriptions', () => {
    expect(pipe.transform('COMMERCIAL')).toEqual('Commercial');
    expect(pipe.transform('NON_COMMERCIAL')).toEqual('Non-commercial');
    expect(pipe.transform('SCHEDULED')).toEqual('Scheduled flights');

    expect(pipe.transform('SCHEDULED')).toEqual('Scheduled flights');
    expect(pipe.transform('NON_SCHEDULED')).toEqual('Non-scheduled flights');
    expect(pipe.transform('UK_DOMESTIC')).toEqual('UK domestic');
    expect(pipe.transform('UK_TO_EEA_COUNTRIES')).toEqual('UK to EEA countries');
    expect(pipe.transform(null)).toEqual(null);
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
