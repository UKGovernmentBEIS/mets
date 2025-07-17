import { Pipe, PipeTransform } from '@angular/core';

import { AviationDoECorsiaDeterminationReason } from 'pmrv-api';

const DETERMINATION_REASON_TYPE_SELECTION = {
  VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED: 'A verified emissions report has not been submitted',
  CORRECTIONS_TO_A_VERIFIED_REPORT: 'We are making corrections to a verified report',
};

@Pipe({
  name: 'doeDeterminationReasonType',
  pure: true,
  standalone: true,
})
export class DoeDeterminationReasonTypePipe implements PipeTransform {
  transform(value: AviationDoECorsiaDeterminationReason['type']): string | null {
    if (value == null) {
      return null;
    }

    return DETERMINATION_REASON_TYPE_SELECTION[value] ?? null;
  }
}
