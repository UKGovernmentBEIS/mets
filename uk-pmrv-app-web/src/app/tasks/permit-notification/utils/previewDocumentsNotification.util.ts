import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { PermitNotificationReviewDecision, RequestTaskActionProcessDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';

export function getPreviewDocumentsInfo(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  determinationStatus: PermitNotificationReviewDecision['type'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION':
      switch (determinationStatus) {
        case 'ACCEPTED':
        case 'PERMANENT_CESSATION':
        case 'TEMPORARY_CESSATION':
        case 'CESSATION_TREATED_AS_PERMANENT':
        case 'NOT_CESSATION':
          return [{ documentType: 'PERMIT_NOTIFICATION_ACCEPTED', filename: letterPreview }];
        case 'REJECTED':
          return [{ documentType: 'PERMIT_NOTIFICATION_REFUSED', filename: letterPreview }];
      }
      break;
  }
}
