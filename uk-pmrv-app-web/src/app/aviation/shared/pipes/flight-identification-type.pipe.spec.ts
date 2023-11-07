import { FlightIdentificationTypePipe } from './flight-identification-type.pipe';

describe('FlightIdentificationTypePipe', () => {
  let pipe: FlightIdentificationTypePipe;

  beforeEach(() => (pipe = new FlightIdentificationTypePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform flight identification types', () => {
    expect(pipe.transform('INTERNATIONAL_CIVIL_AVIATION_ORGANISATION')).toEqual(
      'International Civil Aviation Organisation (ICAO) designators used in item 7 of the flight plan',
    );
    expect(pipe.transform('AIRCRAFT_REGISTRATION_MARKINGS')).toEqual(
      'Nationality or common mark, registration mark of an aeroplane or any other code used in item 7 of the flight plan',
    );
  });
});
