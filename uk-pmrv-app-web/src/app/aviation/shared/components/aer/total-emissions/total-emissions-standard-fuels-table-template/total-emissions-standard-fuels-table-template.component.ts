import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { Observable, shareReplay } from 'rxjs';

import { getSummaryDescription } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import {
  AviationAerEmissionsCalculationDTO,
  AviationAerUkEts,
  AviationReportingService,
  StandardFuelsTotalEmissions,
} from 'pmrv-api';

@Component({
  selector: 'app-total-emissions-standard-fuels-table-template',
  templateUrl: './total-emissions-standard-fuels-table-template.component.html',
  standalone: true,
  imports: [SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsStandardFuelsTableTemplateComponent implements OnInit {
  @Input() data: AviationAerUkEts;

  @Input() standardFuelsTotalEmissions$: Observable<StandardFuelsTotalEmissions[]>;

  getSummaryDescription = getSummaryDescription;
  aviationAerEmissionsCalculationDTO: AviationAerEmissionsCalculationDTO;
  columns: GovukTableColumn[] = [
    { header: 'Fuel type', field: 'fuelType' },
    { header: 'Emissions factor', field: 'emissionsFactor' },
    { header: 'Net calorific value', field: 'netCalorificValue' },
    { header: 'Fuel consumption', field: 'fuelConsumption' },
    { header: 'Emissions', field: 'emissions' },
  ];

  constructor(private aviationReportingService: AviationReportingService) {}

  ngOnInit() {
    if (!this.standardFuelsTotalEmissions$) {
      const emissionData = this.data.aggregatedEmissionsData;
      const safData = this.data.saf;
      this.aviationAerEmissionsCalculationDTO = {
        aggregatedEmissionsData: emissionData,
        saf: safData,
      };
      this.standardFuelsTotalEmissions$ = this.aviationReportingService
        .getStandardFuelsEmissionsUkEts(this.aviationAerEmissionsCalculationDTO)
        .pipe(shareReplay(1));
    }
  }
}
