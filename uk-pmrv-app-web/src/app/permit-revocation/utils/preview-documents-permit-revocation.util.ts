import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';

export function getPermitRevocationPreviewDocumentsInfo(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION':
    case 'PERMIT_REVOCATION_SUBMIT_PEER_REVIEW_DECISION':
      return [
        {
          documentType: 'PERMIT_REVOCATION',
          filename: letterPreview,
        },
      ];
  }
}

export function permitRevocationDocumentPreviewRequestTaskActionTypesMap(
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskActionProcessDTO['requestTaskActionType'] {
  switch (requestTaskType) {
    case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEW':
      return 'PERMIT_REVOCATION_SUBMIT_PEER_REVIEW_DECISION';
  }
}
