import { AircraftTypeFuelTypesPipe } from '@aviation/shared/pipes/aircraft-type-fuel-types.pipe';

import { AircraftTypeDetails } from 'pmrv-api';

describe('AircraftTypeFuelTypesPipe', () => {
  let pipe: AircraftTypeFuelTypesPipe;

  const aircraftType = {
    fuelTypes: ['JET_KEROSENE', 'JET_GASOLINE', 'AVIATION_GASOLINE', 'OTHER'],
  } as AircraftTypeDetails;

  beforeEach(() => (pipe = new AircraftTypeFuelTypesPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform fuel types', () => {
    expect(pipe.transform(aircraftType)).toEqual(['Jet kerosene', 'Jet gasoline', 'Aviation gasoline', 'Other']);

    aircraftType.fuelTypes = [];
    expect(pipe.transform(aircraftType)).toEqual([]);
  });
});
