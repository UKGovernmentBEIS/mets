import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { AuthorityResponse, DoalDetermination, RequestTaskActionProcessDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';

export function getPreviewDocumentsInfo(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  determinationStatus: AuthorityResponse['type'] | DoalDetermination['type'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION':
      switch (determinationStatus) {
        case 'VALID':
        case 'VALID_WITH_CORRECTIONS':
          return [
            {
              documentType: 'DOAL_ACCEPTED',
              filename: letterPreview,
            },
          ];
        case 'INVALID':
          return [
            {
              documentType: 'DOAL_REJECTED',
              filename: letterPreview,
            },
          ];
      }
      break;
    case 'DOAL_PROCEED_TO_AUTHORITY_AND_NOTIFY_OPERATOR_FOR_DECISION':
      switch (determinationStatus) {
        case 'PROCEED_TO_AUTHORITY':
          return [
            {
              documentType: 'DOAL_SUBMITTED',
              filename: letterPreview,
            },
          ];
      }
  }
}
