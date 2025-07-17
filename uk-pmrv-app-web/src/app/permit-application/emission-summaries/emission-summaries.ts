import { GovukTableColumn } from 'govuk-components';

import { EmissionSummary } from 'pmrv-api';

export const emissionSummariesColumns: GovukTableColumn<EmissionSummary>[] = [
  { field: 'sourceStream', header: 'Source stream', widthClass: 'govuk-!-width-one-quarter' },
  { field: 'emissionSources', header: 'Emission sources', widthClass: 'govuk-!-width-one-quarter' },
  { field: 'emissionPoints', header: 'Emission points', widthClass: 'govuk-!-width-one-quarter' },
  { field: 'regulatedActivity', header: 'Regulated activity', widthClass: 'govuk-!-width-one-quarter' },
];

export const editColumns: GovukTableColumn[] = [
  { field: 'change', header: '', widthClass: 'govuk-input--width-20' },
  { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
];
