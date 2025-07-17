import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';

import { Observable, shareReplay } from 'rxjs';

import { getSummaryDescription } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import {
  AviationAerCorsia,
  AviationAerCorsiaEmissionsCalculationDTO,
  AviationAerCorsiaStandardFuelsTotalEmissions,
  AviationReportingService,
} from 'pmrv-api';

@Component({
  selector: 'app-total-emissions-corsia-standard-fuels-table-template',
  templateUrl: './total-emissions-corsia-standard-fuels-table-template.component.html',
  standalone: true,
  imports: [CommonModule, GovukComponentsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsCorsiaStandardFuelsTableTemplateComponent {
  private aviationReportingService = inject(AviationReportingService);

  @Input() standardFuelsTotalEmissions$: Observable<AviationAerCorsiaStandardFuelsTotalEmissions[]>;

  @Input() set aviationAerCorsia(aviationAerCorsia: AviationAerCorsia) {
    if (!this.standardFuelsTotalEmissions$) {
      const aviationAerCorsiaEmissionsCalculationDTO: AviationAerCorsiaEmissionsCalculationDTO = {
        aggregatedEmissionsData: aviationAerCorsia?.aggregatedEmissionsData,
        emissionsReductionClaim:
          aviationAerCorsia?.emissionsReductionClaim?.emissionsReductionClaimDetails?.totalEmissions,
      };
      this.standardFuelsTotalEmissions$ = this.aviationReportingService
        .getStandardFuelsEmissionsCorsia(aviationAerCorsiaEmissionsCalculationDTO)
        .pipe(shareReplay(1));
    }
  }

  getSummaryDescription = getSummaryDescription;
  columns: GovukTableColumn<AviationAerCorsiaStandardFuelsTotalEmissions>[] = [
    { header: 'Fuel type', field: 'fuelType' },
    { header: 'Emissions factor', field: 'emissionsFactor' },
    { header: 'Fuel consumption', field: 'fuelConsumption' },
    { header: 'Emissions', field: 'emissions' },
  ];
}
