import { AircraftTypeDetails, AircraftTypeInfo } from 'pmrv-api';

import { FuelTypes } from '../components/emp/emission-sources/aircraft-type/fuel-types';
import { AircraftTypeFuelMethodPipe } from './aircraft-type-method.pipe';

const mockAircraftTypeInfo: AircraftTypeInfo = {
  designatorType: 'example designatorType',
  manufacturer: 'example manufacturer',
  model: 'example model',
};
const mockAircraftType: AircraftTypeDetails = {
  aircraftTypeInfo: mockAircraftTypeInfo,
  fuelTypes: ['JET_GASOLINE'] as FuelTypes[],
  isCurrentlyUsed: true,
  numberOfAircrafts: 12,
  subtype: '',
  fuelConsumptionMeasuringMethod: 'METHOD_A',
};
const mockAircraftTypeMissingFuelConsumptionMethod: AircraftTypeDetails = {
  aircraftTypeInfo: mockAircraftTypeInfo,
  fuelTypes: ['JET_GASOLINE'] as FuelTypes[],
  isCurrentlyUsed: true,
  numberOfAircrafts: 12,
  subtype: '',
};
describe('aircraftTypeMethodTypes', () => {
  let pipe: AircraftTypeFuelMethodPipe;
  beforeEach(() => {
    pipe = new AircraftTypeFuelMethodPipe();
  });
  it('should be truthy', () => {
    expect(pipe).toBeTruthy();
  });
  it('should return format METHOD_A', () => {
    expect(pipe.transform(mockAircraftType, true)).toEqual('Method A');
  });
  it("should return 'Missing' when no fuelConsumptionMeasuringMethod", () => {
    expect(pipe.transform(mockAircraftTypeMissingFuelConsumptionMethod, true)).toEqual('Missing');
  });
});
