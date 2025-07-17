import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';

export function getPreviewDocumentsInfo(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'DRE_SUBMIT_NOTIFY_OPERATOR':
    case 'DRE_SUBMIT_PEER_REVIEW_DECISION':
      return [
        {
          documentType: 'DRE_SUBMITTED',
          filename: letterPreview,
        },
      ];
  }
}
