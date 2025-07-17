import { ReviewDeterminationStatus } from '@permit-application/review/types/review.permit.type';
import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { PreviewDocumentRequest, RequestTaskActionProcessDTO } from 'pmrv-api';

const letterPreview = 'letter_preview.pdf';
const empPreview = 'emissions_monitoring_plan_preview.pdf';
const letterDocumentTypes: PreviewDocumentRequest['documentType'][] = [
  'EMP_ISSUANCE_UKETS_GRANTED',
  'EMP_ISSUANCE_UKETS_DEEMED_WITHDRAWN',
  'EMP_ISSUANCE_CORSIA_GRANTED',
  'EMP_ISSUANCE_CORSIA_DEEMED_WITHDRAWN',
  'EMP_VARIATION_UKETS_ACCEPTED',
  'EMP_VARIATION_UKETS_REJECTED',
  'EMP_VARIATION_UKETS_DEEMED_WITHDRAWN',
  'EMP_VARIATION_UKETS_REGULATOR_LED_APPROVED',
  'EMP_VARIATION_CORSIA_ACCEPTED',
  'EMP_VARIATION_CORSIA_REJECTED',
  'EMP_VARIATION_CORSIA_DEEMED_WITHDRAWN',
  'EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED',
];

const empDocumentTypes: PreviewDocumentRequest['documentType'][] = ['EMP_UKETS', 'EMP_CORSIA'];

export function getPreviewDocumentsInfoEmp(
  taskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  determinationStatus: ReviewDeterminationStatus | 'withdrawn',
): DocumentFilenameAndDocumentType[] {
  switch (taskActionType) {
    case 'EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION':
    case 'EMP_ISSUANCE_UKETS_SAVE_REVIEW_DETERMINATION':
      switch (determinationStatus) {
        case 'approved':
          return [buildPreviewInfo('EMP_ISSUANCE_UKETS_GRANTED'), buildPreviewInfo('EMP_UKETS')];
        case 'withdrawn':
          return [buildPreviewInfo('EMP_ISSUANCE_UKETS_DEEMED_WITHDRAWN')];
      }
      break;
    case 'EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION':
      switch (determinationStatus) {
        case 'approved':
          return [buildPreviewInfo('EMP_ISSUANCE_CORSIA_GRANTED'), buildPreviewInfo('EMP_CORSIA')];
        case 'withdrawn':
          return [buildPreviewInfo('EMP_ISSUANCE_CORSIA_DEEMED_WITHDRAWN')];
      }
      break;
    case 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION':
      switch (determinationStatus) {
        case 'approved':
          return [buildPreviewInfo('EMP_VARIATION_UKETS_ACCEPTED'), buildPreviewInfo('EMP_UKETS')];
        case 'rejected':
          return [buildPreviewInfo('EMP_VARIATION_UKETS_REJECTED')];
        case 'withdrawn':
          return [buildPreviewInfo('EMP_VARIATION_UKETS_DEEMED_WITHDRAWN')];
      }
      break;
    case 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED':
      return [buildPreviewInfo('EMP_VARIATION_UKETS_REGULATOR_LED_APPROVED'), buildPreviewInfo('EMP_UKETS')];
    case 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION':
      switch (determinationStatus) {
        case 'approved':
          return [buildPreviewInfo('EMP_VARIATION_CORSIA_ACCEPTED'), buildPreviewInfo('EMP_CORSIA')];
        case 'rejected':
          return [buildPreviewInfo('EMP_VARIATION_CORSIA_REJECTED')];
        case 'withdrawn':
          return [buildPreviewInfo('EMP_VARIATION_CORSIA_DEEMED_WITHDRAWN')];
      }
      break;
    case 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED':
      return [buildPreviewInfo('EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED'), buildPreviewInfo('EMP_CORSIA')];
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
  } else if (empDocumentTypes.includes(documentType)) {
    return empPreview;
  }
}
