import { Pipe, PipeTransform } from '@angular/core';

import { RequestDetailsDTO } from 'pmrv-api';

@Pipe({ name: 'workflowType' })
export class WorkflowTypePipe implements PipeTransform {
  transform(type: RequestDetailsDTO['requestType']): string {
    switch (type) {
      // ######################## COMMON ########################
      case 'SYSTEM_MESSAGE_NOTIFICATION':
        return 'System Message Notification';

      // ######################## INSTALLATION ########################
      case 'INSTALLATION_ACCOUNT_OPENING':
        return 'Account creation';

      case 'PERMIT_ISSUANCE':
        return 'Permit Application';
      case 'PERMIT_SURRENDER':
        return 'Permit Surrender';
      case 'PERMIT_REVOCATION':
        return 'Permit Revocation';
      case 'PERMIT_TRANSFER_A':
      case 'PERMIT_TRANSFER_B':
        return 'Permit Transfer';
      case 'PERMIT_VARIATION':
        return 'Permit Variation';
      case 'PERMIT_NOTIFICATION':
        return 'Permit Notification';
      case 'PERMIT_REISSUE':
        return 'Batch variation';

      case 'NON_COMPLIANCE':
        return 'Non-compliance';

      case 'NER':
        return 'New Entrant Reserve';
      case 'DOAL':
        return 'Determination of activity level';
      case 'AER':
        return 'Emissions report';
      case 'VIR':
        return 'Verifier improvement';
      case 'AIR':
        return 'Annual improvement';
      case 'DRE':
        return 'Determine emissions';

      case 'WITHHOLDING_OF_ALLOWANCES':
        return 'Withholding of allowances';
      case 'RETURN_OF_ALLOWANCES':
        return 'Return of allowances';

      // ######################## AVIATION ########################
      case 'AVIATION_ACCOUNT_CLOSURE':
        return 'Account closure';

      case 'AVIATION_NON_COMPLIANCE':
        return 'Non-compliance';

      case 'EMP_REISSUE':
        return 'Batch variation';
      case 'EMP_ISSUANCE_CORSIA':
      case 'EMP_ISSUANCE_UKETS':
        return 'EMP Application';
      case 'EMP_VARIATION_CORSIA':
      case 'EMP_VARIATION_UKETS':
        return 'Variation';

      case 'AVIATION_AER_CORSIA':
      case 'AVIATION_AER_UKETS':
        return 'Emissions report';
      case 'AVIATION_DRE_UKETS':
        return 'Determine emissions';
      case 'AVIATION_VIR':
        return 'Verifier improvement';

      default:
        return null;
    }
  }
}
