import _ from 'lodash';

import {
  AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

export function refreshVerificationSectionsCompletedUponSubmitToVerifier(
  payload: AviationAerCorsiaApplicationSubmitRequestTaskPayload,
): AviationAerCorsiaApplicationSubmitRequestTaskPayload['verificationSectionsCompleted'] {
  return payload?.aer?.emissionsReductionClaim?.exist
    ? payload.verificationSectionsCompleted
    : _.omit(payload?.verificationSectionsCompleted, ['emissionsReductionClaimVerification']);
}

export function refreshVerificationSectionsCompletedUponSubmitToRegulator(
  payload: AviationAerCorsiaApplicationSubmitRequestTaskPayload,
): AviationAerCorsiaApplicationSubmitRequestTaskPayload['verificationSectionsCompleted'] {
  return payload?.verificationPerformed === false ? {} : payload.verificationSectionsCompleted;
}

export function refreshVerificationSectionsCompletedUponSubmitAmendToRegulator(
  payload: AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
): AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload['verificationSectionsCompleted'] {
  return payload?.verificationPerformed === false ? {} : payload.verificationSectionsCompleted;
}

export function refreshReviewSectionsCompletedUponRequestVerification(
  payload: AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
): { [key: string]: boolean } {
  return payload?.reportingRequired === true
    ? _.omit(payload?.reviewSectionsCompleted, ['REPORTING_OBLIGATION_DETAILS'])
    : payload.reviewSectionsCompleted;
}

export function refreshReviewSectionsCompletedUponSubmitAmend(
  payload: AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
): AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload['reviewSectionsCompleted'] {
  return payload?.aer?.emissionsReductionClaim?.exist
    ? payload.reviewSectionsCompleted
    : _.omit(payload?.reviewSectionsCompleted, ['ELIGIBLE_FUELS_REDUCTION_CLAIM']);
}
