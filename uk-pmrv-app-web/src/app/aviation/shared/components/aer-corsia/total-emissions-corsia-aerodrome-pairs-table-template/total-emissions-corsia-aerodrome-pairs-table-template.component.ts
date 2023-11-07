import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay } from 'rxjs';

import { getSummaryDescription } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import {
  AviationAerCorsia,
  AviationAerCorsiaAerodromePairsTotalEmissions,
  AviationAerCorsiaEmissionsCalculationDTO,
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

  @Input() set aviationAerCorsia(aviationAerCorsia: AviationAerCorsia) {
    const aviationAerCorsiaEmissionsCalculationDTO: AviationAerCorsiaEmissionsCalculationDTO = {
      aggregatedEmissionsData: aviationAerCorsia?.aggregatedEmissionsData,
      emissionsReductionClaim:
        aviationAerCorsia?.emissionsReductionClaim?.emissionsReductionClaimDetails?.totalEmissions,
    };
    const aerodromePairsTotalEmissions$ = this.aviationReportingService
      .getAerodromePairsEmissionsCorsia(aviationAerCorsiaEmissionsCalculationDTO)
      .pipe(shareReplay(1));
    this.pageItems$ = combineLatest([aerodromePairsTotalEmissions$, this.currentPage$]).pipe(
      map(([items, currentPage]) => createTablePage(currentPage, this.pageSize, items)),
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
