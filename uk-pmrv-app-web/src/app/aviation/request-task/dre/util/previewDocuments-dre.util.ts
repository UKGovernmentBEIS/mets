import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';

export function getPreviewDocumentsInfoDre(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'AVIATION_DRE_UKETS_SUBMIT_PEER_REVIEW_DECISION':
    case 'AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR':
      return [
        {
          documentType: 'AVIATION_DRE_SUBMITTED',
          filename: letterPreview,
        },
      ];
  }
}
