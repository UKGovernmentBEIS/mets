import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';

export function getPreviewDocumentsInfoVir(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'AVIATION_VIR_NOTIFY_OPERATOR_FOR_DECISION':
      return [
        {
          documentType: 'AVIATION_VIR_REVIEWED',
          filename: letterPreview,
        },
      ];
  }
}
