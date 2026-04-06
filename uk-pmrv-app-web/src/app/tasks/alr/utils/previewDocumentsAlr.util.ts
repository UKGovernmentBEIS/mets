import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { ALRApplicationAuthorityReviewOutcome, DoalDetermination, RequestTaskActionProcessDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';

export function getAlrPreviewDocumentsInfo(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  determinationStatus: DoalDetermination['type'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'ALR_PROCEED_TO_AUTHORITY':
      switch (determinationStatus) {
        case 'PROCEED_TO_AUTHORITY':
          return [
            {
              documentType: 'ALR_SUBMITTED',
              filename: letterPreview,
            },
          ];
      }
  }
}

export function getAlrAuthorityPreviewDocumentsInfo(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  determinationStatus: ALRApplicationAuthorityReviewOutcome['authorityResponse']['type'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'ALR_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION':
      switch (determinationStatus) {
        case 'VALID':
        case 'VALID_WITH_CORRECTIONS':
          return [
            {
              documentType: 'ALR_ACCEPTED',
              filename: letterPreview,
            },
          ];

        case 'INVALID':
          return [
            {
              documentType: 'ALR_REJECTED',
              filename: letterPreview,
            },
          ];
      }
  }
}
