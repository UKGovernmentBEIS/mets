import { ReviewDeterminationStatus } from '@permit-application/review/types/review.permit.type';
import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import {
  PermitIssuanceApplicationReviewRequestTaskPayload,
  PreviewDocumentRequest,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';
const permitPreview = 'permit_preview.pdf';
const letterDocumentTypes: PreviewDocumentRequest['documentType'][] = [
  'PERMIT_VARIATION_REGULATOR_LED_APPROVED',
  'PERMIT_VARIATION_ACCEPTED',
  'PERMIT_VARIATION_REJECTED',
  'PERMIT_VARIATION_DEEMED_WITHDRAWN',
  'PERMIT_ISSUANCE_GHGE_ACCEPTED',
  'PERMIT_ISSUANCE_HSE_ACCEPTED',
  'PERMIT_ISSUANCE_WASTE_ACCEPTED',
  'PERMIT_ISSUANCE_REJECTED',
  'PERMIT_ISSUANCE_DEEMED_WITHDRAWN',
  'PERMIT_TRANSFER_ACCEPTED',
  'PERMIT_TRANSFER_REFUSED',
  'PERMIT_TRANSFER_DEEMED_WITHDRAWN',
];

const permitDocumentTypes: PreviewDocumentRequest['documentType'][] = ['PERMIT'];

export function getPreviewDocumentsInfo(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] | RequestTaskDTO['type'],
  determinationStatus: ReviewDeterminationStatus,
  permitType?: PermitIssuanceApplicationReviewRequestTaskPayload['permitType'],
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED':
      return [buildPreviewInfo('PERMIT_VARIATION_REGULATOR_LED_APPROVED'), buildPreviewInfo('PERMIT')];

    case 'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION':
      switch (determinationStatus) {
        case 'approved':
          return [buildPreviewInfo('PERMIT_VARIATION_ACCEPTED'), buildPreviewInfo('PERMIT')];
        case 'rejected':
          return [buildPreviewInfo('PERMIT_VARIATION_REJECTED')];
        case 'deemed withdrawn':
          return [buildPreviewInfo('PERMIT_VARIATION_DEEMED_WITHDRAWN')];
      }
      break;

    case 'PERMIT_VARIATION_APPLICATION_PEER_REVIEW':
      switch (determinationStatus) {
        case 'approved':
          return [buildPreviewInfo('PERMIT_VARIATION_ACCEPTED'), buildPreviewInfo('PERMIT')];
        case 'rejected':
          return [buildPreviewInfo('PERMIT_VARIATION_REJECTED')];
        case 'deemed withdrawn':
          return [buildPreviewInfo('PERMIT_VARIATION_DEEMED_WITHDRAWN')];
      }
      break;

    case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
    case 'PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION':
      switch (determinationStatus) {
        case 'granted':
          switch (permitType) {
            case 'GHGE':
              return [buildPreviewInfo('PERMIT_ISSUANCE_GHGE_ACCEPTED'), buildPreviewInfo('PERMIT')];
            case 'HSE':
              return [buildPreviewInfo('PERMIT_ISSUANCE_HSE_ACCEPTED'), buildPreviewInfo('PERMIT')];
            case 'WASTE':
              return [buildPreviewInfo('PERMIT_ISSUANCE_WASTE_ACCEPTED'), buildPreviewInfo('PERMIT')];
          }
          break;
        case 'rejected':
          return [buildPreviewInfo('PERMIT_ISSUANCE_REJECTED')];
        case 'deemed withdrawn':
          return [buildPreviewInfo('PERMIT_ISSUANCE_DEEMED_WITHDRAWN')];
      }
      break;

    case 'PERMIT_TRANSFER_B_NOTIFY_OPERATOR_FOR_DECISION':
      switch (determinationStatus) {
        case 'granted':
          return [buildPreviewInfo('PERMIT_TRANSFER_ACCEPTED'), buildPreviewInfo('PERMIT')];
        case 'rejected':
          return [buildPreviewInfo('PERMIT_TRANSFER_REFUSED')];
        case 'deemed withdrawn':
          return [buildPreviewInfo('PERMIT_TRANSFER_DEEMED_WITHDRAWN')];
      }
      break;
  }
}

function buildPreviewInfo(documentType: PreviewDocumentRequest['documentType']): DocumentFilenameAndDocumentType {
  return {
    documentType,
    filename: getFilename(documentType),
  };
}

function getFilename(documentType: PreviewDocumentRequest['documentType']) {
  if (letterDocumentTypes.includes(documentType)) {
    return letterPreview;
  } else if (permitDocumentTypes.includes(documentType)) {
    return permitPreview;
  }
}
