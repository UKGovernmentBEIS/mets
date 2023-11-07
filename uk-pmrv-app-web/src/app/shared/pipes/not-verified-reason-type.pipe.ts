import { Pipe, PipeTransform } from '@angular/core';

import { NotVerifiedReason } from 'pmrv-api';

@Pipe({ name: 'notVerifiedReasonType' })
export class NotVerifiedReasonTypePipe implements PipeTransform {
  transform(value: NotVerifiedReason['type'], otherReason?: string): string {
    switch (value) {
      case 'ANOTHER_REASON':
        return otherReason;
      case 'DATA_OR_INFORMATION_LIMITATIONS':
        return 'limitations in the data or information made available for verification';
      case 'NOT_APPROVED_MONITORING_PLAN':
        return 'the monitoring plan is not approved by the competent authority';
      case 'SCOPE_LIMITATIONS_CLARITY':
        return 'limitations of scope due to lack of clarity';
      case 'SCOPE_LIMITATIONS_MONITORING_PLAN':
        return 'limitations of scope of the approved monitoring plan';
      case 'UNCORRECTED_MATERIAL_MISSTATEMENT':
        return 'uncorrected material mis-statment (individual or in aggregate)';
      case 'UNCORRECTED_MATERIAL_NON_CONFORMITY':
        return 'uncorrected material non-comformity (individual or in aggregate)';
      default:
        return '';
    }
  }
}
