import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay } from 'rxjs';

import { getSummaryDescription } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import {
  AerodromePairsTotalEmissions,
  AviationAerEmissionsCalculationDTO,
  AviationAerUkEts,
  AviationReportingService,
} from 'pmrv-api';

import { createTablePage } from '../../../../../../mi-reports/core/mi-report';

@Component({
  selector: 'app-total-emissions-aerodrome-pairs-table-template',
  templateUrl: './total-emissions-aerodrome-pairs-table-template.component.html',
  standalone: true,
  imports: [SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsAerodromePairsTableTemplateComponent implements OnInit {
  @Input() data: AviationAerUkEts;

  getSummaryDescription = getSummaryDescription;
  aviationAerEmissionsCalculationDTO: AviationAerEmissionsCalculationDTO;
  aerodromePairsTotalEmissions$: Observable<AerodromePairsTotalEmissions[]>;
  pageItems$: Observable<AerodromePairsTotalEmissions[]>;
  totalNumOfItems$: Observable<number>;
  currentPage$ = new BehaviorSubject<number>(1);
  pageSize = 30;
  columns: GovukTableColumn[] = [
    { header: 'Departure aerodrome', field: 'departureAirport' },
    { header: 'Arrival aerodrome', field: 'arrivalAirport' },
    { header: 'Number of flights', field: 'flightsNumber' },
    { header: 'Emissions  (t CO2)', field: 'emissions' },
  ];

  constructor(private aviationReportingService: AviationReportingService) {}

  ngOnInit() {
    const emissionData = this.data.aggregatedEmissionsData;
    const safData = this.data.saf;
    this.aviationAerEmissionsCalculationDTO = {
      aggregatedEmissionsData: emissionData,
      saf: safData,
    };
    this.aerodromePairsTotalEmissions$ = this.aviationReportingService
      .getAerodromePairsEmissionsUkEts(this.aviationAerEmissionsCalculationDTO)
      .pipe(shareReplay(1));
    this.pageItems$ = combineLatest([this.aerodromePairsTotalEmissions$, this.currentPage$]).pipe(
      map(([items, currentPage]) => createTablePage(currentPage, this.pageSize, items)),
    );
    this.totalNumOfItems$ = this.aerodromePairsTotalEmissions$.pipe(map((items) => items?.length));
  }
}
