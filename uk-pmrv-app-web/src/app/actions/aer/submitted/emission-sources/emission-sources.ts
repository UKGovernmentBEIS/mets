import { GovukTableColumn } from 'govuk-components';

import { EmissionSource } from 'pmrv-api';

export const sourcesColumns: GovukTableColumn<EmissionSource>[] = [
  { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-half' },
  { field: 'description', header: 'Description', widthClass: 'govuk-!-width-one-half' },
];
