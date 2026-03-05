import { Pipe, PipeTransform } from '@angular/core';

import { AviationDreDeterminationReason } from 'pmrv-api';

const DETERMINATION_REASON_TYPE_SELECTION = {
  VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER: 'A report that was not submitted according to the Order',
  CORRECTING_NON_MATERIAL_MISSTATEMENT: 'Correcting a non-material misstatement',
  IMPOSING_OR_CONSIDERING_IMPOSING_CIVIL_PENALTY_IN_ACCORDANCE_WITH_ORDER:
    'Imposing or considering imposing a civil penalty according to the Order',
};

@Pipe({
  name: 'determinationReasonType',
  pure: true,
  standalone: true,
})
export class DeterminationReasonTypePipe implements PipeTransform {
  transform(value: AviationDreDeterminationReason['type']): string | null {
    if (value == null) {
      return null;
    }

    return DETERMINATION_REASON_TYPE_SELECTION[value] ?? null;
  }
}
