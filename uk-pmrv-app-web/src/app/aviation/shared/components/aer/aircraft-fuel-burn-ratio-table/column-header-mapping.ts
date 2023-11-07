import { GovukTableColumn } from 'govuk-components';

import { AviationAerCorsiaAircraftTypeDetails } from 'pmrv-api';

export const defaultColumns: GovukTableColumn<AviationAerCorsiaAircraftTypeDetails>[] = [
  {
    header: 'Aircraft type designator',
    field: 'designator',
  },
  {
    header: 'Aircraft sub-type',
    field: 'subtype',
  },
  {
    header: 'Fuel burn (tonnes per hour)',
    field: 'fuelBurnRatio',
  },
];

export const rowExamples: { [key in keyof AviationAerCorsiaAircraftTypeDetails]: string }[] = [
  {
    designator: 'Aircraft type designator',
    subtype: 'Aircraft sub type (leave blank if not applicable)',
    fuelBurnRatio: 'Fuel burn ratio (tonnes per hour)',
  },
];

export const columnHeaderTitles: GovukTableColumn<AviationAerCorsiaAircraftTypeDetails>[] = [
  {
    header: 'Column A',
    field: 'designator',
  },
  {
    header: 'Column B',
    field: 'subtype',
  },
  {
    header: 'Column C',
    field: 'fuelBurnRatio',
  },
];
