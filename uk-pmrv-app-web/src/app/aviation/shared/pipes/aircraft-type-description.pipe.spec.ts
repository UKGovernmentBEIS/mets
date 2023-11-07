import { AircraftTypeDescriptionPipe } from '@aviation/shared/pipes/aircraft-type-description.pipe';

import { AircraftTypeDTO } from 'pmrv-api';

describe('AircraftTypeDescriptionPipe', () => {
  let pipe: AircraftTypeDescriptionPipe;

  const aircraftType = {
    manufacturer: '3XTRIM',
    model: 'Ultra',
    designatorType: 'UL45',
  } as AircraftTypeDTO;

  beforeEach(() => (pipe = new AircraftTypeDescriptionPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform aircraft types', () => {
    expect(pipe.transform(aircraftType, 'label')).toEqual('Ultra (UL45)');
    expect(pipe.transform(aircraftType, 'hint')).toEqual('3XTRIM');
    expect(pipe.transform(aircraftType, 'full')).toEqual('3XTRIM Ultra (UL45)');
  });
});
