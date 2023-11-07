import { Pipe, PipeTransform } from '@angular/core';

import { AviationDreEmissionsCalculationApproach } from 'pmrv-api';

const AVIATION_DRE_EMISSIONS_SELECTION = {
  EUROCONTROL_SUPPORT_FACILITY: 'Eurocontrol Support Facility',
  VERIFIED_ANNUAL_EMISSIONS_REPORT_SUBMITTED_LATE: 'a verified annual emissions report that was submitted late',
  OTHER_DATASOURCE: 'another data source',
};

@Pipe({
  name: 'aviationDreEmissionsType',
  pure: true,
  standalone: true,
})
export class AviationDreEmissionsTypePipe implements PipeTransform {
  transform(value: AviationDreEmissionsCalculationApproach['type']): string | null {
    if (value == null) {
      return null;
    }

    return AVIATION_DRE_EMISSIONS_SELECTION[value] ?? null;
  }
}
