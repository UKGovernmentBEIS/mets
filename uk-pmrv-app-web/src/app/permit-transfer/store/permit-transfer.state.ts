import { PermitIssuanceState } from '@permit-issuance/store/permit-issuance.state';

import {
  PermitTransferBDetailsConfirmationReviewDecision,
  PermitTransferDetails,
  PermitTransferDetailsConfirmation,
} from 'pmrv-api';

export interface PermitTransferState extends PermitIssuanceState {
  permitTransferDetails?: PermitTransferDetails;
  permitTransferDetailsConfirmation?: PermitTransferDetailsConfirmation;
  permitTransferDetailsConfirmationDecision?: PermitTransferBDetailsConfirmationReviewDecision;
}
