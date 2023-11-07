import { AsyncPipe, NgIf, NgSwitch, NgSwitchCase } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { Observable, shareReplay } from 'rxjs';

import { getSummaryDescription } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import {
  AviationAerDomesticFlightsEmissions,
  AviationAerEmissionsCalculationDTO,
  AviationAerUkEts,
  AviationReportingService,
} from 'pmrv-api';

@Component({
  selector: 'app-total-emissions-domestic-flights-table-template',
  templateUrl: './total-emissions-domestic-flights-table-template.component.html',
  standalone: true,
  imports: [AsyncPipe, NgSwitch, NgIf, NgSwitchCase, SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsDomesticFlightsTableTemplateComponent implements OnInit {
  @Input() data: AviationAerUkEts;

  getSummaryDescription = getSummaryDescription;
  aviationAerEmissionsCalculationDTO: AviationAerEmissionsCalculationDTO;
  domesticFlightsTotalEmissions$: Observable<AviationAerDomesticFlightsEmissions>;
  columns: GovukTableColumn[] = [
    { header: 'State', field: 'country' },
    { header: 'Number of flights', field: 'flightsNumber' },
    { header: 'Fuel used', field: 'fuelType' },
    { header: 'Fuel consumption (t)', field: 'fuelConsumption' },
    { header: 'Emissions (tCO2)', field: 'emissions' },
  ];

  constructor(private aviationReportingService: AviationReportingService) {}

  ngOnInit() {
    const emissionData = this.data.aggregatedEmissionsData;
    const safData = this.data.saf;
    this.aviationAerEmissionsCalculationDTO = {
      aggregatedEmissionsData: emissionData,
      saf: safData,
    };
    this.domesticFlightsTotalEmissions$ = this.aviationReportingService
      .getDomesticFlightsEmissionsUkEts(this.aviationAerEmissionsCalculationDTO)
      .pipe(shareReplay(1));
  }
}
