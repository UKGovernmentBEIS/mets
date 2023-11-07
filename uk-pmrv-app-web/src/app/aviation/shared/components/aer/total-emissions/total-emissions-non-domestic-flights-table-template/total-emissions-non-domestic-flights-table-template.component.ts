import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay } from 'rxjs';

import { getSummaryDescription } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import {
  AviationAerEmissionsCalculationDTO,
  AviationAerNonDomesticFlightsEmissions,
  AviationAerNonDomesticFlightsEmissionsDetails,
  AviationAerUkEts,
  AviationReportingService,
} from 'pmrv-api';

import { createTablePage } from '../../../../../../mi-reports/core/mi-report';

@Component({
  selector: 'app-total-emissions-non-domestic-flights-table-template',
  templateUrl: './total-emissions-non-domestic-flights-table-template.component.html',
  standalone: true,
  imports: [SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsNonDomesticFlightsTableTemplateComponent implements OnInit {
  @Input() data: AviationAerUkEts;

  getSummaryDescription = getSummaryDescription;
  aviationAerEmissionsCalculationDTO: AviationAerEmissionsCalculationDTO;
  nonDomesticFlightsTotalEmissions$: Observable<AviationAerNonDomesticFlightsEmissions>;
  pageItems$: Observable<AviationAerNonDomesticFlightsEmissionsDetails[]>;
  totalNumOfItems$: Observable<number>;
  currentPage$ = new BehaviorSubject<number>(1);
  pageSize = 30;
  columns: GovukTableColumn[] = [
    { header: 'Departure state', field: 'departureCountry' },
    { header: 'Arrival state', field: 'arrivalCountry' },
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
    this.nonDomesticFlightsTotalEmissions$ = this.aviationReportingService
      .getNonDomesticFlightsEmissionsUkEts(this.aviationAerEmissionsCalculationDTO)
      .pipe(shareReplay(1));
    this.pageItems$ = combineLatest([this.nonDomesticFlightsTotalEmissions$, this.currentPage$]).pipe(
      map(([emissionsObj, currentPage]) => {
        const items = emissionsObj.nonDomesticFlightsEmissionsDetails || [];
        return createTablePage(currentPage, this.pageSize, items);
      }),
    );

    this.totalNumOfItems$ = this.nonDomesticFlightsTotalEmissions$.pipe(
      map((emissionsObj) => emissionsObj?.nonDomesticFlightsEmissionsDetails?.length || 0),
    );
  }
}
