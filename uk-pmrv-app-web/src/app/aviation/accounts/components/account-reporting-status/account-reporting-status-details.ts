import { GovukTableColumn } from 'govuk-components';

export const ACCOUNT_REPORTING_STATUS_COLUMNS: Array<GovukTableColumn> = [
  {
    field: 'year',
    header: 'Reporting year',
  },
  {
    field: 'status',
    header: 'Status',
  },
  {
    field: 'actions',
    header: undefined,
  },
  {
    field: 'lastUpdate',
    header: 'Last update',
  },
];
