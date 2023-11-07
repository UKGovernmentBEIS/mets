import { Pipe, PipeTransform } from '@angular/core';

import { RequestDetailsDTO } from 'pmrv-api';

@Pipe({ name: 'workflowType' })
export class WorkflowTypePipe implements PipeTransform {
  transform(type: RequestDetailsDTO['requestType']): string {
    switch (type) {
      case 'AER':
        return 'AER';
      case 'NER':
        return 'NER';
      case 'VIR':
        return 'VIR';
      case 'DRE':
        return 'DRE';
      case 'INSTALLATION_ACCOUNT_OPENING':
        return 'Account creation';
      case 'PERMIT_ISSUANCE':
        return 'Permit Application';
      case 'PERMIT_NOTIFICATION':
        return 'Permit Notification';
      case 'PERMIT_REVOCATION':
        return 'Permit Revocation';
      case 'PERMIT_SURRENDER':
        return 'Permit Surrender';
      case 'PERMIT_TRANSFER_A':
        return 'Permit Transfer';
      case 'PERMIT_TRANSFER_B':
        return 'Permit Transfer';
      case 'PERMIT_VARIATION':
        return 'Permit Variation';
      case 'SYSTEM_MESSAGE_NOTIFICATION':
        return 'System Message Notification';
      case 'NON_COMPLIANCE':
        return 'Non Compliance';
      default:
        return null;
    }
  }
}
