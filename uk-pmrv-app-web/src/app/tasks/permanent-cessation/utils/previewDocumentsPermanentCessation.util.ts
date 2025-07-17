import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { RequestTaskDTO } from 'pmrv-api';

export function getPermanentCessationPreviewDocumentsInfo(
  taskType: RequestTaskDTO['type'],
): DocumentFilenameAndDocumentType[] {
  switch (taskType) {
    case 'PERMANENT_CESSATION_APPLICATION_SUBMIT':
      return [
        {
          documentType: 'PERMANENT_CESSATION_APPLICATION_SUBMIT',
          filename: 'letter_preview.pdf',
        },
      ];

    case 'PERMANENT_CESSATION_APPLICATION_PEER_REVIEW':
      return [
        {
          documentType: 'PERMANENT_CESSATION_APPLICATION_PEER_REVIEW',
          filename: 'letter_preview.pdf',
        },
      ];
  }
}
