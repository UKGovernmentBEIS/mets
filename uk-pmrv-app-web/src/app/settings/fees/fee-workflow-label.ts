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
  NRW_CAT_FA_1_TO_2: 'Permit application (GHGE, FA for 1-2 sub installations)',
  NRW_CAT_FA_3_PLUS: 'Permit application (GHGE, FA for 3 or more sub installations)',
};

/** Returns null for any requestType/feeType combination not shown on the Fees page. */
export function getFeeWorkflowLabel(requestType: RequestType, feeType: FeeType): string | null {
  if (requestType === 'PERMIT_ISSUANCE') {
    return PERMIT_ISSUANCE_LABELS[feeType] ?? null;
  }

  return REQUEST_TYPE_LABELS[requestType] ?? null;
}

/**
 * Same lookup as getFeeWorkflowLabel, but falls back to a humanized version of the raw
 * requestType/feeType instead of null - used for fee history, where a past record must never
 * render blank just because its workflow is no longer shown on the current Fees page.
 */
export function getFeeWorkflowLabelWithFallback(requestType: RequestType, feeType: FeeType): string {
  const label = getFeeWorkflowLabel(requestType, feeType);
  if (label) {
    return label;
  }

  const joined = [requestType, feeType]
    .filter(Boolean)
    .flatMap((value) => value.split('_'))
    .map((word) => word.toLowerCase())
    .join(' ');

  return joined ? joined.charAt(0).toUpperCase() + joined.slice(1) : '';
}
