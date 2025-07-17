import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { PermitSurrenderReviewDetermination, PreviewDocumentRequest, RequestTaskDTO } from 'pmrv-api';

const letterPreview = 'Letter_preview.pdf';

export function getPreviewDocumentsInfoSurrender(
  taskActionType: RequestTaskDTO['type'],
  determinationStatus: PermitSurrenderReviewDetermination['type'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEW':
      switch (determinationStatus) {
        case 'GRANTED':
          return buildPreviewInfo('PERMIT_SURRENDERED_NOTICE');

        case 'REJECTED':
          return buildPreviewInfo('PERMIT_SURRENDER_REJECTED_NOTICE');

        case 'DEEMED_WITHDRAWN':
          return buildPreviewInfo('PERMIT_SURRENDER_DEEMED_WITHDRAWN_NOTICE');
      }
  }
}

const buildPreviewInfo = (type: PreviewDocumentRequest['documentType']) => {
  return [
    {
      documentType: type,
      filename: letterPreview,
    },
  ];
};
