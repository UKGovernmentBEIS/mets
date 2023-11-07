import { OperatorDetailsFlightIdentificationTypePipe } from '@aviation/shared/pipes/operator-details-flight-identification-type.pipe';

describe('OperatorDetailsFlightIdentificationTypePipe', () => {
  let pipe: OperatorDetailsFlightIdentificationTypePipe;

  beforeEach(() => (pipe = new OperatorDetailsFlightIdentificationTypePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform flight identification types', () => {
    expect(pipe.transform('INTERNATIONAL_CIVIL_AVIATION_ORGANISATION')).toEqual(
      'International Civil Aviation Organisation (ICAO) designators',
    );
    expect(pipe.transform('AIRCRAFT_REGISTRATION_MARKINGS')).toEqual('Aircraft registration markings');

    expect(pipe.transform('INTERNATIONAL_CIVIL_AVIATION_ORGANISATION', true)).toEqual(
      'International Civil Aviation Organisation (ICAO) designators used in item 7 of the flight plan',
    );
    expect(pipe.transform('AIRCRAFT_REGISTRATION_MARKINGS', true)).toEqual(
      'Nationality or common mark, registration mark of an aeroplane or any other code used in item 7 of the flight plan',
    );
    expect(pipe.transform(null)).toEqual(null);
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
