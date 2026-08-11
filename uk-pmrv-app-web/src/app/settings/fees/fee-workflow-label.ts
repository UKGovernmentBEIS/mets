import { FeeRowDTO } from 'pmrv-api';

type RequestType = FeeRowDTO['requestType'];
type FeeType = FeeRowDTO['feeType'];

/** The fee rows currently shown on the Settings > Fees page for Installation and Aviation. */
const REQUEST_TYPE_LABELS: Partial<Record<RequestType, string>> = {
  NER: 'New entrant reserve (GHGE)',
  PERMIT_REVOCATION: 'Permit revocation (GHGE and HSE)',
  PERMIT_SURRENDER: 'Permit surrender (GHGE and HSE)',
  PERMIT_TRANSFER_A: 'Permit transfer (transferring operator, GHGE and HSE)',
  PERMIT_TRANSFER_B: 'Permit transfer (receiving operator, GHGE and HSE)',
  PERMIT_VARIATION: 'Permit variation (GHGE and HSE)',
  HSE_TI: 'Target increase (HSE)',
  EMP_ISSUANCE_UKETS: 'EMP application (UK ETS)',
  EMP_ISSUANCE_CORSIA: 'EMP application (CORSIA)',
  EMP_VARIATION_UKETS: 'EMP variation (UK ETS)',
  EMP_VARIATION_CORSIA: 'EMP variation (CORSIA)',
};

const PERMIT_ISSUANCE_LABELS: Partial<Record<FeeType, string>> = {
  CAT_A: 'Permit application (GHGE category A)',
  CAT_B: 'Permit application (GHGE category B)',
  CAT_C: 'Permit application (GHGE category C)',
  HSE: 'Permit application (HSE)',
};

/** Returns null for any requestType/feeType combination not shown on the Fees page. */
export function getFeeWorkflowLabel(requestType: RequestType, feeType: FeeType): string | null {
  if (requestType === 'PERMIT_ISSUANCE') {
    return PERMIT_ISSUANCE_LABELS[feeType] ?? null;
  }

  return REQUEST_TYPE_LABELS[requestType] ?? null;
}
