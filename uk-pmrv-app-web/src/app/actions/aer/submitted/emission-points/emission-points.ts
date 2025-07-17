import { GovukTableColumn } from 'govuk-components';

import { EmissionPoint } from 'pmrv-api';

export const pointsColumns: GovukTableColumn<EmissionPoint>[] = [
  { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-half' },
  { field: 'description', header: 'Description', widthClass: 'govuk-!-width-one-half' },
];
