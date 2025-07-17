import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';

export function getPreviewDocumentsInfo(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'AIR_NOTIFY_OPERATOR_FOR_DECISION':
      return [
        {
          documentType: 'AIR_REVIEWED',
          filename: letterPreview,
        },
      ];
  }
}
