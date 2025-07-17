import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';

export function getPreviewDocumentsInfoDoe(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR':
    case 'AVIATION_DOE_CORSIA_SUBMIT_PEER_REVIEW_DECISION':
      return [
        {
          documentType: 'AVIATION_DOE_SUBMITTED',
          filename: letterPreview,
        },
      ];
  }
}
