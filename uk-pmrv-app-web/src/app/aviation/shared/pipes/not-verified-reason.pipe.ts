import { Pipe, PipeTransform } from '@angular/core';

import { AviationAerNotVerifiedDecisionReason } from 'pmrv-api';

@Pipe({ name: 'notVerifiedDecisionReasonType', standalone: true })
export class NotVerifiedDecisionReasonTypePipe implements PipeTransform {
  transform(value: AviationAerNotVerifiedDecisionReason['type'], details?: string): string {
    switch (value) {
      case 'UNCORRECTED_MATERIAL_MISSTATEMENT':
        return 'An uncorrected material misstatement (individual or in aggregate)';
      case 'UNCORRECTED_MATERIAL_NON_CONFORMITY':
        return 'An uncorrected material non-conformity (individual or in aggregate)';
      case 'VERIFICATION_DATA_OR_INFORMATION_LIMITATIONS':
        return 'Limitations in the data or information made available for verification - ' + details;
      case 'SCOPE_LIMITATIONS_DUE_TO_LACK_OF_CLARITY':
        return 'Limitations of scope due to lack of clarity - ' + details;
      case 'SCOPE_LIMITATIONS_OF_APPROVED_MONITORING_PLAN':
        return 'Limitations of scope of the approved monitoring plan - ' + details;
      case 'NOT_APPROVED_MONITORING_PLAN_BY_REGULATOR':
        return 'The emissions monitoring plan is not approved by the regulator - ' + details;
      case 'ANOTHER_REASON':
        return 'Another reason - ' + details;
      default:
        return '';
    }
  }
}
