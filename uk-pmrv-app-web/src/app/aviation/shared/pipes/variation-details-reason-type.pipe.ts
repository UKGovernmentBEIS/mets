import { Pipe, PipeTransform } from '@angular/core';

import { EmpVariationUkEtsRegulatorLedReason } from 'pmrv-api';

@Pipe({
  name: 'empVariationDetailsReasonType',
  pure: true,
  standalone: true,
})
export class VariationDetailsReasonTypePipe implements PipeTransform {
  transform(type: EmpVariationUkEtsRegulatorLedReason['type']): string | null {
    switch (type) {
      case 'FOLLOWING_IMPROVING_REPORT':
        return 'Following an improvement report submitted by the aircraft operator';
      case 'FAILED_TO_COMPLY_OR_APPLY':
        return 'Aircraft operator failed to comply with a requirement in the plan, or to apply in accordance with conditions';
      case 'OTHER':
        return 'none of the above';
      default:
        return null;
    }
  }
}
