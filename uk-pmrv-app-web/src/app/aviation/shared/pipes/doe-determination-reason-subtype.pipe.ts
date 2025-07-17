import { Pipe, PipeTransform } from '@angular/core';

const DETERMINATION_REASON_SUBTYPE_SELECTION = {
  CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS: 'Correcting total emissions on all international flights',
  CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS:
    'Correcting emissions on flights with offsetting requirements',
  CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS:
    'Correcting emissions related to a claim from CORSIA Eligible Fuels',
};

@Pipe({
  name: 'doeDeterminationReasonSubType',
  pure: true,
  standalone: true,
})
export class DoeDeterminationReasonSubTypePipe implements PipeTransform {
  transform(
    value:
      | 'CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS'
      | 'CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS'
      | 'CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS',
  ): string | null {
    if (value == null) {
      return null;
    }

    return DETERMINATION_REASON_SUBTYPE_SELECTION[value] ?? null;
  }
}
