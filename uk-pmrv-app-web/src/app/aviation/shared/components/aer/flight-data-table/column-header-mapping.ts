import { GovukTableColumn } from 'govuk-components';

export const defaultColumns: GovukTableColumn[] = [
  {
    header: 'Aerodrome of departure',
    field: 'airportFrom',
  },
  {
    header: 'Aerodrome of arrival',
    field: 'airportTo',
  },
  {
    header: 'Fuel used',
    field: 'fuelType',
  },
  {
    header: 'Fuel consumption (t)',
    field: 'fuelConsumption',
  },
  {
    header: 'Number of flights',
    field: 'flightsNumber',
  },
];

export const exampleColumns: GovukTableColumn[] = [
  {
    header: 'Column A',
    field: 'airportFrom',
  },
  {
    header: 'Column B',
    field: 'airportTo',
  },
  {
    header: 'Column C',
    field: 'fuelType',
  },
  {
    header: 'Column D',
    field: 'fuelConsumption',
  },
  {
    header: 'Column E',
    field: 'flightsNumber',
  },
];

export const exampleData: any = [
  {
    airportFrom: {
      icao: 'Aerodrome of departure ICAO code',
    },
    airportTo: {
      icao: 'Aerodrome of arrival ICAO code',
    },
    fuelType: 'Name of fuel used',
    fuelConsumption: 'Amount of fuel used in tonnes',
    flightsNumber: 'Number of flights',
  },
];
