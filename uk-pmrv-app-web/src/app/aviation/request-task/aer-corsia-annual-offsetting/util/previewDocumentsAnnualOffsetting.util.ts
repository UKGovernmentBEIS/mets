import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

const letterPreview = 'letter preview.pdf';

export function getPreviewDocumentsInfoAnnualOffsetting(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION':
    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_NOTIFY_OPERATOR':
      return [
        {
          documentType: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMITTED',
          filename: letterPreview,
        },
      ];
  }
}
