import { Pipe, PipeTransform } from '@angular/core';

import { ItemDTO } from 'pmrv-api';

@Pipe({ name: 'itemLink' })
export class ItemLinkPipe implements PipeTransform {
  transform(value: ItemDTO, isAviation: boolean, isWorkflow?: boolean): any[] {
    if (isWorkflow) {
      return this.transformWorkflowUrl(value, '/accounts/' + value.accountId + '/workflows/' + value.requestId + '/');
    } else {
      return this.transformWorkflowUrl(value, '/', isAviation);
    }
  }

  private transformWorkflowUrl(value: ItemDTO, routerLooks: string, isAviation?: boolean) {
    switch (value?.requestType) {
      case 'INSTALLATION_ACCOUNT_OPENING':
        return [routerLooks + 'installation-account', value.taskId];
      case 'SYSTEM_MESSAGE_NOTIFICATION':
        return isAviation ? [routerLooks + 'aviation/message', value.taskId] : [routerLooks + 'message', value.taskId];
      case 'PERMIT_ISSUANCE':
        switch (value?.taskType) {
          case 'PERMIT_ISSUANCE_WAIT_FOR_REVIEW':
          case 'PERMIT_ISSUANCE_APPLICATION_REVIEW':
          case 'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW':
          case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
          case 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS':
            return [routerLooks + 'permit-issuance', value.taskId, 'review'];
          case 'PERMIT_ISSUANCE_RFI_RESPONSE_SUBMIT':
            return [routerLooks + 'rfi', value.taskId, 'responses'];
          case 'PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE':
            return [routerLooks + 'rfi', value.taskId, 'wait'];
          case 'PERMIT_ISSUANCE_RDE_RESPONSE_SUBMIT':
            return [routerLooks + 'rde', value.taskId, 'responses'];
          case 'PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE':
            return [routerLooks + 'rde', value.taskId, 'manual-approval'];
          case 'PERMIT_ISSUANCE_MAKE_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'make'];
          case 'PERMIT_ISSUANCE_TRACK_PAYMENT':
          case 'PERMIT_ISSUANCE_CONFIRM_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'track'];
          default:
            return [routerLooks + 'permit-issuance', value.taskId];
        }
      case 'PERMIT_REVOCATION':
        switch (value?.taskType) {
          case 'PERMIT_REVOCATION_APPLICATION_SUBMIT':
          case 'PERMIT_REVOCATION_WAIT_FOR_PEER_REVIEW':
          case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'permit-revocation', value.taskId];
          case 'PERMIT_REVOCATION_WAIT_FOR_APPEAL':
            return [routerLooks + 'permit-revocation', value.taskId, 'wait-for-appeal'];
          case 'PERMIT_REVOCATION_CESSATION_SUBMIT':
            return [routerLooks + 'permit-revocation', value.taskId, 'cessation'];
          case 'PERMIT_REVOCATION_MAKE_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'make'];
          case 'PERMIT_REVOCATION_TRACK_PAYMENT':
          case 'PERMIT_REVOCATION_CONFIRM_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'track'];
          default:
            return ['.'];
        }
      case 'PERMIT_SURRENDER':
        switch (value?.taskType) {
          case 'PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW':
          case 'PERMIT_SURRENDER_APPLICATION_REVIEW':
          case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'permit-surrender', value.taskId, 'review'];
          case 'PERMIT_SURRENDER_WAIT_FOR_REVIEW':
            return [routerLooks + 'permit-surrender', value.taskId, 'review', 'wait'];
          case 'PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT':
            return [routerLooks + 'rfi', value.taskId, 'responses'];
          case 'PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE':
            return [routerLooks + 'rfi', value.taskId, 'wait'];
          case 'PERMIT_SURRENDER_RDE_RESPONSE_SUBMIT':
            return [routerLooks + 'rde', value.taskId, 'responses'];
          case 'PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE':
            return [routerLooks + 'rde', value.taskId, 'manual-approval'];
          case 'PERMIT_SURRENDER_CESSATION_SUBMIT':
            return [routerLooks + 'permit-surrender', value.taskId, 'cessation'];
          case 'PERMIT_SURRENDER_MAKE_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'make'];
          case 'PERMIT_SURRENDER_TRACK_PAYMENT':
          case 'PERMIT_SURRENDER_CONFIRM_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'track'];
          default:
            return [routerLooks + 'permit-surrender', value.taskId];
        }
      case 'PERMIT_NOTIFICATION': {
        switch (value?.taskType) {
          case 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'permit-notification', 'submit'];
          case 'PERMIT_NOTIFICATION_APPLICATION_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'permit-notification', 'review'];
          case 'PERMIT_NOTIFICATION_FOLLOW_UP':
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'permit-notification', 'follow-up'];
          case 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP':
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS':
            return [routerLooks + 'tasks', value.taskId, 'permit-notification', 'follow-up', 'wait'];
          case 'PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'permit-notification', 'peer-review-wait'];
          case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'permit-notification', 'peer-review'];
          case 'PERMIT_NOTIFICATION_WAIT_FOR_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'permit-notification', 'review-wait'];
          case 'PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT':
            return [routerLooks + 'rfi', value.taskId, 'responses'];
          case 'PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE':
            return [routerLooks + 'rfi', value.taskId, 'wait'];
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'permit-notification', 'follow-up', 'review'];
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'permit-notification', 'follow-up', 'review-wait'];
          default:
            return ['.'];
        }
      }
      case 'PERMIT_VARIATION': {
        switch (value?.taskType) {
          case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT':
            return [routerLooks + 'permit-variation', value.taskId, 'review'];
          case 'PERMIT_VARIATION_APPLICATION_SUBMIT':
          case 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT':
            return [routerLooks + 'permit-variation', value.taskId];
          case 'PERMIT_VARIATION_APPLICATION_REVIEW':
          case 'PERMIT_VARIATION_APPLICATION_PEER_REVIEW':
          case 'PERMIT_VARIATION_WAIT_FOR_REVIEW':
          case 'PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW':
          case 'PERMIT_VARIATION_WAIT_FOR_AMENDS':
          case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW':
          case 'PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'permit-variation', value.taskId, 'review'];
          case 'PERMIT_VARIATION_RFI_RESPONSE_SUBMIT':
            return [routerLooks + 'rfi', value.taskId, 'responses'];
          case 'PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE':
            return [routerLooks + 'rfi', value.taskId, 'wait'];
          case 'PERMIT_VARIATION_RDE_RESPONSE_SUBMIT':
            return [routerLooks + 'rde', value.taskId, 'responses'];
          case 'PERMIT_VARIATION_WAIT_FOR_RDE_RESPONSE':
            return [routerLooks + 'rde', value.taskId, 'manual-approval'];
          case 'PERMIT_VARIATION_MAKE_PAYMENT':
          case 'PERMIT_VARIATION_REGULATOR_LED_MAKE_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'make'];
          case 'PERMIT_VARIATION_TRACK_PAYMENT':
          case 'PERMIT_VARIATION_CONFIRM_PAYMENT':
          case 'PERMIT_VARIATION_REGULATOR_LED_TRACK_PAYMENT':
          case 'PERMIT_VARIATION_REGULATOR_LED_CONFIRM_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'track'];
          default:
            return ['.'];
        }
      }

      case 'PERMIT_TRANSFER_A': {
        switch (value?.taskType) {
          case 'PERMIT_TRANSFER_A_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'permit-transfer-a', 'submit'];
          case 'PERMIT_TRANSFER_A_WAIT_FOR_TRANSFER':
            return [routerLooks + 'tasks', value.taskId, 'permit-transfer-a', 'wait'];
          case 'PERMIT_TRANSFER_A_MAKE_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'make'];
          case 'PERMIT_TRANSFER_A_TRACK_PAYMENT':
          case 'PERMIT_TRANSFER_A_CONFIRM_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'track'];
          default:
            return ['.'];
        }
      }

      case 'PERMIT_TRANSFER_B': {
        switch (value?.taskType) {
          case 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT':
          case 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT':
            return [routerLooks + 'permit-transfer', value.taskId];
          case 'PERMIT_TRANSFER_B_MAKE_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'make'];
          case 'PERMIT_TRANSFER_B_TRACK_PAYMENT':
          case 'PERMIT_TRANSFER_B_CONFIRM_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'track'];
          case 'PERMIT_TRANSFER_B_APPLICATION_REVIEW':
          case 'PERMIT_TRANSFER_B_WAIT_FOR_REVIEW':
          case 'PERMIT_TRANSFER_B_WAIT_FOR_AMENDS':
          case 'PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW':
          case 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'permit-transfer', value.taskId, 'review'];
          case 'PERMIT_TRANSFER_B_RFI_RESPONSE_SUBMIT':
            return [routerLooks + 'rfi', value.taskId, 'responses'];
          case 'PERMIT_TRANSFER_B_WAIT_FOR_RFI_RESPONSE':
            return [routerLooks + 'rfi', value.taskId, 'wait'];
          case 'PERMIT_TRANSFER_B_RDE_RESPONSE_SUBMIT':
            return [routerLooks + 'rde', value.taskId, 'responses'];
          case 'PERMIT_TRANSFER_B_WAIT_FOR_RDE_RESPONSE':
            return [routerLooks + 'rde', value.taskId, 'manual-approval'];
          default:
            return ['.'];
        }
      }

      case 'AER': {
        switch (value?.taskType) {
          case 'AER_APPLICATION_SUBMIT':
          case 'AER_APPLICATION_AMENDS_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'aer', 'submit'];
          case 'AER_WAIT_FOR_VERIFICATION':
          case 'AER_AMEND_WAIT_FOR_VERIFICATION':
            return [routerLooks + 'tasks', value.taskId, 'aer', 'verification-wait'];
          case 'AER_WAIT_FOR_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'aer', 'review-wait'];
          case 'AER_APPLICATION_VERIFICATION_SUBMIT':
          case 'AER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'aer', 'verification-submit'];
          case 'AER_APPLICATION_REVIEW':
          case 'AER_WAIT_FOR_AMENDS':
            return [routerLooks + 'tasks', value.taskId, 'aer', 'review'];
          default:
            return ['.'];
        }
      }

      case 'VIR': {
        switch (value?.taskType) {
          case 'VIR_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'vir', 'submit'];
          case 'VIR_APPLICATION_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'vir', 'review'];
          case 'VIR_WAIT_FOR_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'vir', 'review-wait'];
          case 'VIR_RESPOND_TO_REGULATOR_COMMENTS':
            return [routerLooks + 'tasks', value.taskId, 'vir', 'comments-response'];
          case 'VIR_WAIT_FOR_RFI_RESPONSE':
            return [routerLooks + 'rfi', value.taskId, 'wait'];
          case 'VIR_RFI_RESPONSE_SUBMIT':
            return [routerLooks + 'rfi', value.taskId, 'responses'];
          default:
            return ['.'];
        }
      }

      case 'AIR': {
        switch (value?.taskType) {
          case 'AIR_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'air', 'submit'];
          case 'AIR_APPLICATION_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'air', 'review'];
          case 'AIR_WAIT_FOR_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'air', 'review-wait'];
          case 'AIR_RESPOND_TO_REGULATOR_COMMENTS':
            return [routerLooks + 'tasks', value.taskId, 'air', 'comments-response'];
          case 'AIR_WAIT_FOR_RFI_RESPONSE':
            return [routerLooks + 'rfi', value.taskId, 'wait'];
          case 'AIR_RFI_RESPONSE_SUBMIT':
            return [routerLooks + 'rfi', value.taskId, 'responses'];
          default:
            return ['.'];
        }
      }

      case 'DRE': {
        switch (value?.taskType) {
          case 'DRE_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'dre', 'submit'];
          case 'DRE_MAKE_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'make'];
          case 'DRE_TRACK_PAYMENT':
          case 'DRE_CONFIRM_PAYMENT':
            return [routerLooks + 'payment', value.taskId, 'track'];
          case 'DRE_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'dre', 'peer-review-wait'];
          case 'DRE_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'dre', 'peer-review'];
          default:
            return ['.'];
        }
      }

      case 'NON_COMPLIANCE': {
        switch (value?.taskType) {
          case 'NON_COMPLIANCE_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'non-compliance', 'submit'];
          case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
            return [routerLooks + 'tasks', value.taskId, 'non-compliance', 'daily-penalty-notice'];
          case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'non-compliance', 'daily-penalty-notice', 'peer-review-wait'];
          case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'non-compliance', 'daily-penalty-notice', 'dpn-peer-review'];
          case 'NON_COMPLIANCE_NOTICE_OF_INTENT':
            return [routerLooks + 'tasks', value.taskId, 'non-compliance', 'notice-of-intent'];
          case 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'non-compliance', 'notice-of-intent', 'noi-peer-review'];
          case 'NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'non-compliance', 'notice-of-intent', 'peer-review-wait'];
          case 'NON_COMPLIANCE_CIVIL_PENALTY':
            return [routerLooks + 'tasks', value.taskId, 'non-compliance', 'civil-penalty-notice'];
          case 'NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'non-compliance', 'civil-penalty-notice', 'peer-review-wait'];
          case 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'non-compliance', 'civil-penalty-notice', 'cpn-peer-review'];
          case 'NON_COMPLIANCE_FINAL_DETERMINATION':
            return [routerLooks + 'tasks', value.taskId, 'non-compliance', 'conclusion'];
          default:
            return ['.'];
        }
      }

      case 'PERMANENT_CESSATION': {
        switch (value?.taskType) {
          case 'PERMANENT_CESSATION_APPLICATION_SUBMIT':
          case 'PERMANENT_CESSATION_WAIT_FOR_PEER_REVIEW':
          case 'PERMANENT_CESSATION_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'permanent-cessation', 'submit'];
          default:
            return ['.'];
        }
      }

      case 'DOAL': {
        switch (value?.taskType) {
          case 'DOAL_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'doal', 'submit'];
          case 'DOAL_AUTHORITY_RESPONSE':
            return [routerLooks + 'tasks', value.taskId, 'doal', 'authority-response'];
          case 'DOAL_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'doal', 'peer-review-wait'];
          case 'DOAL_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'doal', 'peer-review'];
          default:
            return ['.'];
        }
      }

      case 'INSTALLATION_AUDIT': {
        switch (value?.taskType) {
          case 'INSTALLATION_AUDIT_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'inspection', 'audit', 'submit'];
          case 'INSTALLATION_AUDIT_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'inspection', 'audit', 'peer-review-wait'];
          case 'INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'inspection', 'audit', 'peer-review'];
          case 'INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS':
            return [routerLooks + 'tasks', value.taskId, 'inspection', 'audit', 'respond'];

          default:
            return ['.'];
        }
      }

      case 'INSTALLATION_ONSITE_INSPECTION': {
        switch (value?.taskType) {
          case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'inspection', 'onsite', 'submit'];
          case 'INSTALLATION_ONSITE_INSPECTION_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'inspection', 'onsite', 'peer-review-wait'];
          case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'inspection', 'onsite', 'peer-review'];
          case 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS':
            return [routerLooks + 'tasks', value.taskId, 'inspection', 'onsite', 'respond'];

          default:
            return ['.'];
        }
      }

      case 'WITHHOLDING_OF_ALLOWANCES': {
        switch (value?.taskType) {
          case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'withholding-allowances', 'submit'];
          case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'withholding-allowances', 'peer-review'];
          case 'WITHHOLDING_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'withholding-allowances', 'peer-review-wait'];
          case 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'withholding-allowances', 'withdraw'];

          default:
            return ['.'];
        }
      }

      case 'RETURN_OF_ALLOWANCES': {
        switch (value?.taskType) {
          case 'RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'return-of-allowances', 'submit'];
          case 'RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'return-of-allowances', 'roa-peer-review'];
          case 'RETURN_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'return-of-allowances', 'peer-review-wait'];
          case 'RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'return-of-allowances', 'returned-allowances'];
          default:
            return ['.'];
        }
      }
      case 'EMP_ISSUANCE_CORSIA':
      case 'EMP_ISSUANCE_UKETS': {
        switch (value?.taskType) {
          case 'EMP_ISSUANCE_UKETS_WAIT_FOR_REVIEW':
          case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_REVIEW':
          case 'EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS':
          case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS':
          case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT':
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT':
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
          case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
          case 'EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW':
          case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW':
          case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT':
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT':
          case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW':
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW': {
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          }
          case 'EMP_ISSUANCE_UKETS_MAKE_PAYMENT':
          case 'EMP_ISSUANCE_CORSIA_MAKE_PAYMENT':
            return ['/aviation' + routerLooks, 'payment', value.taskId, 'make'];
          case 'EMP_ISSUANCE_UKETS_TRACK_PAYMENT':
          case 'EMP_ISSUANCE_UKETS_CONFIRM_PAYMENT':
          case 'EMP_ISSUANCE_CORSIA_TRACK_PAYMENT':
          case 'EMP_ISSUANCE_CORSIA_CONFIRM_PAYMENT':
            return ['/aviation' + routerLooks, 'payment', value.taskId, 'track'];
          case 'EMP_ISSUANCE_UKETS_RFI_RESPONSE_SUBMIT':
          case 'EMP_ISSUANCE_CORSIA_RFI_RESPONSE_SUBMIT':
            return ['/aviation' + routerLooks + 'rfi', value.taskId, 'responses'];
          case 'EMP_ISSUANCE_UKETS_WAIT_FOR_RFI_RESPONSE':
          case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_RFI_RESPONSE':
            return ['/aviation' + routerLooks + 'rfi', value.taskId, 'wait'];
          case 'EMP_ISSUANCE_UKETS_RDE_RESPONSE_SUBMIT':
          case 'EMP_ISSUANCE_CORSIA_RDE_RESPONSE_SUBMIT':
            return ['/aviation' + routerLooks + 'rde', value.taskId, 'responses'];
          case 'EMP_ISSUANCE_UKETS_WAIT_FOR_RDE_RESPONSE':
          case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_RDE_RESPONSE':
            return ['/aviation' + routerLooks + 'rde', value.taskId, 'manual-approval'];
          default: {
            return ['.'];
          }
        }
      }

      case 'EMP_VARIATION_CORSIA':
      case 'EMP_VARIATION_UKETS': {
        switch (value?.taskType) {
          case 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT':
          case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT':
          case 'EMP_VARIATION_UKETS_WAIT_FOR_REVIEW':
          case 'EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW':
          case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT':
          case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT':
          case 'EMP_VARIATION_UKETS_WAIT_FOR_AMENDS':
          case 'EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS':
          case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
          case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
          case 'EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW':
          case 'EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW':
          case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW':
          case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW':
          case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
          case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
          case 'EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
          case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW':
          case 'EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
          case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'EMP_VARIATION_UKETS_RFI_RESPONSE_SUBMIT':
          case 'EMP_VARIATION_CORSIA_RFI_RESPONSE_SUBMIT':
            return ['/aviation' + routerLooks + 'rfi', value.taskId, 'responses'];
          case 'EMP_VARIATION_UKETS_WAIT_FOR_RFI_RESPONSE':
          case 'EMP_VARIATION_CORSIA_WAIT_FOR_RFI_RESPONSE':
            return ['/aviation' + routerLooks + 'rfi', value.taskId, 'wait'];
          case 'EMP_VARIATION_UKETS_RDE_RESPONSE_SUBMIT':
          case 'EMP_VARIATION_CORSIA_RDE_RESPONSE_SUBMIT':
            return ['/aviation' + routerLooks + 'rde', value.taskId, 'responses'];
          case 'EMP_VARIATION_UKETS_WAIT_FOR_RDE_RESPONSE':
          case 'EMP_VARIATION_CORSIA_WAIT_FOR_RDE_RESPONSE':
            return ['/aviation' + routerLooks + 'rde', value.taskId, 'manual-approval'];
          case 'EMP_VARIATION_UKETS_MAKE_PAYMENT':
          case 'EMP_VARIATION_UKETS_REGULATOR_LED_MAKE_PAYMENT':
          case 'EMP_VARIATION_CORSIA_REGULATOR_LED_MAKE_PAYMENT':
          case 'EMP_VARIATION_CORSIA_MAKE_PAYMENT':
            return ['/aviation' + routerLooks, 'payment', value.taskId, 'make'];
          case 'EMP_VARIATION_UKETS_TRACK_PAYMENT':
          case 'EMP_VARIATION_UKETS_CONFIRM_PAYMENT':
          case 'EMP_VARIATION_CORSIA_TRACK_PAYMENT':
          case 'EMP_VARIATION_CORSIA_CONFIRM_PAYMENT':
          case 'EMP_VARIATION_UKETS_REGULATOR_LED_TRACK_PAYMENT':
          case 'EMP_VARIATION_UKETS_REGULATOR_LED_CONFIRM_PAYMENT':
          case 'EMP_VARIATION_CORSIA_REGULATOR_LED_TRACK_PAYMENT':
          case 'EMP_VARIATION_CORSIA_REGULATOR_LED_CONFIRM_PAYMENT':
            return ['/aviation' + routerLooks, 'payment', value.taskId, 'track'];

          default: {
            return ['.'];
          }
        }
      }

      case 'AVIATION_AER_UKETS': {
        switch (value?.taskType) {
          case 'AVIATION_AER_UKETS_APPLICATION_SUBMIT':
          case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT':
          case 'AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT':
          case 'AVIATION_AER_UKETS_WAIT_FOR_VERIFICATION':
          case 'AVIATION_AER_UKETS_AMEND_WAIT_FOR_VERIFICATION':
          case 'AVIATION_AER_UKETS_APPLICATION_REVIEW':
          case 'AVIATION_AER_UKETS_WAIT_FOR_AMENDS':
          case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT':
          case 'AVIATION_AER_UKETS_WAIT_FOR_REVIEW':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];

          default: {
            return ['.'];
          }
        }
      }

      case 'AVIATION_DRE_UKETS': {
        switch (value?.taskType) {
          case 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT':
          case 'AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW':
          case 'AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_DRE_UKETS_MAKE_PAYMENT':
            return ['/aviation' + routerLooks, 'payment', value.taskId, 'make'];
          case 'AVIATION_DRE_UKETS_TRACK_PAYMENT':
          case 'AVIATION_DRE_UKETS_CONFIRM_PAYMENT':
            return ['/aviation' + routerLooks, 'payment', value.taskId, 'track'];
          default: {
            return ['.'];
          }
        }
      }

      case 'AVIATION_VIR': {
        switch (value?.taskType) {
          case 'AVIATION_VIR_APPLICATION_SUBMIT':
          case 'AVIATION_VIR_WAIT_FOR_REVIEW':
          case 'AVIATION_VIR_APPLICATION_REVIEW':
          case 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_VIR_RFI_RESPONSE_SUBMIT':
            return ['/aviation' + routerLooks + 'rfi', value.taskId, 'responses'];
          case 'AVIATION_VIR_WAIT_FOR_RFI_RESPONSE':
            return ['/aviation' + routerLooks + 'rfi', value.taskId, 'wait'];

          default: {
            return ['.'];
          }
        }
      }

      case 'AVIATION_ACCOUNT_CLOSURE': {
        switch (value?.taskType) {
          case 'AVIATION_ACCOUNT_CLOSURE_SUBMIT':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          default:
            return ['.'];
        }
      }

      case 'AVIATION_AER_CORSIA': {
        switch (value?.taskType) {
          case 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT':
          case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT':
          case 'AVIATION_AER_CORSIA_WAIT_FOR_VERIFICATION':
          case 'AVIATION_AER_CORSIA_WAIT_FOR_REVIEW':
          case 'AVIATION_AER_CORSIA_APPLICATION_REVIEW':
          case 'AVIATION_AER_CORSIA_WAIT_FOR_AMENDS':
          case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT':
          case 'AVIATION_AER_CORSIA_AMEND_WAIT_FOR_VERIFICATION':
          case 'AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];

          default: {
            return ['.'];
          }
        }
      }

      case 'AVIATION_NON_COMPLIANCE': {
        switch (value?.taskType) {
          case 'AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          default:
            return ['.'];
        }
      }

      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING': {
        switch (value?.taskType) {
          case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW':
          case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_WAIT_FOR_PEER_REVIEW':
          case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          default:
            return ['.'];
        }
      }

      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING': {
        switch (value?.taskType) {
          case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT':
          case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW':
          case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_WAIT_FOR_PEER_REVIEW':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];

          default:
            return ['.'];
        }
      }

      case 'BDR': {
        switch (value?.taskType) {
          case 'BDR_APPLICATION_SUBMIT':
          case 'BDR_APPLICATION_AMENDS_SUBMIT':
          case 'BDR_WAIT_FOR_VERIFICATION':
          case 'BDR_WAIT_FOR_REGULATOR_REVIEW':
          case 'BDR_AMEND_WAIT_FOR_VERIFICATION':
            return [routerLooks + 'tasks', value.taskId, 'bdr', 'submit'];
          case 'BDR_APPLICATION_VERIFICATION_SUBMIT':
          case 'BDR_AMEND_APPLICATION_VERIFICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'bdr', 'verification-submit'];
          case 'BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT':
          case 'BDR_WAIT_FOR_AMENDS':
          case 'BDR_APPLICATION_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'bdr', 'review'];
          case 'BDR_WAIT_FOR_PEER_REVIEW':
            return [routerLooks + 'tasks', value.taskId, 'bdr', 'peer-review-wait'];
          default:
            return ['.'];
        }
      }

      case 'AVIATION_DOE_CORSIA': {
        switch (value?.taskType) {
          case 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT':
          case 'AVIATION_DOE_CORSIA_WAIT_FOR_PEER_REVIEW':
          case 'AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW':
            return ['/aviation' + routerLooks + 'tasks', value.taskId];
          case 'AVIATION_DOE_CORSIA_MAKE_PAYMENT':
            return ['/aviation' + routerLooks, 'payment', value.taskId, 'make'];
          case 'AVIATION_DOE_CORSIA_TRACK_PAYMENT':
          case 'AVIATION_DOE_CORSIA_CONFIRM_PAYMENT':
            return ['/aviation' + routerLooks, 'payment', value.taskId, 'track'];
          default: {
            return ['.'];
          }
        }
      }

      case 'ALR': {
        switch (value?.taskType) {
          case 'ALR_APPLICATION_SUBMIT':
          case 'ALR_WAIT_FOR_VERIFICATION':
            return [routerLooks + 'tasks', value.taskId, 'alr', 'submit'];
          case 'ALR_APPLICATION_VERIFICATION_SUBMIT':
            return [routerLooks + 'tasks', value.taskId, 'alr', 'verification-submit'];
          default: {
            return ['.'];
          }
        }
      }

      default:
        return ['.'];
    }
  }
}
