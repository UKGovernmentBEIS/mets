import { GovukTableColumn } from 'govuk-components';

export const defaultColumns: GovukTableColumn[] = [
  {
    header: 'State A',
    field: 'stateA',
  },
  {
    header: 'State B',
    field: 'stateB',
  },
];

export const exampleColumns: GovukTableColumn[] = [
  {
    header: 'Column A',
    field: 'stateA',
  },
  {
    header: 'Column B',
    field: 'stateB',
  },
];

export const exampleData: any = [
  {
    stateA: 'Canada',
    stateB: 'Mexico',
  },
  {
    stateA: 'Greece',
    stateB: 'Italy',
  },
];
