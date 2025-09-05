import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { HSETIRegulatorReviewOverallDecision, RequestTaskDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';

export function getHsetiPreviewDocumentsInfo(
  taskActionType: RequestTaskDTO['type'],
  determinationStatus: HSETIRegulatorReviewOverallDecision['type'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'HSE_TI_APPLICATION_REGULATOR_REVIEW_SUBMIT':
    case 'HSE_TI_APPLICATION_PEER_REVIEW':
      switch (determinationStatus) {
        case 'APPROVED':
        case 'DEEMED_WITHDRAWN':
        case 'REJECTED':
        case 'WITHDRAWN':
          return [
            {
              documentType: 'HSE_TI_COMPLETED',
              filename: letterPreview,
            },
          ];
      }
  }
}
