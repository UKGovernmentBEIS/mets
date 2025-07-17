import _ from 'lodash';

import {
  AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload,
  AviationAerUkEtsApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

export function refreshVerificationSectionsCompletedUponSubmitToVerifier(
  payload: AviationAerUkEtsApplicationSubmitRequestTaskPayload,
): AviationAerUkEtsApplicationSubmitRequestTaskPayload['verificationSectionsCompleted'] {
  return payload?.aer?.saf?.exist
    ? payload.verificationSectionsCompleted
    : _.omit(payload?.verificationSectionsCompleted, ['emissionsReductionClaimVerification']);
}

export function refreshVerificationSectionsCompletedUponSubmitToRegulator(
  payload: AviationAerUkEtsApplicationSubmitRequestTaskPayload,
): AviationAerUkEtsApplicationSubmitRequestTaskPayload['verificationSectionsCompleted'] {
  return payload?.verificationPerformed === false ? {} : payload.verificationSectionsCompleted;
}

export function refreshVerificationSectionsCompletedUponSubmitAmendToRegulator(
  payload: AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload,
): AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload['verificationSectionsCompleted'] {
  return payload?.verificationPerformed === false ? {} : payload.verificationSectionsCompleted;
}

export function refreshReviewSectionsCompletedUponRequestVerification(
  payload: AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload,
): { [key: string]: boolean } {
  return payload?.reportingRequired === true
    ? _.omit(payload?.reviewSectionsCompleted, ['REPORTING_OBLIGATION_DETAILS'])
    : payload.reviewSectionsCompleted;
}

export function refreshReviewSectionsCompletedUponSubmitAmend(
  payload: AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload,
): AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload['reviewSectionsCompleted'] {
  return payload?.aer?.saf?.exist
    ? payload.reviewSectionsCompleted
    : _.omit(payload?.reviewSectionsCompleted, ['EMISSIONS_REDUCTION_CLAIM_VERIFICATION']);
}
