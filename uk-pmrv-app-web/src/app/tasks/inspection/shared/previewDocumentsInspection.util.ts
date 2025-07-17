import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';

export function getInspectionPreviewDocumentsInfo(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'INSTALLATION_ONSITE_INSPECTION_SUBMIT_NOTIFY_OPERATOR':
    case 'INSTALLATION_ONSITE_INSPECTION_SUBMIT_PEER_REVIEW_DECISION':
      return [
        {
          documentType: 'INSTALLATION_ONSITE_INSPECTION_SUBMITTED',
          filename: letterPreview,
        },
      ];
    case 'INSTALLATION_AUDIT_SUBMIT_NOTIFY_OPERATOR':
    case 'INSTALLATION_AUDIT_SUBMIT_PEER_REVIEW_DECISION':
      return [
        {
          documentType: 'INSTALLATION_AUDIT_SUBMITTED',
          filename: letterPreview,
        },
      ];
  }
}
