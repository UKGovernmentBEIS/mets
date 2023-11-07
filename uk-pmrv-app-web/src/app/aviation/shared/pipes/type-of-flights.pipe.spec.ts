import { TypeOfFlightsPipe } from './type-of-flights.pipe';

describe('TypeOfFlightsPipe', () => {
  let pipe: TypeOfFlightsPipe;

  beforeEach(() => (pipe = new TypeOfFlightsPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform types of flights', () => {
    expect(pipe.transform(['SCHEDULED'])).toEqual('Scheduled flights');
    expect(pipe.transform(['NON_SCHEDULED'])).toEqual('Non-scheduled flights');
  });
});
