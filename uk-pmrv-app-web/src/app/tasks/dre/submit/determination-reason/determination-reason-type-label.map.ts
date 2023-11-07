import { DreDeterminationReason } from 'pmrv-api';

export const determinationReasonTypesLabelsMap: Record<DreDeterminationReason['type'], string> = {
  VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER: 'Verified report not submitted in accordance with the order',
  CORRECTING_NON_MATERIAL_MISSTATEMENT: 'Correcting a non-material misstatement',
  SURRENDER_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER:
    'Surrender report not submitted in accordance with the order',
  REVOCATION_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER:
    'Revocation report not submitted in accordance with the order',
  OTHER: 'Another reason',
};
