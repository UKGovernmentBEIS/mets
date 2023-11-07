import { RequestInfoDTO, RequestTaskActionProcessDTO } from 'pmrv-api';

const relatedRequestTaskActions: Array<RequestTaskActionProcessDTO['requestTaskActionType']> = [
  'RFI_SUBMIT',
  'RDE_SUBMIT',
  'PERMIT_ISSUANCE_RECALL_FROM_AMENDS',
  'PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS',
  'RFI_CANCEL',
  'PERMIT_NOTIFICATION_CANCEL_APPLICATION',
  'PERMIT_VARIATION_CANCEL_APPLICATION',
  'PERMIT_REVOCATION_CANCEL_APPLICATION',
  'PERMIT_SURRENDER_CANCEL_APPLICATION',
  'PERMIT_VARIATION_RECALL_FROM_AMENDS',
  'AER_RECALL_FROM_VERIFICATION',
  'PERMIT_TRANSFER_CANCEL_APPLICATION',
  'PERMIT_TRANSFER_B_RECALL_FROM_AMENDS',
  'DRE_CANCEL_APPLICATION',
  'WITHHOLDING_OF_ALLOWANCES_CANCEL_APPLICATION',
  'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_CLOSE_APPLICATION',
  'NON_COMPLIANCE_CLOSE_APPLICATION',
  'NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION',
  'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION',
  'DOAL_CANCEL_APPLICATION',
  'AVIATION_ACCOUNT_CLOSURE_CANCEL_APPLICATION',
  'AVIATION_DRE_CANCEL_APPLICATION',
  'EMP_VARIATION_CANCEL_APPLICATION',
  'EMP_ISSUANCE_UKETS_RECALL_FROM_AMENDS',
  'RETURN_OF_ALLOWANCES_CANCEL_APPLICATION',
  'AVIATION_AER_RECALL_FROM_VERIFICATION',
  'EMP_VARIATION_UKETS_RECALL_FROM_AMENDS',
  'EMP_ISSUANCE_CORSIA_RECALL_FROM_AMENDS',
  'EMP_VARIATION_CORSIA_RECALL_FROM_AMENDS',
  'AVIATION_AER_UKETS_SKIP_REVIEW',
];

export function hasRelatedViewActions(type: RequestInfoDTO['type']) {
  return ['VIR', 'AVIATION_VIR'].includes(type);
}

export function hasRequestTaskAllowedActions(
  allowedRequestTaskActions: Array<RequestTaskActionProcessDTO['requestTaskActionType']>,
) {
  return allowedRequestTaskActions?.some((action) => relatedRequestTaskActions.includes(action));
}

export function requestTaskAllowedActions(
  allowedRequestTaskActions: Array<RequestTaskActionProcessDTO['requestTaskActionType']>,
  taskId: number,
  isAviation?: boolean,
  isWorkflow?: boolean,
  requestInfo?: RequestInfoDTO,
) {
  return (
    allowedRequestTaskActions
      ?.filter((action) => relatedRequestTaskActions.includes(action))
      .map((action) =>
        actionDetails(action, taskId, isAviation ? '/aviation' : '', isWorkflow ? './' : '/', requestInfo),
      ) ?? []
  );
}

function actionDetails(
  action: RequestTaskActionProcessDTO['requestTaskActionType'],
  taskId: number,
  isAviation?: string,
  routerLooks?: string,
  requestInfo?: RequestInfoDTO,
) {
  switch (action) {
    case 'RFI_SUBMIT':
      return { text: 'Request for information', link: [isAviation + routerLooks + 'rfi', taskId, 'questions'] };

    case 'RDE_SUBMIT':
      return {
        text: 'Request deadline extension',
        link: [isAviation + routerLooks + 'rde', taskId, 'extend-determination'],
      };

    case 'PERMIT_ISSUANCE_RECALL_FROM_AMENDS':
      return { text: 'Recall the permit', link: ['recall-from-amends'] };

    case 'PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS':
      return { text: 'Recall your response', link: ['recall-from-amends'] };

    case 'RFI_CANCEL':
      return { text: 'Cancel request', link: [isAviation + routerLooks + 'rfi', taskId, 'cancel-verify'] };

    case 'DOAL_CANCEL_APPLICATION':
      return { text: 'Cancel task', link: ['cancel'] };

    case 'PERMIT_REVOCATION_CANCEL_APPLICATION':
      return { text: 'Cancel task', link: ['cancel'] };

    case 'PERMIT_NOTIFICATION_CANCEL_APPLICATION':
    case 'PERMIT_VARIATION_CANCEL_APPLICATION':
    case 'PERMIT_SURRENDER_CANCEL_APPLICATION':
    case 'PERMIT_TRANSFER_CANCEL_APPLICATION':
    case 'DRE_CANCEL_APPLICATION':
    case 'WITHHOLDING_OF_ALLOWANCES_CANCEL_APPLICATION':
    case 'RETURN_OF_ALLOWANCES_CANCEL_APPLICATION':
      return { text: 'Cancel task', link: [routerLooks + 'tasks', taskId, 'cancel'] };

    case 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_CLOSE_APPLICATION':
      return {
        text: 'Close task',
        link: [routerLooks + 'tasks', taskId, 'withholding-allowances', 'withdraw', 'close'],
      };

    case 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION':
      return {
        text: 'Provide note on appeal',
        link: ['/accounts', requestInfo.accountId, 'workflows', requestInfo.id],
        fragment: 'notes',
      };

    case 'PERMIT_VARIATION_RECALL_FROM_AMENDS':
      return { text: 'Recall the permit variation', link: ['recall-from-amends'] };

    case 'AER_RECALL_FROM_VERIFICATION':
      return { text: 'Recall AER application from verifier', link: ['recall'] };

    case 'PERMIT_TRANSFER_B_RECALL_FROM_AMENDS':
      return { text: 'Recall the transfer', link: ['recall-from-amends'] };

    case 'NON_COMPLIANCE_CLOSE_APPLICATION':
      return { text: 'Close task', link: [isAviation + routerLooks + 'tasks', taskId, 'non-compliance', 'close'] };

    case 'NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION':
      return {
        text: 'Provide note on appeal',
        link: ['/accounts', requestInfo.accountId, 'workflows', requestInfo.id],
        fragment: 'notes',
      };

    case 'AVIATION_ACCOUNT_CLOSURE_CANCEL_APPLICATION':
    case 'AVIATION_DRE_CANCEL_APPLICATION':
      return { text: 'Cancel this task', link: ['/aviation' + routerLooks + 'tasks', taskId, 'cancel'] };

    case 'EMP_VARIATION_CANCEL_APPLICATION':
      return { text: 'Cancel task', link: ['/aviation' + routerLooks + 'tasks', taskId, 'cancel'] };

    case 'EMP_ISSUANCE_UKETS_RECALL_FROM_AMENDS':
    case 'EMP_ISSUANCE_CORSIA_RECALL_FROM_AMENDS':
      return {
        text: 'Recall the application',
        link: ['/aviation' + routerLooks + 'tasks', taskId, 'recall-from-amends'],
      };

    case 'AVIATION_AER_RECALL_FROM_VERIFICATION':
      return { text: 'Recall report from verifier', link: ['recall-report-from-verifier'] };

    case 'EMP_VARIATION_UKETS_RECALL_FROM_AMENDS':
    case 'EMP_VARIATION_CORSIA_RECALL_FROM_AMENDS':
      return {
        text: 'Recall the application',
        link: ['/aviation' + routerLooks + 'tasks', taskId, 'recall-from-amends'],
      };

    case 'AVIATION_AER_UKETS_SKIP_REVIEW':
      return {
        text: 'Skip review and complete report',
        link: [`/aviation/tasks/${taskId}/skip-review`],
      };

    default:
      return null;
  }
}
