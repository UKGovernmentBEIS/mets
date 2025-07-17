import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay } from 'rxjs';

import { TotalEmissionAviationAerCorsia } from '@aviation/shared/components/aer-corsia/total-emission-aviation-aer-corsia.model';
import { getSummaryDescription } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import {
  AviationAerCorsiaAerodromePairsTotalEmissions,
  AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO,
  AviationReportingService,
} from 'pmrv-api';

import { createTablePage } from '../../../../../mi-reports/core/mi-report';

@Component({
  selector: 'app-total-emissions-corsia-aerodrome-pairs-table-template',
  templateUrl: './total-emissions-corsia-aerodrome-pairs-table-template.component.html',
  standalone: true,
  imports: [SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsCorsiaAerodromePairsTableTemplateComponent {
  private aviationReportingService = inject(AviationReportingService);
  @Input() showAllData = false;
  @Input() aerodromePairsTotalEmissions$: Observable<AviationAerCorsiaAerodromePairsTotalEmissions[]>;

  @Input() set aviationAerCorsia(aviationAerCorsia: TotalEmissionAviationAerCorsia) {
    const aviationAerCorsiaEmissionsCalculationDTO: AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO = {
      aggregatedEmissionsData: aviationAerCorsia?.aggregatedEmissionsData,
      emissionsReductionClaim:
        aviationAerCorsia?.emissionsReductionClaim?.emissionsReductionClaimDetails?.totalEmissions,
      year: aviationAerCorsia?.reportingYear,
    };
    const aerodromePairsTotalEmissions$ =
      this.aerodromePairsTotalEmissions$ ??
      this.aviationReportingService
        .getAerodromePairsEmissionsCorsia(aviationAerCorsiaEmissionsCalculationDTO)
        .pipe(shareReplay(1));
    this.pageItems$ = combineLatest([aerodromePairsTotalEmissions$, this.currentPage$]).pipe(
      map(([items, currentPage]) => (this.showAllData ? items : createTablePage(currentPage, this.pageSize, items))),
    );
    this.totalNumOfItems$ = aerodromePairsTotalEmissions$.pipe(map((items) => items?.length));
  }

  getSummaryDescription = getSummaryDescription;
  pageItems$: Observable<AviationAerCorsiaAerodromePairsTotalEmissions[]>;
  totalNumOfItems$: Observable<number>;
  currentPage$ = new BehaviorSubject<number>(1);
  pageSize = 30;
  columns: GovukTableColumn<AviationAerCorsiaAerodromePairsTotalEmissions>[] = [
    { header: 'Departure aerodrome', field: 'departureAirport' },
    { header: 'Arrival aerodrome', field: 'arrivalAirport' },
    { header: 'Number of flights', field: 'flightsNumber' },
    { header: 'Offset', field: 'offset' },
    { header: 'Emissions', field: 'emissions' },
  ];
}
